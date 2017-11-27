package com.alvin.cheapyshopping;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.alvin.cheapyshopping.databinding.AddProductPriceActivityBinding;
import com.alvin.cheapyshopping.fragments.AddProductPriceFragment;

public class AddProductPriceActivity extends AppCompatActivity {

    public static final String EXTRA_PRODUCT_ID = "com.alvin.cheapyshopping.AddProductPriceActivity.EXTRA_PRODUCT_ID";


    private AddProductPriceActivityBinding mBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.mBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_price);

        Bundle args = this.getIntent().getExtras();
        String productId = null;
        if (args != null) {
            productId = args.getString(EXTRA_PRODUCT_ID);
        }

        if (productId == null) {
            throw new RuntimeException("Product Id required!");
        }

        this.getSupportFragmentManager().registerFragmentLifecycleCallbacks(new FragmentLifeCycleCallback(), false);

        if (savedInstanceState == null) {
            this.getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, AddProductPriceFragment.newInstance(productId, true, null))
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
            if (f instanceof AddProductPriceFragment) {
                ((AddProductPriceFragment) f).setInteractionListener(new AddProductPriceFragmentInteractionListener());
            }
        }

        @Override
        public void onFragmentDetached(FragmentManager fm, Fragment f) {
            super.onFragmentDetached(fm, f);
            if (f instanceof AddProductPriceFragment) {
                ((AddProductPriceFragment) f).setInteractionListener(null);
            }
        }
    }

    private class AddProductPriceFragmentInteractionListener
            implements AddProductPriceFragment.InteractionListener {

        @Override
        public void onDiscardNewPriceOptionSelected(AddProductPriceFragment fragment) {
            AddProductPriceActivity.this.finish();
        }

        @Override
        public void onNewPriceAdded(AddProductPriceFragment fragment, long rowId) {
            AddProductPriceActivity.this.finish();
        }
    }
}
