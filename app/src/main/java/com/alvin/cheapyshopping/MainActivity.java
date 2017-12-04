package com.alvin.cheapyshopping;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
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
import com.alvin.cheapyshopping.db.entities.Store;
import com.alvin.cheapyshopping.fragments.AccountFragment;
import com.alvin.cheapyshopping.fragments.BottomSheetFragment;
import com.alvin.cheapyshopping.fragments.ProductListFragment;
import com.alvin.cheapyshopping.fragments.ShoppingListFragment;
import com.alvin.cheapyshopping.fragments.StoreListFragment;
import com.alvin.cheapyshopping.utils.ImageRotater;
import com.alvin.cheapyshopping.viewmodels.MainActivityViewModel;

import java.io.File;
public class MainActivity extends BaseActivity {

    private static final int REQUEST_ADD_SHOPPING_LIST_PRODUCT = 1;
    private static final int REQUEST_ADD_STORE = 2;



    public interface FloatingActionButtonInteractionListener {

        void onConfigureFloatingActionButton(FloatingActionButton button);

        void onFloatingActionButtonClick(FloatingActionButton button);

    }

    public interface BottomSheetInteractionListener {

        boolean onConfigureBottomSheet(BottomSheetFragment bottomSheetFragment);

    }


    private MainActivityBinding mBinding;
    private DrawerHeaderBinding mDrawerHeaderBinding;
    private MainActivityViewModel mViewModel;

    private ActionBarDrawerToggle mDrawerToggle;
    private Account mAccount;

