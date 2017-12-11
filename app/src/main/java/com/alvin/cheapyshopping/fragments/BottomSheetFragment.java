package com.alvin.cheapyshopping.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.alvin.cheapyshopping.MainActivity;
import com.alvin.cheapyshopping.databinding.BottomSheetFragmentBinding;

import java.util.List;

public class BottomSheetFragment extends BaseFragment {

    public static class ContentFragment extends BaseFragment {

        private int mPeekHeight;

        public int getPeekHeight() {
            return mPeekHeight;
        }

        public void setPeekHeight(int peekHeight) {
            if (mPeekHeight != peekHeight) {
                mPeekHeight = peekHeight;

                BottomSheetFragment parent = (BottomSheetFragment) getParentFragment();
                if (parent != null) {
                    parent.setPeekHeight(peekHeight);
                }
            }
        }


        private boolean mHideable;

        public boolean isHideable() {
            return mHideable;
        }

        public void setHideable(boolean hideable) {
            if (mHideable != hideable) {
                mHideable = hideable;

                BottomSheetFragment parent = (BottomSheetFragment) getParentFragment();
                if (parent != null) {
                    parent.setHideable(hideable);
                }
            }
        }

        @BottomSheetBehavior.State
        private int mState;

        @BottomSheetBehavior.State
        public int getState() {
            return mState;
        }

        public void setState(@BottomSheetBehavior.State int state) {
            if (mState != state) {
                mState = state;

                BottomSheetFragment parent = (BottomSheetFragment) getParentFragment();
                if (parent != null) {
                    parent.setState(state);
                }
            }
        }


        private int mMaxWidth;

        public int getMaxWidth() {
            return mMaxWidth;
        }

        public void setMaxWidth(int maxWidth) {
            if (mMaxWidth != maxWidth) {
                mMaxWidth = maxWidth;

                BottomSheetFragment parent = (BottomSheetFragment) getParentFragment();
                if (parent != null) {
                    parent.setMaxWidth(maxWidth);
                }
            }
        }
    }

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
                float translateY = progress * Math.max(parent.getHeight() - child.getTop(), 0);
                child.setTranslationY(translateY);
                changed = true;
            }
            return changed;
        }
    }



    private BottomSheetFragmentBinding mBinding;
    private Behavior mBehavior;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = BottomSheetFragmentBinding.inflate(inflater, container, false);
        // Prevent touch event from passing through to the underlying views
        mBinding.getRoot().setOnTouchListener((view, motionEvent) -> {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_UP:
                    view.performClick();
                    break;
            }
            return true;
        });
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getChildFragmentManager().registerFragmentLifecycleCallbacks(new FragmentLifecycleCallbacks(), false);
        mBehavior = (Behavior) BottomSheetBehavior.from(mBinding.getRoot());
    }

    @Override
    public void onStart() {
        super.onStart();
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            setBottomSheetContentFragmentOptions(activity.getBottomSheetContentFragmentOptions());
        }
    }



    public static class BottomSheetContentFragmentOptions {

        private final MainActivity.FragmentCreator<ContentFragment> mFragmentCreator;
        private final String mTag;

        public BottomSheetContentFragmentOptions(MainActivity.FragmentCreator<ContentFragment> fragmentCreator, String tag) {
            mFragmentCreator = fragmentCreator;
            mTag = tag;
        }

        public MainActivity.FragmentCreator<ContentFragment> getFragmentCreator() {
            return mFragmentCreator;
        }

        public String getTag() {
            return mTag;
        }
    }

    private BottomSheetContentFragmentOptions mBottomSheetContentFragmentOptions;
    private boolean mBottomSheetContentFragmentOptionsApplied;

    public void setBottomSheetContentFragmentOptions(BottomSheetContentFragmentOptions options) {
        // When both mInfo and info are null, we cannot tell whether they have changed,
        // Maybe this fragment has just initialized and mInfo is null.
        // If we just perform mInfo != info test, the side effect will be missed
        // for the first time of the setting right after the initialization
        if (mBottomSheetContentFragmentOptions != options || !mBottomSheetContentFragmentOptionsApplied) {
            mBottomSheetContentFragmentOptions = options;
            mBottomSheetContentFragmentOptionsApplied = true;

            if (options != null) {
                Fragment fragment = getChildFragmentManager().findFragmentByTag(options.getTag());
                if (fragment == null) {
                    getChildFragmentManager().beginTransaction()
                            .replace(
                                    mBinding.fragmentContainer.getId(),
                                    options.getFragmentCreator().createFragment(),
                                    options.getTag()
                            ).commit();
                }

            } else {
                getChildFragmentManager().executePendingTransactions();
                List<Fragment> attachedFragments = getChildFragmentManager().getFragments();
                if (attachedFragments.size() > 0) {
                    FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                    for (Fragment f : attachedFragments) {
                        transaction.remove(f);
                    }
                    transaction.commit();
                }
                setPeekHeight(0);
                setHideable(true);
                setState(BottomSheetBehavior.STATE_HIDDEN);
                setMaxWidth(0);
            }
        }
    }


    private class FragmentLifecycleCallbacks extends FragmentManager.FragmentLifecycleCallbacks {

        @Override
        public void onFragmentStarted(FragmentManager fm, Fragment f) {
            super.onFragmentStarted(fm, f);

            ContentFragment contentFragment = (ContentFragment) f;

            setPeekHeight(contentFragment.getPeekHeight());
            setHideable(contentFragment.isHideable());
            setState(contentFragment.getState());
            setMaxWidth(contentFragment.getMaxWidth());
        }
    }



    /*
    ************************************************************************************************
    * Public interfaces
    ************************************************************************************************
     */

    private void setPeekHeight(int peekHeight) {
        mBehavior.setPeekHeight(peekHeight);
    }

    private void setHideable(boolean hideable) {
        // Fix for weird animation
        getView().post(() -> mBehavior.setHideable(hideable));
    }

    private void setState(@BottomSheetBehavior.State int state) {
        mBehavior.setState(state);
    }

    private int mMaxWidth;
    private boolean mMaxWidthApplied;
    private View.OnLayoutChangeListener mPendingLayoutChangeListener;
    private void setMaxWidth(int maxWidth) {
        if (mMaxWidth != maxWidth || !mMaxWidthApplied) {
            mMaxWidth = maxWidth;
            mMaxWidthApplied = true;

            //noinspection ConstantConditions
            @NonNull View view = getView();

            if (mPendingLayoutChangeListener != null) {
                view.removeOnLayoutChangeListener(mPendingLayoutChangeListener);
                mPendingLayoutChangeListener = null;
            }

            mPendingLayoutChangeListener = new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    v.removeOnLayoutChangeListener(this);
                    mPendingLayoutChangeListener = null;

                    CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) v.getLayoutParams();
                    int parentWidth = ((ViewGroup) v.getParent()).getWidth();
                    layoutParams.width = maxWidth > 0 && parentWidth > maxWidth ? maxWidth : ViewGroup.LayoutParams.MATCH_PARENT;
                    v.setLayoutParams(layoutParams);
                }
            };
            view.addOnLayoutChangeListener(mPendingLayoutChangeListener);
        }
    }
}
