package com.alvin.cheapyshopping;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Debug;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.alvin.cheapyshopping.databinding.AddStoreProductActivityBinding;
import com.alvin.cheapyshopping.fragments.AddProductFragment;
import com.alvin.cheapyshopping.fragments.AddProductPriceFragment;
import com.alvin.cheapyshopping.fragments.SelectProductFragment;
import com.alvin.cheapyshopping.fragments.StoreListFragment;
import com.alvin.cheapyshopping.utils.CurrentAccountScoreAdder;

import java.util.List;

/**
 * Created by cheng on 11/28/2017.
 */

public class AddStoreProductActivity extends AppCompatActivity {

    public static final String EXTRA_STORE_ID= "com.alvin.cheapyshopping.AddStoreProductActivity.EXTRA_STORE_ID";

    private static final String FRAGMENT_SELECT_PRODUCT = "com.alvin.cheapyshopping.AddStoreProductActivity.FRAGMENT_SELECT_PRODUCT";
    private static final String FRAGMENT_ADD_PRODUCT_PRICE = "com.alvin.cheapyshopping.AddStoreProductActivity.FRAGMENT_ADD_PRODUCT_PRICE";
    private static final String FRAGMENT_ADD_PRODUCT = "com.alvin.cheapyshopping.AddStoreProductActivity.FRAGMENT_ADD_PRODUCT";


    private AddStoreProductActivityBinding mBinding;
    private String mSelectedProductId;
    private String mStoreId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.mBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_store_product);

        Bundle args = this.getIntent().getExtras();
        String storeId = null;
        if (args != null) {
            storeId = args.getString(EXTRA_STORE_ID);
            mStoreId = storeId;
        }

        if (storeId == null) {
            throw new RuntimeException("Store Id required!");
        }

        this.getSupportFragmentManager().registerFragmentLifecycleCallbacks(new FragmentLifeCycleCallback(), false);

        if (savedInstanceState == null) {
            this.getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, SelectProductFragment.newInstance(true, null))
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (this.getSupportFragmentManager().getBackStackEntryCount() > 0) {
            this.getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }



    /*
    ************************************************************************************************
    * Fragment Interaction
    ************************************************************************************************
     */

    private class FragmentLifeCycleCallback extends FragmentManager.FragmentLifecycleCallbacks {

        @Override
        public void onFragmentAttached(FragmentManager fm, Fragment f, Context context) {
            super.onFragmentAttached(fm, f, context);
            if (f instanceof SelectProductFragment) {
                ((SelectProductFragment) f).setInteractionListener(new SelectProductFragmentInteractionListener());
            } else if(f instanceof AddProductPriceFragment){
                ((AddProductPriceFragment) f).setInteractionListener(new AddProductPriceFragmentInteractionListener());
            } else if(f instanceof AddProductFragment){
                ((AddProductFragment) f).setInteractionListener(new AddProductFragmentInteractionListener());
            }
        }

        @Override
        public void onFragmentDetached(FragmentManager fm, Fragment f) {
            super.onFragmentDetached(fm, f);
            if (f instanceof SelectProductFragment) {
                ((SelectProductFragment) f).setInteractionListener(null);
            } else if (f instanceof AddProductPriceFragment) {
                ((AddProductPriceFragment) f).setInteractionListener(null);
            } else if (f instanceof AddProductFragment) {
                ((AddProductFragment) f).setInteractionListener(null);
            }
        }
    }

    /*
    ************************************************************************************************
    * SelectProductFragment Interaction
    ************************************************************************************************
     */
    private class SelectProductFragmentInteractionListener
        implements  SelectProductFragment.InteractionListener{

        @Override
        public void onAddProductOptionSelected(SelectProductFragment fragment) {
            Toast.makeText(AddStoreProductActivity.this,"Add new product", Toast.LENGTH_LONG).show();
            AddStoreProductActivity.this.getSupportFragmentManager().popBackStack();
            AddStoreProductActivity.this.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, AddProductFragment.newInstance(true), FRAGMENT_ADD_PRODUCT)
                    .addToBackStack(null)
                    .commit();
        }

        @Override
        public void onProductItemsSelected(SelectProductFragment fragment, List<String> products) {
            Toast.makeText(AddStoreProductActivity.this,"Selected product", Toast.LENGTH_LONG).show();
            AddStoreProductActivity.this.mSelectedProductId = products.get(0);
            AddStoreProductActivity.this.getSupportFragmentManager().popBackStack();
            AddStoreProductActivity.this.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, AddProductPriceFragment.newInstance(mSelectedProductId, true, mStoreId), FRAGMENT_ADD_PRODUCT_PRICE)
                    .addToBackStack(null)
                    .commit();
        }

    }

    /*
    ************************************************************************************************
    * AddProductPriceFragment Interaction
    ************************************************************************************************
     */

    private class AddProductPriceFragmentInteractionListener implements AddProductPriceFragment.InteractionListener{
        @Override
        public void onDiscardNewPriceOptionSelected(AddProductPriceFragment fragment) {
            Toast.makeText(AddStoreProductActivity.this, "Add product action cancelled", Toast.LENGTH_LONG).show();
            AddStoreProductActivity.this.finish();
        }
        @Override
        public void onNewPriceAdded(AddProductPriceFragment fragment, long rowId) {
            Toast.makeText(AddStoreProductActivity.this,"Product added", Toast.LENGTH_LONG).show();
            CurrentAccountScoreAdder.getsInstance(getApplicationContext())
                    .addScore(getResources().getInteger(R.integer.add_price));
            AddStoreProductActivity.this.finish();
        }
    }

    /*
    ************************************************************************************************
    * AddProductFragment Interaction
    ************************************************************************************************
     */

    private class AddProductFragmentInteractionListener implements AddProductFragment.InteractionListener{
        @Override
        public void onDiscardOptionSelected(AddProductFragment fragment) {
            Toast.makeText(AddStoreProductActivity.this, "Add product action cancelled", Toast.LENGTH_LONG).show();
            AddStoreProductActivity.this.finish();
        }

        @Override
        public void onNewProductAdded(AddProductFragment fragment, String productId) {
            Toast.makeText(AddStoreProductActivity.this,"New product added", Toast.LENGTH_LONG).show();
            CurrentAccountScoreAdder.getsInstance(getApplicationContext())
                    .addScore(getResources().getInteger(R.integer.add_new_product));
            AddStoreProductActivity.this.mSelectedProductId = productId;
            AddStoreProductActivity.this.getSupportFragmentManager().popBackStack();
            AddStoreProductActivity.this.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, AddProductPriceFragment.newInstance(mSelectedProductId, true, mStoreId), FRAGMENT_ADD_PRODUCT_PRICE)
                    .addToBackStack(null)
                    .commit();
        }
    }

}