    private BottomSheetFragment mBottomSheetFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        this.mDrawerHeaderBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.drawer_header, mBinding.drawer, false);
        this.mViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);

        this.mBottomSheetFragment = (BottomSheetFragment) this.getSupportFragmentManager().findFragmentById(R.id.bottom_sheet);

        this.getSupportFragmentManager().registerFragmentLifecycleCallbacks(new FragmentLifecycleCallbacks(), false);

        if (savedInstanceState == null) { //Prevent adding fragment twice
            this.getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, ShoppingListFragment.newInstance(), FRAGMENT_TYPE_MAIN)
                    .commit();
        }


        // Toolbar
        this.setSupportActionBar(this.mBinding.toolbar);
        //noinspection ConstantConditions
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Drawer
        this.mBinding.drawer.setNavigationItemSelectedListener(MainActivity.this::onDrawerMenuItemSelected);
        this.mBinding.drawer.addHeaderView(mDrawerHeaderBinding.getRoot());
        this.mDrawerHeaderBinding.setOnClickListener(view -> {
            MainActivity.this.getSupportFragmentManager().popBackStack();
            MainActivity.this.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, AccountFragment.newInstance(), FRAGMENT_TYPE_MAIN)
                    .addToBackStack(null)
                    .commit();
            MainActivity.this.mBinding.drawerLayout.closeDrawer(MainActivity.this.mBinding.drawer);
        });

        // Setup drawer account information
        setupDrawerHeader();


        // Floating action buttons
        this.mBinding.fabPlus.setOnClickListener(view -> MainActivity.this.onFabClick((FloatingActionButton) view));

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
        Toast.makeText(this, item.getTitle()+" Clicked", Toast.LENGTH_SHORT).show();
        switch (item.getItemId()) {
            case R.id.nav_home:
                return this.onShoppingListDrawerMenuItemSelected(item);
            case R.id.item_store_list:
                return this.onStoreListDrawerMenuItemSelected(item);
            case R.id.item_product_list:
                return this.onProductListDrawerMenuItemSelected(item);
            case R.id.item_account:
                return this.onAccountDrawerMenuItemSelected(item);
        }

        return false;
    }


    /*
    ************************************************************************************************
    * Floating Action Button Interactions
    ************************************************************************************************
     */

    private void configureFab() {
        if (this.mActiveMainFragment != null && this.mActiveMainFragment instanceof FloatingActionButtonInteractionListener) {
            ((FloatingActionButtonInteractionListener) this.mActiveMainFragment)
                    .onConfigureFloatingActionButton(this.mBinding.fabPlus);
        }
    }

    private void onFabClick(FloatingActionButton button) {
        if (this.mActiveMainFragment != null && this.mActiveMainFragment instanceof FloatingActionButtonInteractionListener) {
            ((FloatingActionButtonInteractionListener) this.mActiveMainFragment)
                    .onFloatingActionButtonClick(button);
        }
    }


    @SuppressWarnings("unused")
    public static class FloatingActionButtonBehavior extends FloatingActionButton.Behavior {

        private float appBarLayoutHidingProgress = 0;
        private float dodgeBottomSheetTranslationY = 0;

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

            boolean layoutChild = false;

            if (dependency instanceof AppBarLayout) {

                this.appBarLayoutHidingProgress = -dependency.getY() / dependency.getHeight();
                layoutChild = true;

            } else if (dependency.getId() == R.id.bottom_sheet) {

                CoordinatorLayout.LayoutParams childLayoutParams = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
                float childMarginLeft = child.getX() - childLayoutParams.leftMargin;
                float childMarginRight = child.getX() + child.getWidth() + childLayoutParams.rightMargin;

                float dependencyMarginLeft = dependency.getX();
                float dependencyMarginRight = dependency.getX() + dependency.getWidth();

                if (childMarginLeft < dependencyMarginRight && childMarginRight > dependencyMarginLeft) {
                    float translateY = dependency.getY() - parent.getHeight();
                    this.dodgeBottomSheetTranslationY = Math.min(translateY, 0);
                } else {
                    this.dodgeBottomSheetTranslationY = 0;
                }

                layoutChild = true;
            }

            if (layoutChild) {
                this.layoutChild(parent, child);
                changed = true;
            }

            return changed;
        }

        private void layoutChild(CoordinatorLayout parent, FloatingActionButton child) {
            CoordinatorLayout.LayoutParams childLayoutParams = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
            float movePathY = -this.dodgeBottomSheetTranslationY + child.getHeight() + childLayoutParams.bottomMargin;
            float translationY = this.dodgeBottomSheetTranslationY + movePathY * this.appBarLayoutHidingProgress;
            child.setTranslationY(translationY);
        }
    }


    /*
    ************************************************************************************************
    * Bottom Sheet Interactions
    ************************************************************************************************
     */

    private void configureBottomSheet() {
        boolean show = false;
        if (this.mActiveMainFragment != null && this.mActiveMainFragment instanceof BottomSheetInteractionListener) {
            show = ((BottomSheetInteractionListener) this.mActiveMainFragment)
                    .onConfigureBottomSheet(this.mBottomSheetFragment);
        }
        if (!show) {
            this.mBottomSheetFragment.setHideable(true);
            this.mBottomSheetFragment.hide();
        }
    }



    /*
    ************************************************************************************************
    * Fragment Interactions
    ************************************************************************************************
     */

    private static final String FRAGMENT_TYPE_MAIN = "FRAGMENT_TYPE_MAIN";
    private static final String FRAGMENT_TYPE_BOTTOM_SHEET = "FRAGMENT_TYPE_BOTTOM_SHEET";
    private static final String FRAGMENT_TYPE_DIALOG = "FRAGMENT_TYPE_DIALOG";

    private Fragment mActiveMainFragment;

    private class FragmentLifecycleCallbacks extends FragmentManager.FragmentLifecycleCallbacks {

        @Override
        public void onFragmentAttached(FragmentManager fm, Fragment f, Context context) {
            super.onFragmentAttached(fm, f, context);
            if (f instanceof StoreListFragment) {
                ((StoreListFragment) f).setInteractableListener(new StoreListFragmentInteractionListener());
            }
        }

        @Override
        public void onFragmentStarted(FragmentManager fm, Fragment f) {
            super.onFragmentStarted(fm, f);

            if (FRAGMENT_TYPE_MAIN.equals(f.getTag())) {

                MainActivity.this.mActiveMainFragment = f;

                int itemId = 0;
                if (f instanceof ShoppingListFragment) {
                    itemId = R.id.nav_home;
                } else if (f instanceof StoreListFragment) {
                    itemId = R.id.item_store_list;
                } else if (f instanceof ProductListFragment){
                    itemId = R.id.item_product_list;
                } else if (f instanceof AccountFragment){
                    itemId = R.id.item_account;
                }
                if (itemId != 0) MainActivity.this.mBinding.drawer.setCheckedItem(itemId);

                MainActivity.this.configureFab();

                MainActivity.this.configureBottomSheet();
            }

            // Hide floating action button for some fragments
            if (f instanceof AccountFragment){
                MainActivity.this.mBinding.fabPlus.hide();
            } else {
                MainActivity.this.mBinding.fabPlus.show();
            }
        }

        @Override
        public void onFragmentStopped(FragmentManager fm, Fragment f) {
            super.onFragmentStopped(fm, f);

            if (FRAGMENT_TYPE_MAIN.equals(f.getTag())) {

                if (MainActivity.this.mActiveMainFragment == f) {
                    MainActivity.this.mActiveMainFragment = null;

                    MainActivity.this.configureFab();

                    MainActivity.this.configureBottomSheet();
                }
            }
        }

        @Override
        public void onFragmentDetached(FragmentManager fm, Fragment f) {
            super.onFragmentDetached(fm, f);
            if (f instanceof StoreListFragment) {
                ((StoreListFragment) f).setInteractableListener(null);
            }
        }
    }



    /*
    ************************************************************************************************
    * ShoppingListFragment Interactions
    ************************************************************************************************
     */

    private boolean onShoppingListDrawerMenuItemSelected(MenuItem item) {
        if (!item.isChecked()) {
            this.getSupportFragmentManager().popBackStack();
        }
        this.mBinding.drawerLayout.closeDrawer(this.mBinding.drawer);
        return true;
    }

    /*
    ************************************************************************************************
    * StoreListFragment Interactions
    ************************************************************************************************
     */

    private boolean onStoreListDrawerMenuItemSelected(MenuItem item) {
        if (!item.isChecked()) {
            this.getSupportFragmentManager().popBackStack();
            this.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, StoreListFragment.newInstance(), FRAGMENT_TYPE_MAIN)
                    .addToBackStack(null)
                    .commit();
        }
        this.mBinding.drawerLayout.closeDrawer(this.mBinding.drawer);
        return true;
    }

    private class StoreListFragmentInteractionListener implements StoreListFragment.InteractionListener {

        @Override
        public void onStoreSelected(StoreListFragment fragment, Store store) {
            Toast.makeText(MainActivity.this, "Store Selected: " + store.getName(), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, StoreActivity.class);
            intent.putExtra(StoreActivity.EXTRA_STORE_ID, store.getStoreId());
            MainActivity.this.startActivity(intent);
        }

        @Override
        public void onRequestNewStore(StoreListFragment fragment) {
            Intent intent = new Intent(MainActivity.this, AddStoreActivity.class);
            MainActivity.this.startActivityForResult(intent, REQUEST_ADD_STORE);
        }
    }

    /*
    ************************************************************************************************
    * ProductListFragment Interactions
    ************************************************************************************************
     */

    private boolean onProductListDrawerMenuItemSelected(MenuItem item) {
        if (!item.isChecked()) {
            this.getSupportFragmentManager().popBackStack();
            this.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, ProductListFragment.newInstance(), FRAGMENT_TYPE_MAIN)
                    .addToBackStack(null)
                    .commit();
        }
        this.mBinding.drawerLayout.closeDrawer(this.mBinding.drawer);
        return true;
    }

    /*
    ************************************************************************************************
    * AccountFragment Interactions
    ************************************************************************************************
     */

    private boolean onAccountDrawerMenuItemSelected(MenuItem item){
        if (!item.isChecked()){
            this.getSupportFragmentManager().popBackStack();
            this.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, AccountFragment.newInstance(), FRAGMENT_TYPE_MAIN)
                    .addToBackStack(null)
                    .commit();
        }
        this.mBinding.drawerLayout.closeDrawer(this.mBinding.drawer);
        return true;
    }



}
