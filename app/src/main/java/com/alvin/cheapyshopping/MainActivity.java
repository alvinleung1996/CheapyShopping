package com.alvin.cheapyshopping;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.alvin.cheapyshopping.databinding.DrawerHeaderBinding;
import com.alvin.cheapyshopping.databinding.MainActivityBinding;
import com.alvin.cheapyshopping.db.entities.Account;
import com.alvin.cheapyshopping.fragments.AccountFragment;
import com.alvin.cheapyshopping.fragments.BaseFragment;
import com.alvin.cheapyshopping.fragments.BottomSheetFragment;
import com.alvin.cheapyshopping.fragments.ProductListFragment;
import com.alvin.cheapyshopping.fragments.ShoppingListFragment;
import com.alvin.cheapyshopping.fragments.StoreListFragment;
import com.alvin.cheapyshopping.utils.ImageRotater;
import com.alvin.cheapyshopping.utils.OnClickListener;
import com.alvin.cheapyshopping.viewmodels.MainActivityViewModel;

import java.io.File;
import java.lang.ref.WeakReference;


public class MainActivity extends BaseActivity {

    public static abstract class MainFragment extends BaseFragment {

        private String mFragmentTitle;

        public String getFragmentTitle() {
            return mFragmentTitle;
        }

        public void setFragmentTitle(String fragmentTitle) {
            if (fragmentTitle == null && mFragmentTitle != null || fragmentTitle != null && !fragmentTitle.equals(mFragmentTitle)) {
                mFragmentTitle = fragmentTitle;

                MainActivity activity = (MainActivity) getActivity();
                if (activity != null) {
                    activity.setFragmentTitle(fragmentTitle);
                }
            }
        }

        private FloatingActionButtonOptions mFloatingActionButtonOptions;

        public FloatingActionButtonOptions getFloatingActionButtonOptions() {
            return mFloatingActionButtonOptions;
        }

        public void setFloatingActionButtonOptions(FloatingActionButtonOptions info) {
            if (mFloatingActionButtonOptions != info) {
                mFloatingActionButtonOptions = info;

                MainActivity activity = (MainActivity) getActivity();
                if (activity != null) {
                    activity.setFloatingActionButtonOptions(info);
                }
            }
        }


        private BottomSheetFragment.BottomSheetContentFragmentOptions mBottomSheetContentFragmentOptions;

        public BottomSheetFragment.BottomSheetContentFragmentOptions getBottomSheetContentFragmentOptions() {
            return mBottomSheetContentFragmentOptions;
        }

        public void setBottomSheetContentFragment(BottomSheetFragment.BottomSheetContentFragmentOptions info) {
            if (mBottomSheetContentFragmentOptions != info) {
                mBottomSheetContentFragmentOptions = info;

                MainActivity activity = (MainActivity) getActivity();
                if (activity != null) {
                    activity.setBottomSheetContentFragmentOptions(info);
                }
            }
        }
    }

    public interface FragmentCreator<T extends Fragment> {

        T createFragment();

    }

    public static class FloatingActionButtonOptions {

        private final @DrawableRes int mDrawableId;
        private final OnClickListener<FloatingActionButton> mOnClickListener;

        public FloatingActionButtonOptions(@DrawableRes int drawableId, OnClickListener<FloatingActionButton> onClickListener) {
            mDrawableId = drawableId;
            mOnClickListener = onClickListener;
        }

        @DrawableRes
        public int getDrawableId() {
            return mDrawableId;
        }

        public OnClickListener<FloatingActionButton> getOnClickListener() {
            return mOnClickListener;
        }
    }



    private MainActivityBinding mBinding;
    private DrawerHeaderBinding mDrawerHeaderBinding;
    private MainActivityViewModel mViewModel;

