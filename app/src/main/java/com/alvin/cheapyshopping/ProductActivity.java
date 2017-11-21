package com.alvin.cheapyshopping;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.alvin.cheapyshopping.fragments.ProductFragment;
import com.alvin.cheapyshopping.olddb.models.PriceModel;
import com.alvin.cheapyshopping.olddb.models.StoreModel;
import com.alvin.cheapyshopping.room.entities.Store;

/**
 * Created by cheng on 11/13/2017.
 */

public class ProductActivity extends AppCompatActivity {

    private static final int REQUEST_PRODUCT_ADD_PRICE = 1;
    private static final int REQUEST_PRODUCT_EDIT = 2;

    public static final String EXTRA_PRODUCT_ID = "com.alvin.cheapyshopping.ProductActivity.EXTRA_PRODUCT_ID";

    private static final String FRAGMENT_PRODUCT = "com.alvin.cheapyshopping.ProductActivity.FRAGMENT_PRODUCT";

    private Toolbar mToolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        Bundle extras = getIntent().getExtras();

        // Toolbar
        this.mToolbar = findViewById(R.id.toolbar);
        this.setSupportActionBar(this.mToolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(false);


        this.getSupportFragmentManager().registerFragmentLifecycleCallbacks(new FragmentLifecycleCallbacks(), false);
        // Start ProductFragment
        if (savedInstanceState == null) { //Prevent adding fragment twice
            this.getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, ProductFragment.newInstance(extras.getLong("mProductID")), FRAGMENT_PRODUCT)
                    .commit();
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.toolbar_menu_product, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.action_save:
//                return true;
//            case R.id.action_edit:
//                return true;
//            case R.id.action_add_price:
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_PRODUCT_ADD_PRICE:
                this.onRequestAddProductPriceResult(requestCode, resultCode, data);
                break;
            case REQUEST_PRODUCT_EDIT:
                //this.onRequestEditProduct(requestCode, resultCode, data);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);

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
            if (f instanceof ProductFragment) {
                ((ProductFragment) f).setInteractionListener(new ProductFragmentInteractionListener());
            }
        }

    }

    /*
    ************************************************************************************************
    * ProductFragment Interactions
    ************************************************************************************************
     */

    private void onRequestAddProductPriceResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PRODUCT_ADD_PRICE && resultCode == RESULT_OK) {
            ProductFragment fragment = (ProductFragment) this.getSupportFragmentManager().findFragmentByTag(FRAGMENT_PRODUCT);
            if (fragment != null) {
                fragment.updateProductPriceItemList();
            }
        }
    }
    private class ProductFragmentInteractionListener implements ProductFragment.InteractionListener {

        @Override
        public void onPriceSelected(ProductFragment fragment, PriceModel price) {

        }

        @Override
        public void onStoreSelected(ProductFragment fragment, StoreModel store) {
            Toast.makeText(ProductActivity.this, "Store Selected: " + store.name, Toast.LENGTH_SHORT).show();
            // TODO: link to Store Activity?
        }
    }

}
