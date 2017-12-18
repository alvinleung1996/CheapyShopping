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

public class Promise<T> {
    
    public class Handler {

        private Handler() {}
        
        public void resolve(@Nullable T value) {
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

    @FunctionalInterface
    public interface Executor<T> {
        void exec(@NonNull Promise<? super T>.Handler handler) throws Throwable;
    }

    @FunctionalInterface
    public interface OnResolvedHandler<T, R> {
        @Nullable
        R onResolved(@Nullable T value) throws Throwable;
    }

    @FunctionalInterface
    public interface OnResolvedPromiseHandler<T, R> {
        @NonNull
        Promise<R> onResolvedPromise(@Nullable T value) throws Throwable;
    }

    @FunctionalInterface
    public interface OnRejectedHandler<T, R> {
        @Nullable
        R onRejected(@NonNull T exception) throws Throwable;
    }

    @FunctionalInterface
    public interface OnRejectedPromiseHandler<T, R> {
        @NonNull
        Promise<R> onRejectedPromise(@NonNull T exception) throws Throwable;
    }

    @FunctionalInterface
    public interface OnFinallyHandler {
        void onFinally() throws Throwable;
    }


    @NonNull
    public static <T> Promise<T> resolve(@Nullable T value) {
        return new Promise<>(handler -> handler.resolve(value));
    }

    @NonNull
    public static <T> Promise<T> reject(@NonNull Throwable value) {
        return new Promise<>(handler -> handler.reject(value));
    }


    private final MutableLiveData<T> mResolveValue = new MutableLiveData<>();
    private final MutableLiveData<Throwable> mRejectValue = new MutableLiveData<>();
    private boolean mConsumed = false;


    public Promise(Executor<? extends T> executor) {
        Handler handler = new Handler();
        try {
            executor.exec(handler);
        } catch (Throwable e) {
            handler.reject(e);
        }
    }

    @NonNull
    public <R> Promise<R> onResolved(@NonNull LifecycleOwner owner,
                                     @NonNull OnResolvedHandler<? super T, ? extends R> onResolvedHandler) {
        return onResolvedPromise(owner, v -> Promise.resolve(onResolvedHandler.onResolved(v)));
    }

    @NonNull
    public <R> Promise<R> onResolvedPromise(@NonNull LifecycleOwner owner,
                                            @NonNull OnResolvedPromiseHandler<? super T, ? extends R> onResolvedHandler) {
        return onResultPromise(owner, onResolvedHandler, null, e -> { throw e; });
    }

    @NonNull
    public Promise<T> onRejected(@NonNull LifecycleOwner owner,
                                 @NonNull OnRejectedHandler<? super Throwable, ? extends T> onRejectedHandler) {
        return onRejectedPromise(owner, v -> Promise.resolve(onRejectedHandler.onRejected(v)));
    }

    @NonNull
    public Promise<T> onRejectedPromise(@NonNull LifecycleOwner owner,
                                        @NonNull OnRejectedPromiseHandler<? super Throwable, ? extends T> onRejectedHandler) {
        return onResultPromise(null, Promise::resolve, owner, onRejectedHandler);
    }

    @NonNull
    public Promise<T> onFinally(@NonNull LifecycleOwner owner,
                                @NonNull OnFinallyHandler onFinallyHandler) {
        return onResult(owner, v -> {
            onFinallyHandler.onFinally();
            return v;
        }, v -> {
            onFinallyHandler.onFinally();
            throw v;
        });
    }

    @NonNull
    public <R> Promise<R> onResult(@NonNull LifecycleOwner owner,
                                   @NonNull OnResolvedHandler<? super T, ? extends R> onResolvedHandler,
                                   @NonNull OnRejectedHandler<? super Throwable, ? extends R> onRejectedHandler) {
        return onResultPromise(owner,
                v -> Promise.resolve(onResolvedHandler.onResolved(v)),
                v -> Promise.resolve(onRejectedHandler.onRejected(v)));
    }

    @NonNull
    public <R> Promise<R> onResultPromise(@NonNull LifecycleOwner owner,
                                           @NonNull OnResolvedPromiseHandler<? super T, ? extends R> onResolvedHandler,
                                           @NonNull OnRejectedPromiseHandler<? super Throwable, ? extends R> onRejectedHandler) {
        return onResultPromise(owner, onResolvedHandler, owner, onRejectedHandler);
    }


    @NonNull
    private <R, Rs extends R, Rj extends R> Promise<R> onResultPromise(
            @Nullable LifecycleOwner fulfillOwner,
            @NonNull OnResolvedPromiseHandler<? super T, Rs> onResolvedHandler,
            @Nullable LifecycleOwner rejectOwner,
            @NonNull OnRejectedPromiseHandler<? super Throwable, Rj> onRejectedHandler) {
        return new Promise<>(new Executor<R>() {

            private Observer<T> mResolveObserver;
            private Observer<Throwable> mRejectObserver;

            @Override
            public void exec(@NonNull Promise<? super R>.Handler handler) {

                mResolveObserver = new Observer<T>() {

                    private Observer<Rs> mmResolveObserver;
                    private Observer<Throwable> mmRejectObserver;

                    @Override
                    public void onChanged(@Nullable T v) {
                        mResolveValue.removeObserver(mResolveObserver);
                        mRejectValue.removeObserver(mRejectObserver);

                        try {
                            Promise<Rs> next = onResolvedHandler.onResolvedPromise(v);

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

                    private Observer<Rj> mmResolveObserver;
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
                            Promise<Rj> next = onRejectedHandler.onRejectedPromise(v);

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
