package com.alvin.cheapyshopping.utils;

/**
 * Created by Alvin on 1/12/2017.
 */

public class MutableLivePromise<X, Y> extends LivePromise<X, Y> {

    public MutableLivePromise() {
        super();
    }

    @Override
    public void setResolveValue(X resolveValue) {
        super.setResolveValue(resolveValue);
    }

    @Override
    public void postResolveValue(X resolveValue) {
        super.postResolveValue(resolveValue);
    }

    @Override
    public void setRejectValue(Y rejectValue) {
        super.setRejectValue(rejectValue);
    }

    @Override
    public void postRejectValue(Y rejectValue) {
        super.postRejectValue(rejectValue);
    }
}
