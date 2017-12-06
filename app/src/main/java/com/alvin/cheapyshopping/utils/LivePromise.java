package com.alvin.cheapyshopping.utils;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;

/**
 * Created by Alvin on 1/12/2017.
 */

public class LivePromise<X, Y> {

    private final MutableLiveData<X> mResolveData;

    private final MutableLiveData<Y> mRejectData;

    @SuppressWarnings("WeakerAccess")
    protected LivePromise() {
        this.mResolveData = new MutableLiveData<>();
        this.mRejectData = new MutableLiveData<>();
    }

    public void observeResolve(LifecycleOwner owner, Observer<X> observer) {
        this.mResolveData.observe(owner, observer);
    }

    public void observeForeverResolve(Observer<X> observer) {
        mResolveData.observeForever(observer);
    }

    public void observeReject(LifecycleOwner owner, Observer<Y> observer) {
        this.mRejectData.observe(owner, observer);
    }

    public void observeForeverReject(Observer<Y> observer) {
        mRejectData.observeForever(observer);
    }

    public X getResolveValue() {
        return this.mResolveData.getValue();
    }

    protected void setResolveValue(X resolveValue) {
        this.mResolveData.setValue(resolveValue);
    }

    protected void postResolveValue(X resolveValue) {
        this.mResolveData.postValue(resolveValue);
    }

    public Y getRejectValue() {
        return this.mRejectData.getValue();
    }

    protected void setRejectValue(Y rejectValue) {
        this.mRejectData.setValue(rejectValue);
    }

    protected void postRejectValue(Y rejectValue) {
        this.mRejectData.postValue(rejectValue);
    }

}
