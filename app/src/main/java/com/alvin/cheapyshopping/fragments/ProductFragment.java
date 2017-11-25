package com.alvin.cheapyshopping.fragments;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


import com.alvin.cheapyshopping.R;
import com.alvin.cheapyshopping.databinding.ProductFragmentBinding;
import com.alvin.cheapyshopping.databinding.ProductStorePriceItemBinding;
import com.alvin.cheapyshopping.db.entities.Price;
import com.alvin.cheapyshopping.db.entities.Product;
import com.alvin.cheapyshopping.db.entities.Store;
import com.alvin.cheapyshopping.db.entities.pseudo.StorePrice;
import com.alvin.cheapyshopping.viewmodels.ProductFragmentViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by cheng on 11/21/2017.
 */

public class ProductFragment extends Fragment {


    public static final String EXTRA_PRODUCT_ID = "com.alvin.cheapyshopping.fragments.ProductFragment.EXTRA_PRODUCT_ID";


    private class ProductStorePriceListAdapter extends RecyclerView.Adapter<ProductStorePriceListAdapter.ProductStorePriceListItemViewHolder> {

        private List<StorePrice> mStorePrices;

        private ProductStorePriceListAdapter() {
            this.mStorePrices = new ArrayList<>();
        }

        public int getItemCount() {
            return mStorePrices.size();
        }

        public class ProductStorePriceListItemViewHolder extends RecyclerView.ViewHolder {
            private ProductStorePriceItemBinding mBinding;

            private ProductStorePriceListItemViewHolder(View v) {
                super(v);
                this.mBinding = DataBindingUtil.getBinding(this.itemView);
            }
        }

        @Override
        public ProductStorePriceListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = ProductStorePriceItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false).getRoot();
            ProductStorePriceListItemViewHolder viewHolder = new ProductStorePriceListItemViewHolder(view);
            return viewHolder;
        }
//
        @Override
        public void onBindViewHolder(ProductStorePriceListItemViewHolder holder, int position) {
            holder.mBinding.setStorePrice(mStorePrices.get(position));

            // Set price update date & time
            SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy     HH:mm:ss");
            String updateDate = formatter.format(mStorePrices.get(position).getCreationTime());
            holder.mBinding.setDate(updateDate);
        }


        private void setStorePriceItems(List<StorePrice> items){
            this.mStorePrices = items;
            this.notifyDataSetChanged();
        }

        @Override
        public void onViewRecycled(ProductStorePriceListItemViewHolder holder) {
            super.onViewRecycled(holder);
            holder.mBinding.setStorePrice(null);
        }
    } // End of ProductStorePriceListAdapter



    public static ProductFragment newInstance(long productID) {
        ProductFragment fragment = new ProductFragment();

        Bundle args = new Bundle();
        args.putLong(EXTRA_PRODUCT_ID, productID);
        fragment.setArguments(args);
        return fragment;
    }


    private ProductFragmentBinding mBinding;
    private ProductFragmentViewModel mViewModel;

    private long mCurrentProductID;
    private StorePrice mCurrentBestProductStorePrice;
    private Product mCurrentProduct;
    private Map<Price, Store> mPriceStoreMap;

    private ProductStorePriceListAdapter mProductStorePriceItemListAdapter;

//    private InteractionListener mInteractionListener;

    public ProductFragment(){
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);

        // Get Product ID
        Bundle args = getArguments();
        mCurrentProductID= args.getLong(EXTRA_PRODUCT_ID, 0);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.mBinding = ProductFragmentBinding.inflate(inflater, container, false);

        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Setup viewModel
        this.mViewModel = ViewModelProviders.of(this).get(ProductFragmentViewModel.class);

        // Setup recycler view
        this.mBinding.listProductPriceItems.setLayoutManager(new LinearLayoutManager(this.getContext()));
        this.mBinding.listProductPriceItems.setNestedScrollingEnabled(false); // For smoother scrolling
        this.mProductStorePriceItemListAdapter = new ProductStorePriceListAdapter();
        mBinding.listProductPriceItems.setAdapter(mProductStorePriceItemListAdapter);


        // Get Product
        this.mViewModel.getProduct(mCurrentProductID).observe(this, new Observer<Product>() {
            @Override
            public void onChanged(@Nullable Product product) {
                ProductFragment.this.mCurrentProduct = product;

                // Setup Product Basic Info
                mBinding.setProduct(mCurrentProduct);
            }
        });

        // Get StorePrice list
        this.mViewModel.getStorePrices(mCurrentProductID).observe(this, new Observer<List<StorePrice>>() {
            @Override
            public void onChanged(@Nullable List<StorePrice> storePrices) {
                ProductFragment.this.mProductStorePriceItemListAdapter.setStorePriceItems(storePrices);
            }
        });

        // Get best StorePrice
        this.mViewModel.getBestStorePrice(mCurrentProductID).observe(this, new Observer<StorePrice>() {
            @Override
            public void onChanged(@Nullable StorePrice storePrice) {
                ProductFragment.this.mCurrentBestProductStorePrice = storePrice;
                ProductFragment.this.mBinding.setBestPrice(storePrice);

                // Set price update date & time
                SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
                String updateDate = formatter.format(storePrice.getCreationTime());
                ProductFragment.this.mBinding.setBestPriceDate(updateDate);

            }
        });


    }


    /*
    ************************************************************************************************
    * Menu
    ************************************************************************************************
     */

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.toolbar_menu_product, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_save:
                return true;
            case R.id.item_edit:
                return true;
            case R.id.item_add_price:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