    private ActionBarDrawerToggle mDrawerToggle;
    private Account mAccount;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        this.mDrawerHeaderBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.drawer_header, mBinding.drawer, false);
        this.mViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);

        this.getSupportFragmentManager().registerFragmentLifecycleCallbacks(new FragmentLifecycleCallbacks(), false);

        if (savedInstanceState == null) {
            switchMainFragment(ShoppingListFragment::newInstance, ShoppingListFragment.class.getName(), true);
        }


        // Toolbar
        this.setSupportActionBar(this.mBinding.toolbar);
        //noinspection ConstantConditions
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Drawer
        this.mBinding.drawer.setNavigationItemSelectedListener(MainActivity.this::onDrawerMenuItemSelected);
        this.mBinding.drawer.addHeaderView(mDrawerHeaderBinding.getRoot());
        this.mDrawerHeaderBinding.setOnClickListener(view -> {
            switchMainFragment(AccountFragment::newInstance, AccountFragment.class.getName(), false);
        });

        // Setup drawer account information
        setupDrawerHeader();

        // Drawer Toggle
        this.mDrawerToggle = new ActionBarDrawerToggle(this, this.mBinding.drawerLayout, R.string.message_drawer_open, R.string.message_drawer_close);
        this.mBinding.drawerLayout.addDrawerListener(this.mDrawerToggle);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        this.mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        this.mDrawerToggle.onConfigurationChanged(newConfig);
    }



    @Override
    public void onBackPressed() {
        /*
         * No need to call fragment manager to pop back stack,
         * the super class method has already take care of it
         */
        if (this.mBinding.drawerLayout.isDrawerVisible(this.mBinding.drawer)) {
            this.mBinding.drawerLayout.closeDrawer(this.mBinding.drawer);

        }  else {
            super.onBackPressed();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.main_activity_toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (this.mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void setupDrawerHeader(){

        this.mViewModel.findCurrentAccount().observe(this, account -> {
            if (account != null){
                mDrawerHeaderBinding.setAccount(account);
                mAccount = account;
                setAccountImage(account.isImageExist());
            }
        });

        this.mViewModel.getCurrentAccountRanks().observe(this, ranks -> {
            if (ranks != null){
                int rank = ranks.size() - 1;
                int resId = getResources().getIdentifier(
                        "badge_small_rank_" + rank,
                        "drawable", getPackageName());
                mDrawerHeaderBinding.drawerAccountBadge.setImageResource(resId);
            }
        });
    }

    // Setup the profile image. Check if not custom image, set as default
    private void setAccountImage(boolean isCustom){
        String imageFileName = "Account" + "_" + mAccount.getAccountId();
        File storageDir = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = new File(storageDir, imageFileName + ".jpg");

        if (isCustom){
            if (imageFile.exists()) {
                Bitmap bitmap = ImageRotater.getsInstance(this).rotateImage(imageFile);
                // Update image view with rotated bitmap
                mDrawerHeaderBinding.navAccountPic.setImageBitmap(bitmap);
            }
        } else{
            mDrawerHeaderBinding.navAccountPic.setImageResource(R.drawable.ic_account_circle_white);
        }
    }



    /*
    ************************************************************************************************
    * Drawer Interactions
    ************************************************************************************************
     */

    private boolean onDrawerMenuItemSelected(MenuItem item) {
//        Toast.makeText(this, item.getTitle()+" Clicked", Toast.LENGTH_SHORT).show();
        switch (item.getItemId()) {
            case R.id.item_home:
                return this.onShoppingListDrawerMenuItemSelected(item);
            case R.id.item_store_list:
                return this.onStoreListDrawerMenuItemSelected(item);
            case R.id.item_product_list:
                return this.onProductListDrawerMenuItemSelected(item);
            case R.id.item_account:
                return this.onAccountDrawerMenuItemSelected(item);
            default:
                return false;
        }
    }


    /*
    ************************************************************************************************
    * Action bar title
    ************************************************************************************************
     */

    public void setFragmentTitle(String fragmentTitle) {
        if (fragmentTitle == null || fragmentTitle.isEmpty()) {
            fragmentTitle = getString(R.string.app_name);
        }
        setTitle(fragmentTitle);
    }


    /*
    ************************************************************************************************
    * Floating Action Button Interactions
    ************************************************************************************************
     */

    @SuppressWarnings("unused")
    public static class FloatingActionButtonBehavior extends FloatingActionButton.Behavior {

        private WeakReference<CoordinatorLayout> mParentRef;
        private WeakReference<FloatingActionButton> mFabRef;
        private float mAppBarLayoutHidingProgress = 0;
        private float mDodgeBottomSheetTranslationY = 0;
        private boolean mHide;

        public FloatingActionButtonBehavior() {
            super();
        }

        public FloatingActionButtonBehavior(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
            return dependency.getId() == R.id.bottom_sheet
                    || dependency instanceof AppBarLayout
                    || super.layoutDependsOn(parent, child, dependency);
        }

        @Override
        public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
            boolean changed = super.onDependentViewChanged(parent, child, dependency);

            mParentRef = new WeakReference<>(parent);
            mFabRef = new WeakReference<>(child);

            boolean layoutChild = false;

            if (dependency instanceof AppBarLayout) {

                this.mAppBarLayoutHidingProgress = -dependency.getY() / dependency.getHeight();
                layoutChild = true;

            } else if (dependency.getId() == R.id.bottom_sheet) {

                CoordinatorLayout.LayoutParams childLayoutParams = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
                float childMarginLeft = child.getX() - childLayoutParams.leftMargin;
                float childMarginRight = child.getX() + child.getWidth() + childLayoutParams.rightMargin;

                float dependencyMarginLeft = dependency.getX();
                float dependencyMarginRight = dependency.getX() + dependency.getWidth();

                if (childMarginLeft < dependencyMarginRight && childMarginRight > dependencyMarginLeft) {
                    float translateY = dependency.getY() - parent.getHeight();
                    this.mDodgeBottomSheetTranslationY = Math.min(translateY, 0);
                } else {
                    this.mDodgeBottomSheetTranslationY = 0;
                }

                layoutChild = true;
            }

            if (layoutChild) {
                this.layoutChild();
                changed = true;
            }

            return changed;
        }

        private void layoutChild() {
            CoordinatorLayout parent = mParentRef == null ? null : mParentRef.get();
            FloatingActionButton child = mFabRef == null ? null : mFabRef.get();

            if (parent == null || child == null) {
                return;
            }

            CoordinatorLayout.LayoutParams childLayoutParams = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
            float progress = mHide ? 1 : mAppBarLayoutHidingProgress;
            float translationY = this.mDodgeBottomSheetTranslationY
                    + (child.getHeight() + childLayoutParams.bottomMargin) * progress;
            child.setTranslationY(translationY);
        }

        public boolean isHide() {
            return mHide;
        }

        public void setHide(boolean hide) {
            mHide = hide;
            layoutChild();
        }
    }

    private FloatingActionButtonOptions mFloatingActionButtonOptions;
    private boolean mFloatingActionButtonOptionsApplied;

    private void setFloatingActionButtonOptions(FloatingActionButtonOptions options) {
        if (mFloatingActionButtonOptions != options || !mFloatingActionButtonOptionsApplied) {
            mFloatingActionButtonOptions = options;
            mFloatingActionButtonOptionsApplied = true;

            Drawable imageDrawable;
            final OnClickListener<FloatingActionButton> onClickListener;
            boolean hide;
            if (options != null) {
                imageDrawable = getDrawable(options.getDrawableId());
                onClickListener = options.getOnClickListener();
                hide = false;
            } else {
                imageDrawable = null;
                onClickListener = null;
                hide = true;
            }
            mBinding.fabPlus.setImageDrawable(imageDrawable);
            mBinding.fabPlus.setOnClickListener(onClickListener == null ? null : onClickListener.toViewOnClickListener());
            //noinspection ConstantConditions
            ((FloatingActionButtonBehavior) ((CoordinatorLayout.LayoutParams) mBinding.fabPlus.getLayoutParams()).getBehavior()).setHide(hide);
        }

    }

    /*
    ************************************************************************************************
    * Bottom Sheet Interactions
    ************************************************************************************************
     */

    private BottomSheetFragment.BottomSheetContentFragmentOptions mBottomSheetContentFragmentOptions;

    private void setBottomSheetContentFragmentOptions(BottomSheetFragment.BottomSheetContentFragmentOptions info) {
        if (mBottomSheetContentFragmentOptions != info) {
            this.mBottomSheetContentFragmentOptions = info;

            BottomSheetFragment fragment = (BottomSheetFragment) getSupportFragmentManager().findFragmentById(R.id.bottom_sheet);
            if (fragment != null) {
                fragment.setBottomSheetContentFragmentOptions(mBottomSheetContentFragmentOptions);
            }
        }
    }

    public BottomSheetFragment.BottomSheetContentFragmentOptions getBottomSheetContentFragmentOptions() {
        return mBottomSheetContentFragmentOptions;
    }

    /*
    ************************************************************************************************
    * Fragment Interactions
    ************************************************************************************************
     */

    private static final String FRAGMENT_TYPE_MAIN = "FRAGMENT_TYPE_MAIN";

    private class FragmentLifecycleCallbacks extends FragmentManager.FragmentLifecycleCallbacks {

        @Override
        public void onFragmentStarted(FragmentManager fm, Fragment f) {
            super.onFragmentStarted(fm, f);

            if (f instanceof MainFragment) {
                MainFragment mainFragment = (MainFragment) f;

                int drawerMenuItemId = 0;
                if (mainFragment instanceof ShoppingListFragment) {
                    drawerMenuItemId = R.id.item_home;
                } else if (mainFragment instanceof ProductListFragment) {
                    drawerMenuItemId = R.id.item_product_list;
                } else if (mainFragment instanceof StoreListFragment) {
                    drawerMenuItemId = R.id.item_store_list;
                } else if (mainFragment instanceof AccountFragment) {
                    drawerMenuItemId = R.id.item_account;
                }
                mBinding.drawer.setCheckedItem(drawerMenuItemId);

                setFragmentTitle(mainFragment.getFragmentTitle());
                setFloatingActionButtonOptions(mainFragment.getFloatingActionButtonOptions());
                setBottomSheetContentFragmentOptions(mainFragment.getBottomSheetContentFragmentOptions());
            }
        }
    }



    private static final String TAG_FRAGMENT_ROOT = "TAG_FRAGMENT_ROOT";

    private void switchMainFragment(FragmentCreator fragmentCreator, String tag, boolean root) {
        FragmentManager manager = getSupportFragmentManager();
        if (root) {
            manager.popBackStack(TAG_FRAGMENT_ROOT, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            if (manager.findFragmentByTag(tag) == null) {
                manager.beginTransaction()
                        .replace(mBinding.fragmentContainer.getId(), fragmentCreator.createFragment(), tag)
                        .commit();
            }

        } else {
            if (manager.findFragmentByTag(tag) == null) {
                manager.popBackStack(TAG_FRAGMENT_ROOT, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                manager.beginTransaction()
                        .replace(mBinding.fragmentContainer.getId(), fragmentCreator.createFragment(), tag)
                        .addToBackStack(TAG_FRAGMENT_ROOT)
                        .commit();
            }
        }
        mBinding.drawerLayout.closeDrawer(mBinding.drawer);
    }



    /*
    ************************************************************************************************
    * ShoppingListFragment Interactions
    ************************************************************************************************
     */

    private boolean onShoppingListDrawerMenuItemSelected(MenuItem item) {
        switchMainFragment(ShoppingListFragment::newInstance, ShoppingListFragment.class.getName(), true);
        return true;
    }

    /*
    ************************************************************************************************
    * StoreListFragment Interactions
    ************************************************************************************************
     */

    private boolean onStoreListDrawerMenuItemSelected(MenuItem item) {
        switchMainFragment(StoreListFragment::newInstance, StoreListFragment.class.getName(), false);
        return true;
    }

    /*
    ************************************************************************************************
    * ProductListFragment Interactions
    ************************************************************************************************
     */

    private boolean onProductListDrawerMenuItemSelected(MenuItem item) {
        switchMainFragment(ProductListFragment::newInstance, ProductListFragment.class.getName(), false);
        return true;
    }

    /*
    ************************************************************************************************
    * AccountFragment Interactions
    ************************************************************************************************
     */

    private boolean onAccountDrawerMenuItemSelected(MenuItem item){
        switchMainFragment(AccountFragment::newInstance, AccountFragment.class.getName(), false);
        return true;
    }



}
