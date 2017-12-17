package com.alvin.cheapyshopping.utils;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Alvin on 18/12/2017.
 */

public class Promise<X> {
    
    public class Handler {

        private Handler() {}
        
        public void resolve(@Nullable X value) {
            if (!mConsumed) {
                mConsumed = true;
                mResolveValue.postValue(value);
            }
        }
        
        public void reject(@NonNull Throwable value) {
            if (!mConsumed) {
                mConsumed = true;
                mRejectValue.postValue(value);
            }
        }
        
    }

    public interface Executor<X> {

        void exec(@NonNull Promise<X>.Handler handler) throws Throwable;

    }

    public interface OnFulfilledHandler<X, Y> {

        @NonNull
        Promise<Y> onFulfilled(@Nullable X value) throws Throwable;

    }

    public interface OnRejectedHandler<Y> {

        @NonNull
        Promise<Y> onRejected(@NonNull Throwable exception) throws Throwable;

    }


    @NonNull
    public static <X> Promise<X> resolve(@Nullable X value) {
        return new Promise<>(handler -> handler.resolve(value));
    }

    @NonNull
    public static <X> Promise<X> reject(@NonNull Throwable value) {
        return new Promise<>(handler -> handler.reject(value));
    }


    private final MutableLiveData<X> mResolveValue = new MutableLiveData<>();
    private final MutableLiveData<Throwable> mRejectValue = new MutableLiveData<>();
    private boolean mConsumed = false;


    public Promise(Executor<X> executor) {
        Handler handler = new Handler();
        try {
            executor.exec(handler);
        } catch (Throwable e) {
            handler.reject(e);
        }
    }

    @NonNull
    public <Y> Promise<Y> onFulfill(@NonNull LifecycleOwner owner,
                                    @NonNull OnFulfilledHandler<X, Y> onFulfilledHandler) {
        return onResult(owner, onFulfilledHandler, null, e -> { throw e; });
    }

    @NonNull
    public Promise<X> onReject(@NonNull LifecycleOwner owner,
                               @NonNull OnRejectedHandler<X> onRejectedHandler) {
        return onResult(null, Promise::resolve, owner, onRejectedHandler);
    }

    @NonNull
    public <Y> Promise<Y> onResult(@NonNull LifecycleOwner owner,
                                   @NonNull OnFulfilledHandler<X, Y> onFulfilledHandler,
                                   @NonNull OnRejectedHandler<Y> onRejectedHandler) {
        return onResult(owner, onFulfilledHandler, owner, onRejectedHandler);
    }

    @NonNull
    private <Y> Promise<Y> onResult(@Nullable LifecycleOwner fulfillOwner,
                                    @NonNull OnFulfilledHandler<X, Y> onFulfilledHandler,
                                    @Nullable LifecycleOwner rejectOwner,
                                    @NonNull OnRejectedHandler<Y> onRejectedHandler) {
        return new Promise<>(new Executor<Y>() {

            private Observer<X> mResolveObserver;
            private Observer<Throwable> mRejectObserver;

            @Override
            public void exec(@NonNull Promise<Y>.Handler handler) {

                mResolveObserver = new Observer<X>() {

                    private Observer<Y> mmResolveObserver;
                    private Observer<Throwable> mmRejectObserver;

                    @Override
                    public void onChanged(@Nullable X v) {
                        mResolveValue.removeObserver(mResolveObserver);
                        mRejectValue.removeObserver(mRejectObserver);

                        try {
                            Promise<Y> next = onFulfilledHandler.onFulfilled(v);

                            next.mResolveValue.observeForever(mmResolveObserver = value -> {
                                next.mResolveValue.removeObserver(mmResolveObserver);
                                next.mRejectValue.removeObserver(mmRejectObserver);
                                handler.resolve(value);
                            });

                            next.mRejectValue.observeForever(mmRejectObserver = value -> {
                                if (value == null) {
                                    Log.e("Promise", "Unexpected null value");
                                    return;
                                }
                                next.mResolveValue.removeObserver(mmResolveObserver);
                                next.mRejectValue.removeObserver(mmRejectObserver);
                                handler.reject(value);
                            });

                        } catch (Throwable e) {
                            handler.reject(e);
                        }
                    }
                };

                mRejectObserver = new Observer<Throwable>() {

                    private Observer<Y> mmResolveObserver;
                    private Observer<Throwable> mmRejectObserver;

                    @Override
                    public void onChanged(@Nullable Throwable v) {
                        if (v == null) {
                            Log.e("Promise", "Unexpected null value");
                            return;
                        }
                        mResolveValue.removeObserver(mResolveObserver);
                        mRejectValue.removeObserver(mRejectObserver);

                        try {
                            Promise<Y> next = onRejectedHandler.onRejected(v);

                            next.mResolveValue.observeForever(mmResolveObserver = value -> {
                                next.mResolveValue.removeObserver(mmResolveObserver);
                                next.mRejectValue.removeObserver(mmRejectObserver);
                                handler.resolve(value);
                            });

                            next.mRejectValue.observeForever(mmRejectObserver = value -> {
                                if (value == null) {
                                    Log.e("Promise", "Unexpected null value");
                                    return;
                                }
                                next.mResolveValue.removeObserver(mmResolveObserver);
                                next.mRejectValue.removeObserver(mmRejectObserver);
                                handler.reject(value);
                            });

                        } catch (Throwable e) {
                            handler.reject(e);
                        }
                    }
                };

                if (fulfillOwner == null) {
                    mResolveValue.observeForever(mResolveObserver);
                } else {
                    mResolveValue.observe(fulfillOwner, mResolveObserver);
                }

                if (rejectOwner == null) {
                    mRejectValue.observeForever(mRejectObserver);
                } else {
                    mRejectValue.observe(rejectOwner, mRejectObserver);
                }
            }
        });
    }

}
