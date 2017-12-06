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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.alvin.cheapyshopping.MainActivity;
import com.alvin.cheapyshopping.databinding.BottomSheetFragmentBinding;

import java.util.List;

public class BottomSheetFragment extends Fragment {

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
            setBottomSheetContentFragmentInfo(activity.getBottomSheetContentFragmentInfo());
        }
    }



    private MainActivity.BottomSheetContentFragmentInfo mBottomSheetContentFragmentInfo;

    public void setBottomSheetContentFragmentInfo(MainActivity.BottomSheetContentFragmentInfo info) {
        if (mBottomSheetContentFragmentInfo != info) {
            mBottomSheetContentFragmentInfo = info;

            if (info != null) {
                Fragment fragment = getChildFragmentManager().findFragmentByTag(info.getTag());
                if (fragment == null) {
                    getChildFragmentManager().beginTransaction()
                            .replace(
                                    mBinding.fragmentContainer.getId(),
                                    info.getFragmentCreator().createFragment(),
                                    info.getTag()
                            ).commit();
                }

            } else {
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
        if (mBehavior.getPeekHeight() != peekHeight) {
            mBehavior.setPeekHeight(peekHeight);
        }
    }

    private void setHideable(boolean hideable) {
        if (mBehavior.isHideable() != hideable) {
            // Fix for weird animation
            getView().post(() -> mBehavior.setHideable(hideable));
//            mBehavior.setHideable(hideable);
        }
    }

    private void setState(@BottomSheetBehavior.State int state) {
        if (mBehavior.getState() != state) {
//            getView().post(() -> mBehavior.setState(state));
            mBehavior.setState(state);
        }
    }

    private int mMaxWidth;
    private View.OnLayoutChangeListener mPendingLayoutChangeListener;
    private void setMaxWidth(int maxWidth) {
        if (mMaxWidth != maxWidth) {
            mMaxWidth = maxWidth;

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
