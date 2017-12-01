package com.alvin.cheapyshopping.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alvin.cheapyshopping.databinding.BottomSheetFragmentBinding;

/**
 * Created by Alvin on 1/12/2017.
 */

public class BottomSheetFragment extends Fragment {

    private BottomSheetFragmentBinding mBinding;
    private BottomSheetBehavior mBottomSheetBehavior;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.mBinding = BottomSheetFragmentBinding.inflate(inflater, container, false);
        this.mBinding.getRoot().setOnTouchListener((view, motionEvent) -> true);
        return this.mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.mBottomSheetBehavior = BottomSheetBehavior.from(this.mBinding.getRoot());
    }



    /*
    ************************************************************************************************
    * Public interfaces
    ************************************************************************************************
     */

    public void hide() {
        this.mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    public void show() {
        this.mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    public CoordinatorLayout.LayoutParams getLayoutParams() {
        return (CoordinatorLayout.LayoutParams) this.getView().getLayoutParams();
    }

    public void setLayoutParams(CoordinatorLayout.LayoutParams layoutParams) {
        this.getView().setLayoutParams(layoutParams);
    }

    public boolean isHideable() {
        return this.mBottomSheetBehavior.isHideable();
    }

    public void setHideable(boolean hideable) {
        this.mBottomSheetBehavior.setHideable(hideable);
    }

    public Fragment getContentFragment(String tag) {
        return this.getChildFragmentManager().findFragmentByTag(tag);
    }

    public int getPeekHeight() {
        return this.mBottomSheetBehavior.getPeekHeight();
    }

    public void setPeekHeight(int peekHeight) {
        this.mBottomSheetBehavior.setPeekHeight(peekHeight);
    }

    public void setContentFragment(Fragment fragment, String tag) {
        this.getChildFragmentManager().beginTransaction()
                .replace(this.mBinding.fragmentContainer.getId(), fragment, tag)
                .commit();
    }
}
