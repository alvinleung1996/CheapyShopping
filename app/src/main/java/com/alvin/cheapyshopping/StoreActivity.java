package com.alvin.cheapyshopping;


import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.alvin.cheapyshopping.databinding.StoreActivityBinding;
import com.alvin.cheapyshopping.fragments.StoreInfoFragment;
import com.alvin.cheapyshopping.fragments.StoreProductPricesFragment;

/**
 * Created by cheng on 11/26/2017.
 */

public class StoreActivity extends AppCompatActivity {

    public static final String EXTRA_STORE_ID = "com.alvin.cheapyshopping.StoreActivity.EXTRA_STORE_ID";


    public interface FloatingActionButtonInteractionListener {

        void onFloatingActionButtonClick(FloatingActionButton button);

    }


    private StoreActivityBinding mBinding;

    private String mStoreId;

    private Fragment mActiveFragment;

    FragmentPageAdapter mFragmentPageAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.mBinding = DataBindingUtil.setContentView(this, R.layout.activity_store);

        Bundle args = getIntent().getExtras();

        Log.d("Debug", "Created Store Activity");

        String storeId = null;
        if (args != null) {
            if (args.containsKey(EXTRA_STORE_ID)) {
                storeId = args.getString(EXTRA_STORE_ID);
            }
        }

        if (storeId == null) {
            throw new RuntimeException("Product Id required in args!");
        }

        this.mStoreId = storeId;


        // Toolbar
        this.setSupportActionBar(this.mBinding.toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // View Pager
        mFragmentPageAdapter = new FragmentPageAdapter(this.getSupportFragmentManager());
        this.mBinding.viewPager.setAdapter(mFragmentPageAdapter);

        this.mBinding.tabsContainer.setupWithViewPager(this.mBinding.viewPager);

        // Floating Action Button
        this.mBinding.setOnFloatingActionButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StoreActivity.this.onFloatingActionButtonClick((FloatingActionButton) view);
            }
        });

        this.mBinding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mActiveFragment = mFragmentPageAdapter.getRegisteredFragment(mBinding.viewPager.getCurrentItem());
                if (position == 0){
                    StoreActivity.this.mBinding.fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_edit_white_24dp));
                } else {
                    StoreActivity.this.mBinding.fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_white_24dp));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

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

        SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return StoreInfoFragment.newInstance(StoreActivity.this.mStoreId);
                case 1:
                    return StoreProductPricesFragment.newInstance(StoreActivity.this.mStoreId);
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
                    return "Product Prices";
                default:
                    return null;
            }
        }
    }


    private void onFloatingActionButtonClick(FloatingActionButton fab) {
        if(mActiveFragment != null){
            if(mActiveFragment instanceof StoreInfoFragment){
                Intent intent = new Intent(this.getApplicationContext(), EditStoreActivity.class);
                intent.putExtra(StoreActivity.EXTRA_STORE_ID, mStoreId);
                this.startActivity(intent);
            }else if(mActiveFragment instanceof StoreProductPricesFragment){
                Intent intent = new Intent(this.getApplicationContext(), AddStoreProductActivity.class);
                intent.putExtra(AddStoreProductActivity.EXTRA_STORE_ID, mStoreId);
                this.startActivity(intent);
            }
        }


    }
}
