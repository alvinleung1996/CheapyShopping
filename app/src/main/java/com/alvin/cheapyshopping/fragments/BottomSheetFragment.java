package com.alvin.cheapyshopping.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alvin.cheapyshopping.databinding.BottomSheetFragmentBinding;

/**
 * Created by Alvin on 1/12/2017.
 */

public class BottomSheetFragment extends Fragment {

    public static class Behavior extends BottomSheetBehavior<View> {

        @SuppressWarnings("unused")
        public Behavior() {
            super();
        }

        @SuppressWarnings("unused")
        public Behavior(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
            return dependency instanceof AppBarLayout
                    || super.layoutDependsOn(parent, child, dependency);
        }

        @Override
        public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
            boolean changed = super.onDependentViewChanged(parent, child, dependency);
            if (dependency instanceof AppBarLayout) {
                float progress = -dependency.getY() / dependency.getHeight();
                float translateY = progress * child.getHeight();
                child.setTranslationY(translateY);
                changed = true;
            }
            return changed;
        }
    }

    private BottomSheetFragmentBinding mBinding;
    private Behavior mBehavior;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.mBinding = BottomSheetFragmentBinding.inflate(inflater, container, false);
        // Prevent touch event from passing through to the underlying views
        this.mBinding.getRoot().setOnTouchListener((view, motionEvent) -> true);
        return this.mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.mBehavior = (Behavior) BottomSheetBehavior.from(this.mBinding.getRoot());
    }



    /*
    ************************************************************************************************
    * Public interfaces
    ************************************************************************************************
     */

    public void hide() {
        this.mBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    public void show() {
        this.mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    public CoordinatorLayout.LayoutParams getLayoutParams() {
        return (CoordinatorLayout.LayoutParams) this.getView().getLayoutParams();
    }

    public void setLayoutParams(CoordinatorLayout.LayoutParams layoutParams) {
        this.getView().setLayoutParams(layoutParams);
    }

    public boolean isHideable() {
        return this.mBehavior.isHideable();
    }

    public void setHideable(boolean hideable) {
        this.mBehavior.setHideable(hideable);
    }

    public Fragment getContentFragment(String tag) {
        return this.getChildFragmentManager().findFragmentByTag(tag);
    }

    public int getPeekHeight() {
        return this.mBehavior.getPeekHeight();
    }

    public void setPeekHeight(int peekHeight) {
        this.mBehavior.setPeekHeight(peekHeight);
    }

    public void setContentFragment(Fragment fragment, String tag) {
        this.getChildFragmentManager().beginTransaction()
                .replace(this.mBinding.fragmentContainer.getId(), fragment, tag)
                .commit();
    }
}
