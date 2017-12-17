package com.alvin.cheapyshopping.utils;

/**
 * Created by Alvin on 1/12/2017.
 */

public class MutableLivePromise0<X, Y> extends LivePromise0<X, Y> {

    public MutableLivePromise0() {
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
