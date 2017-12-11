package com.alvin.cheapyshopping.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.gms.maps.SupportMapFragment;

/**
 * Created by Alvin on 27/11/2017.
 * References:
 * https://stackoverflow.com/questions/30525066/how-to-set-google-map-fragment-inside-scroll-view
 */

public class NestedSupportMapFragment extends SupportMapFragment {

    private static class InterceptableFrameLayout extends FrameLayout {

        private InterceptableFrameLayout(Context context) {
            super(context);
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent ev) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_UP:
                    this.requestDisallowInterceptTouchEvent(true);
            }
            return super.dispatchTouchEvent(ev);
        }
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View view = super.onCreateView(layoutInflater, viewGroup, bundle);

        if (view instanceof ViewGroup) {
            ((ViewGroup) view).addView(
                    new InterceptableFrameLayout(this.getContext()),
                    new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                    )
            );
        }

        return view;
    }
}
