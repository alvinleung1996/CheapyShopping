package com.alvin.cheapyshopping;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.alvin.cheapyshopping.fragments.ProductListFragment;
import com.alvin.cheapyshopping.olddb.DatabaseHelper;
import com.alvin.cheapyshopping.olddb.models.PriceModel;
import com.alvin.cheapyshopping.olddb.models.ProductModel;
import com.alvin.cheapyshopping.olddb.models.ShoppingListModel;
import com.alvin.cheapyshopping.olddb.models.StoreModel;
import com.alvin.cheapyshopping.fragments.ShoppingListFragment;
import com.alvin.cheapyshopping.fragments.StoreListFragment;
import com.alvin.cheapyshopping.room.entities.Store;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ADD_SHOPPING_LIST_PRODUCT = 1;
    private static final int REQUEST_ADD_STORE = 2;


    private static final String FRAGMENT_SHOPPING_LIST = "com.alvin.cheapyshopping.MainActivity.FRAGMENT_SHOPPING_LIST";
    private static final String FRAGMENT_STORE_LIST = "com.alvin.cheapyshopping.MainActivity.FRAGMENT_STORE_LIST";
    private static final String FRAGMENT_PRODUCT_LIST = "com.alvin.cheapyshopping.MainActivity.FRAGMENT_PRODUCT_LIST";



    public interface FloatingActionButtonInteractionListener {

        void onConfigureFloatingActionButton(FloatingActionButton button);

        void onFloatingActionButtonClick(FloatingActionButton button);

    }



    private DrawerLayout mDrawerLayout;
    private NavigationView mDrawer;
    private Toolbar mToolbar;
    private FloatingActionButton mFab;

    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.createSampleData();

        this.getSupportFragmentManager().registerFragmentLifecycleCallbacks(new FragmentLifecycleCallbacks(), false);

        if (savedInstanceState == null) { //Prevent adding fragment twice
            this.getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, ShoppingListFragment.newInstance(), FRAGMENT_SHOPPING_LIST)
                    .commit();

//            this.getSupportFragmentManager().beginTransaction()
//                    .add(R.id.fragment_container_popular_products, PopularProductsFragment.newInstance())
//                    .commit();
        }

        this.mDrawerLayout = findViewById(R.id.drawer_layout);
        this.mDrawer = findViewById(R.id.drawer);
        this.mToolbar = findViewById(R.id.toolbar);


        // Toolbar
        this.setSupportActionBar(this.mToolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Drawer
        this.mDrawer.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return MainActivity.this.onDrawerMenuItemSelected(item);
            }
        });

        //Floating action buttons
        this.mFab = findViewById(R.id.fab_plus);
        this.mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.this.onFabClick((FloatingActionButton) view);
            }
        });

        // Drawer Toggle
        this.mDrawerToggle = new ActionBarDrawerToggle(this, this.mDrawerLayout, R.string.message_drawer_open, R.string.message_drawer_close);
        this.mDrawerLayout.addDrawerListener(this.mDrawerToggle);

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
        if (this.mDrawerLayout.isDrawerVisible(this.mDrawer)) {
            this.mDrawerLayout.closeDrawer(this.mDrawer);

        }  else {
            super.onBackPressed();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.toolbar_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.item_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        // TODO: Add listener to search view

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
            ((FloatingActionButtonInteractionListener) this.mActiveFragment).onConfigureFloatingActionButton(this.mFab);
        }
    }

    private void onFabClick(FloatingActionButton button) {
        if (this.mActiveFragment != null && this.mActiveFragment instanceof FloatingActionButtonInteractionListener) {
            ((FloatingActionButtonInteractionListener) this.mActiveFragment).onFloatingActionButtonClick(button);
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
            }
            if (itemId != 0) MainActivity.this.mDrawer.setCheckedItem(itemId);

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
        this.mDrawerLayout.closeDrawer(this.mDrawer);
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
        this.mDrawerLayout.closeDrawer(this.mDrawer);
        return true;
    }

    private class StoreListFragmentInteractionListener implements StoreListFragment.InteractionListener {

        @Override
        public void onStoreSelected(StoreListFragment fragment, Store store) {
            Toast.makeText(MainActivity.this, "Store Selected: " + store.getName(), Toast.LENGTH_SHORT).show();
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
        this.mDrawerLayout.closeDrawer(this.mDrawer);
        return true;
    }


    /*
    ************************************************************************************************
    * Sample Data
    ************************************************************************************************
     */

    private void createSampleData() {
        if (!DatabaseHelper.getInstance(this).deleteDatabase()) {
            Log.e("DB", "Cannot delete database");
        }

        ShoppingListModel shoppingList = new ShoppingListModel(this);
        shoppingList.saveOrThrow();

        List<ProductModel> products = new ArrayList<>();
        for (int i = 0; i < 10; ++i) {
            ProductModel product = new ProductModel(this);
            product.name = "Product " + i;
            product.saveOrThrow();
            products.add(product);
        }
        for (int i = 0; i < 7 && i < products.size(); ++i) {
            shoppingList.addProduct(products.get(i));
        }

        StoreModel store0 = new StoreModel(this);
        store0.location = "HKU";
        store0.name = "COOP";
        store0.saveOrThrow();

        StoreModel store1 = new StoreModel(this);
        store1.location = "UST";
        store1.name = "Hall 1";
        store1.saveOrThrow();

        StoreModel store2 = new StoreModel(this);
        store2.location = "IFC";
        store2.name = "CitySuper";
        store2.saveOrThrow();

        StoreModel store3 = new StoreModel(this);
        store3.location = "HKU";
        store3.name = "Library";
        store3.saveOrThrow();


        PriceModel price0 = new PriceModel(this);
        price0.price = 100;
        price0.type = "SINGLE";
        price0.foreignProductId = 1;
        price0.foreignStoreId = 1;
        price0.saveOrThrow();

        PriceModel price1 = new PriceModel(this);
        price1.price = 200;
        price1.type = "SINGLE";
        price1.foreignProductId = 1;
        price1.foreignStoreId = 2;
        price1.saveOrThrow();

        PriceModel price2 = new PriceModel(this);
        price2.price = 300;
        price2.type = "SINGLE";
        price2.foreignProductId = 1;
        price2.foreignStoreId = 3;
        price2.saveOrThrow();

        PriceModel price3 = new PriceModel(this);
        price3.price = 300;
        price3.type = "SINGLE";
        price3.foreignProductId = 1;
        price3.foreignStoreId = 4;
        price3.saveOrThrow();

        for(int i = 0; i < 10; ++i){
            PriceModel price = new PriceModel(this);
            price.price = 100 * (i+1);
            price.type = "SINGLE";
            price.foreignProductId = 1;
            price.foreignStoreId = 1;
            price.saveOrThrow();
        }


    }

}
