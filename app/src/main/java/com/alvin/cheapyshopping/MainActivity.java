package com.alvin.cheapyshopping;

import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.alvin.cheapyshopping.databinding.DrawerHeaderBinding;
import com.alvin.cheapyshopping.databinding.MainActivityBinding;
import com.alvin.cheapyshopping.db.entities.Account;
import com.alvin.cheapyshopping.fragments.AccountFragment;
import com.alvin.cheapyshopping.fragments.ProductListFragment;
import com.alvin.cheapyshopping.fragments.ShoppingListFragment;
import com.alvin.cheapyshopping.fragments.StoreListFragment;
import com.alvin.cheapyshopping.db.entities.Store;
import com.alvin.cheapyshopping.viewmodels.AccountFragmentViewModel;
import com.alvin.cheapyshopping.viewmodels.MainActivityViewModel;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ADD_SHOPPING_LIST_PRODUCT = 1;
    private static final int REQUEST_ADD_STORE = 2;


    private static final String FRAGMENT_SHOPPING_LIST = "com.alvin.cheapyshopping.MainActivity.FRAGMENT_SHOPPING_LIST";
    private static final String FRAGMENT_STORE_LIST = "com.alvin.cheapyshopping.MainActivity.FRAGMENT_STORE_LIST";
    private static final String FRAGMENT_PRODUCT_LIST = "com.alvin.cheapyshopping.MainActivity.FRAGMENT_PRODUCT_LIST";
    private static final String FRAGMENT_ACCOUNT = "com.alvin.cheapyshopping.MainActivity.FRAGMENT_ACCOUNT";



    public interface FloatingActionButtonInteractionListener {

        void onConfigureFloatingActionButton(FloatingActionButton button);

        void onFloatingActionButtonClick(FloatingActionButton button);

    }


    private MainActivityBinding mBinding;
    private DrawerHeaderBinding mDrawerHeaderBinding;
    private MainActivityViewModel mViewModel;

    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        this.mDrawerHeaderBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.drawer_header, mBinding.drawer, false);

        this.getSupportFragmentManager().registerFragmentLifecycleCallbacks(new FragmentLifecycleCallbacks(), false);

        if (savedInstanceState == null) { //Prevent adding fragment twice
            this.getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, ShoppingListFragment.newInstance(), FRAGMENT_SHOPPING_LIST)
                    .commit();
        }


        // Toolbar
        this.setSupportActionBar((Toolbar) this.mBinding.toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Drawer
        this.mBinding.drawer.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return MainActivity.this.onDrawerMenuItemSelected(item);
            }
        });
        this.mBinding.drawer.addHeaderView(mDrawerHeaderBinding.getRoot());
        this.mDrawerHeaderBinding.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                MainActivity.this.getSupportFragmentManager().popBackStack();
                MainActivity.this.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, AccountFragment.newInstance(), FRAGMENT_ACCOUNT)
                        .addToBackStack(null)
                        .commit();
                MainActivity.this.mBinding.drawerLayout.closeDrawer(MainActivity.this.mBinding.drawer);
            }
        });

        // TODO: Setup drawer account information
//        this.mViewModel.findCurrentAccount().observe(this, new Observer<Account>() {
//            @Override
//            public void onChanged(@Nullable Account account) {
//                mDrawerHeaderBinding.setAccount(account);
//            }
//        });


        // Floating action buttons
        this.mBinding.fabPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.this.onFabClick((FloatingActionButton) view);
            }
        });

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
        if (this.mActiveFragment != null && this.mActiveFragment instanceof FloatingActionButtonInteractionListener) {
            ((FloatingActionButtonInteractionListener) this.mActiveFragment)
                    .onConfigureFloatingActionButton(this.mBinding.fabPlus);
        }
    }

    private void onFabClick(FloatingActionButton button) {
        if (this.mActiveFragment != null && this.mActiveFragment instanceof FloatingActionButtonInteractionListener) {
            ((FloatingActionButtonInteractionListener) this.mActiveFragment)
                    .onFloatingActionButtonClick(button);
        }
    }



    /*
    ************************************************************************************************
    * Fragment Interactions
    ************************************************************************************************
     */

    private Fragment mActiveFragment;

    private class FragmentLifecycleCallbacks extends FragmentManager.FragmentLifecycleCallbacks {

        @Override
        public void onFragmentAttached(FragmentManager fm, Fragment f, Context context) {
            super.onFragmentAttached(fm, f, context);
            if (f instanceof ShoppingListFragment) {
            } else if (f instanceof StoreListFragment) {
                ((StoreListFragment) f).setInteractableListener(new StoreListFragmentInteractionListener());
            }
        }

        @Override
        public void onFragmentStarted(FragmentManager fm, Fragment f) {
            super.onFragmentStarted(fm, f);

            MainActivity.this.mActiveFragment = f;

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


            // Hide floating action button for some fragments
            if (f instanceof AccountFragment){
                MainActivity.this.mBinding.fabPlus.hide();
            } else {
                MainActivity.this.mBinding.fabPlus.show();
            }

            MainActivity.this.configureFab();
        }

        @Override
        public void onFragmentStopped(FragmentManager fm, Fragment f) {
            super.onFragmentStopped(fm, f);
            if (MainActivity.this.mActiveFragment == f) {
                MainActivity.this.mActiveFragment = null;

                MainActivity.this.configureFab();
            }
        }

        @Override
        public void onFragmentDetached(FragmentManager fm, Fragment f) {
            super.onFragmentDetached(fm, f);
            if (f instanceof ShoppingListFragment) {
            } else if (f instanceof StoreListFragment) {
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
                    .replace(R.id.fragment_container, StoreListFragment.newInstance(), FRAGMENT_STORE_LIST)
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
                    .replace(R.id.fragment_container, ProductListFragment.newInstance(), FRAGMENT_PRODUCT_LIST)
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
                    .replace(R.id.fragment_container, AccountFragment.newInstance(), FRAGMENT_ACCOUNT)
                    .addToBackStack(null)
                    .commit();
        }
        this.mBinding.drawerLayout.closeDrawer(this.mBinding.drawer);
        return true;
    }

}
