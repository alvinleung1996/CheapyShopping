package com.alvin.cheapyshopping;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.alvin.cheapyshopping.databinding.ProductActivityBinding;
import com.alvin.cheapyshopping.fragments.ProductInfoFragment;
import com.alvin.cheapyshopping.fragments.ProductStorePricesFragment;

/**
 * Created by cheng on 11/13/2017.
 */

public class ProductActivity extends AppCompatActivity {

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
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // View Pager
        this.mBinding.viewPager.setAdapter(new FragmentPageAdapter(this.getSupportFragmentManager()));

        this.mBinding.tabsContainer.setupWithViewPager(this.mBinding.viewPager);

        // Floating Action Button
        this.mBinding.setOnFloatingActionButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProductActivity.this.onFloatingActionButtonClick((FloatingActionButton) view);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class FragmentPageAdapter extends FragmentPagerAdapter {

        private FragmentPageAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return ProductInfoFragment.newInstance(ProductActivity.this.mProductId);
                case 1:
                    return ProductStorePricesFragment.newInstance(ProductActivity.this.mProductId);
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
                case 1:
                    return "Store Prices";
                default:
                    return null;
            }
        }
    }


    private void onFloatingActionButtonClick(FloatingActionButton fab) {
        Intent intent = new Intent(this.getApplicationContext(), AddPriceActivity.class);

    }

}
