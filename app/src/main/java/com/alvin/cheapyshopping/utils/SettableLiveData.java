package com.alvin.cheapyshopping.utils;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Transformations;

/**
 * Created by Alvin on 5/12/2017.
 */

public class SettableLiveData<T> extends MediatorLiveData<T> {

    private final LiveData<T> mSource;
    private boolean mSourceAttached;

    public SettableLiveData(LiveData<T> source) {
        mSource = source;
        attachSource();
    }

    private void attachSource() {
        if (!mSourceAttached) {
            mSourceAttached = true;
            addSource(mSource, super::setValue);
        }
    }

    private void detachSource() {
        if (mSourceAttached) {
            mSourceAttached = false;
            removeSource(mSource);
        }
    }

    @Override
    public void setValue(T value) {
        if (mSourceAttached) {
            detachSource();
        }
        super.setValue(value);
    }

    @Override
    public void postValue(T value) {
        if (mSourceAttached) {
            detachSource();
        }
        super.postValue(value);
    }

    public void reset() {
        if (!mSourceAttached) {
            attachSource();
        }
    }
}
