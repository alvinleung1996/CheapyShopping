package com.alvin.cheapyshopping;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;

import com.alvin.cheapyshopping.databinding.ProductActivityBinding;
import com.alvin.cheapyshopping.fragments.ProductInfoFragment;

/**
 * Created by cheng on 11/13/2017.
 */

public class ProductActivity extends AppCompatActivity {

    private static final int REQUEST_PRODUCT_ADD_PRICE = 1;
    private static final int REQUEST_PRODUCT_EDIT = 2;

    public static final String EXTRA_PRODUCT_ID = "com.alvin.cheapyshopping.ProductActivity.EXTRA_PRODUCT_ID";


    private ProductActivityBinding mBinding;

    private long mProductId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.mBinding = DataBindingUtil.setContentView(this, R.layout.activity_product);

        Bundle args = getIntent().getExtras();

        Long productId = null;
        if (args != null) {
            if (args.containsKey(EXTRA_PRODUCT_ID)) {
                productId = args.getLong(EXTRA_PRODUCT_ID);
            }
        }

        if (productId == null) {
            throw new RuntimeException("Product Id required in args!");
        }

        this.mProductId = productId;

        // Toolbar
        this.setSupportActionBar(this.mBinding.toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        // View Pager
        this.mBinding.viewPager.setAdapter(new FragmentPageAdapter(this.getSupportFragmentManager()));

        this.mBinding.tabsContainer.setupWithViewPager(this.mBinding.viewPager);
    }

    private class FragmentPageAdapter extends FragmentPagerAdapter {

        private FragmentPageAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return ProductInfoFragment.newInstance(ProductActivity.this.mProductId);
                default:
                    return null;
            }
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Info";
                default:
                    return null;
            }
        }
    }

    /*
    ************************************************************************************************
    * ProductInfoFragment Interactions
    ************************************************************************************************
     */

    private void onRequestAddProductPriceResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PRODUCT_ADD_PRICE && resultCode == RESULT_OK) {
//            ProductInfoFragment fragment = (ProductInfoFragment) this.getSupportFragmentManager().findFragmentByTag(FRAGMENT_PRODUCT);
//            if (fragment != null) {
////                fragment.updateProductPriceItemList();
//            }
        }
    }
//    private class ProductFragmentInteractionListener implements ProductInfoFragment.InteractionListener {
//
//        @Override
//        public void onPriceSelected(ProductInfoFragment fragment, PriceModel price) {
//
//        }
//
//        @Override
//        public void onStoreSelected(ProductInfoFragment fragment, StoreModel store) {
//            Toast.makeText(ProductActivity.this, "Store Selected: " + store.name, Toast.LENGTH_SHORT).show();
//            // TODO: link to Store Activity?
//        }
//    }

}
