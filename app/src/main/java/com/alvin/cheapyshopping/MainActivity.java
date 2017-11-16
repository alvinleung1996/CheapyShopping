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

import com.alvin.cheapyshopping.db.DatabaseHelper;
import com.alvin.cheapyshopping.db.models.ProductModel;
import com.alvin.cheapyshopping.db.models.ShoppingListModel;
import com.alvin.cheapyshopping.db.models.StoreModel;
import com.alvin.cheapyshopping.fragments.ShoppingListFragment;
import com.alvin.cheapyshopping.fragments.StoreListFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        ShoppingListFragment.ShoppingListFragmentListener {

    private static final int REQUEST_ADD_SHOPPING_LIST_PRODUCT = 1;


    private static final String FRAGMENT_SHOPPING_LIST = "com.alvin.cheapyshopping.MainActivity.FRAGMENT_SHOPPING_LIST";


    private static final String FRAGMENT_STACK_STATE_HOME = "home";


    private DrawerLayout mDrawerLayout;
    private NavigationView mDrawer;
    private Toolbar mToolbar;
    private FloatingActionButton mPlusFab;

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
        this.mPlusFab = findViewById(R.id.fab_plus);
        this.mPlusFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.this.onPlusFabClick(view);
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
        if (this.mDrawerLayout.isDrawerVisible(this.mDrawer)) {
            this.mDrawerLayout.closeDrawer(this.mDrawer);

        } else if (this.getSupportFragmentManager().getBackStackEntryCount() > 0) {
            // TODO: really need this? or the parent class has already take care this?
            this.getSupportFragmentManager().popBackStack();

        } else {
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ADD_SHOPPING_LIST_PRODUCT:
                this.onRequestAddShoppingListProductResult(requestCode, resultCode, data);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);

    }



    /*
    ************************************************************************************************
    * Fragment Interactions
    ************************************************************************************************
     */

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
            int itemId;
            if (f instanceof ShoppingListFragment) {
                itemId = R.id.nav_home;
            } else if (f instanceof StoreListFragment) {
                itemId = R.id.item_store_list;
            } else {
                return;
            }

            MainActivity.this.mDrawer.setCheckedItem(itemId);
        }

        @Override
        public void onFragmentDetached(FragmentManager fm, Fragment f) {
            super.onFragmentDetached(fm, f);
            if (f instanceof StoreListFragment) {
                ((StoreListFragment) f).setInteractableListener(null);
            }
        }
    };


    /*
    ************************************************************************************************
    * View Interactions
    ************************************************************************************************
     */

    private boolean onDrawerMenuItemSelected(MenuItem item) {
        Toast.makeText(this, item.getTitle()+" Clicked", Toast.LENGTH_SHORT).show();
        switch (item.getItemId()) {
            case R.id.nav_home:
                return this.onShoppingListDrawerMenuItemSelected(item);
            case R.id.item_store_list:
                return this.onStoreListDrawerMenuItemSelected(item);
        }

        return false;
    }

    private void onPlusFabClick(View view) {
        Intent intent = new Intent(this, AddShoppingListProductActivity.class);
        this.startActivityForResult(intent, REQUEST_ADD_SHOPPING_LIST_PRODUCT);
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

    @Override
    public void onShoppingListStoreItemSelected(ShoppingListFragment fragment, StoreModel model) {
        Toast.makeText(this, "Selected Store: " + model.name, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onShoppingListProductItemSelected(ShoppingListFragment fragment, ProductModel model) {
        Toast.makeText(this, "Selected Product: " + model.name, Toast.LENGTH_SHORT).show();
    }

    private void onRequestAddShoppingListProductResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ADD_SHOPPING_LIST_PRODUCT && resultCode == RESULT_OK) {
            ShoppingListFragment fragment = (ShoppingListFragment) this.getSupportFragmentManager().findFragmentByTag(FRAGMENT_SHOPPING_LIST);
            if (fragment != null) {
                fragment.updateShoppingListItemList();
            }
        }
    }

    /*
    ************************************************************************************************
    * InteractionListener
    ************************************************************************************************
     */

    private boolean onStoreListDrawerMenuItemSelected(MenuItem item) {
        if (!item.isChecked()) {
            this.getSupportFragmentManager().popBackStack();
            this.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, StoreListFragment.newInstance())
                    .addToBackStack(null)
                    .commit();
        }
        this.mDrawerLayout.closeDrawer(this.mDrawer);
        return true;
    }

    private class StoreListFragmentInteractionListener implements StoreListFragment.InteractionListener {

        @Override
        public void onStoreSelected(StoreListFragment fragment, StoreModel store) {
            Toast.makeText(MainActivity.this, "Store Selected: " + store.name, Toast.LENGTH_SHORT).show();
        }

    };


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
    }

}
