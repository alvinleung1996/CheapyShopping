package com.alvin.cheapyshopping.fragments;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alvin.cheapyshopping.ProductActivity;
import com.alvin.cheapyshopping.R;
import com.alvin.cheapyshopping.StoreActivity;
import com.alvin.cheapyshopping.databinding.StoreInfoFragmentBinding;
import com.alvin.cheapyshopping.databinding.StoreProductPriceItemBinding;
import com.alvin.cheapyshopping.db.entities.Product;
import com.alvin.cheapyshopping.db.entities.Store;
import com.alvin.cheapyshopping.db.entities.pseudo.ProductPrice;
import com.alvin.cheapyshopping.viewmodels.StoreFragmentViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cheng on 11/26/2017.
 */

public class StoreInfoFragment extends Fragment{


    /*
    ************************************************************************************************
    * Store recycler view setup
    ************************************************************************************************
     */

    private class StoreProductPriceListAdapter extends RecyclerView.Adapter<StoreProductPriceListAdapter.StoreProductPriceListItemViewHolder>{

        private List<ProductPrice> mProductPrices;

        private StoreProductPriceListAdapter(){
            this.mProductPrices = new ArrayList<>();
        }

        public int getItemCount(){
            return mProductPrices.size();
        }

        public class StoreProductPriceListItemViewHolder extends RecyclerView.ViewHolder{
            private StoreProductPriceItemBinding mBinding;

            private StoreProductPriceListItemViewHolder(View v){
                super(v);
                this.mBinding = DataBindingUtil.getBinding(this.itemView);
            }
        }

        @Override
        public StoreProductPriceListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = StoreProductPriceItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false).getRoot();
            StoreProductPriceListItemViewHolder viewHolder = new StoreProductPriceListItemViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(StoreProductPriceListItemViewHolder holder, final int position) {

            holder.mBinding.setProductPrice(mProductPrices.get(position));

            holder.mBinding.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    StoreInfoFragment.this.onProductClick(view, mProductPrices.get(position).getProduct());
                }
            });

//            // Set price update date & time
//            SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy     HH:mm:ss");
//            String updateDate = formatter.format(mProductPrices.get(position).getCreationTime());
//            holder.mBinding.setDate(updateDate);
        }

        private void setStoreProductPriceItems(List<ProductPrice> items){
            this.mProductPrices = items;
            this.notifyDataSetChanged();
        }

        @Override
        public void onViewRecycled(StoreProductPriceListItemViewHolder holder) {
            super.onViewRecycled(holder);
        }
    }



    /*
    ************************************************************************************************
    * Store Fragment Setup
    ************************************************************************************************
     */


    public static StoreInfoFragment newInstance(long storeId) {
        StoreInfoFragment fragment = new StoreInfoFragment();

        Bundle args = new Bundle();
        args.putLong(StoreActivity.EXTRA_STORE_ID, storeId);
        fragment.setArguments(args);
        return fragment;
    }


    private StoreInfoFragmentBinding mBinding;
    private StoreFragmentViewModel mViewModel;

    private GoogleMap mMap;

    private long mCurrentStoreId;
    private Store mCurrentStore;
    private StoreProductPriceListAdapter mStoreProductPriceListAdapter;


    public StoreInfoFragment(){
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);

        // Get Product ID
        Bundle args = getArguments();
        mCurrentStoreId= args.getLong(StoreActivity.EXTRA_STORE_ID, 0);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.mBinding = StoreInfoFragmentBinding.inflate(inflater, container, false);

        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        // Setup viewModel
        this.mViewModel = ViewModelProviders.of(this).get(StoreFragmentViewModel.class);

        ((SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.fragment_map))
                .getMapAsync(new MapReadyCallback());

        // Setup recycler view
        this.mBinding.listProductPriceItems.setLayoutManager(new LinearLayoutManager(this.getContext()));
        this.mBinding.listProductPriceItems.setNestedScrollingEnabled(false); // For smoother scrolling
        this.mStoreProductPriceListAdapter = new StoreProductPriceListAdapter();
        mBinding.listProductPriceItems.setAdapter(mStoreProductPriceListAdapter);

        // Get Store
        this.mViewModel.getStore(mCurrentStoreId).observe(this, new Observer<Store>() {
            @Override
            public void onChanged(@Nullable Store store) {
                StoreInfoFragment.this.mCurrentStore = store;

                // Setup Store Basic Info
                mBinding.setStore(mCurrentStore);
            }
        });


        // Get Store ProductPrice list
        this.mViewModel.getProductPriceList(mCurrentStoreId).observe(this, new Observer<List<ProductPrice>>() {
            @Override
            public void onChanged(@Nullable List<ProductPrice> productPrices) {
                StoreInfoFragment.this.mStoreProductPriceListAdapter.setStoreProductPriceItems(productPrices);
            }
        });
    }

    /*
    ************************************************************************************************
    * Google Map
    ************************************************************************************************
     */

    private class MapReadyCallback implements OnMapReadyCallback {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            StoreInfoFragment.this.mMap = googleMap;
            if (googleMap != null) {
                try {
                    googleMap.setMyLocationEnabled(true);
                    googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                } catch (SecurityException e) {
                    Toast.makeText(StoreInfoFragment.this.getContext(), "Security Exception when setting google map", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }
    /*
    ************************************************************************************************
    * Menu
    ************************************************************************************************
     */

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.store_fragment_menu, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_favourite:
                // TODO: Add store to favourite
                return true;
            case R.id.item_edit:
                // TODO: Edit store information
                return true;
            case R.id.item_add_product:
                // TODO: Add new product to store
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /*
    ************************************************************************************************
    * On Click
    ************************************************************************************************
     */

    private void onProductClick(View view, Product product) {
        Intent intent = new Intent(this.getContext(), ProductActivity.class);
        intent.putExtra(ProductActivity.EXTRA_PRODUCT_ID, product.getProductId());
        this.startActivity(intent);
    }

}
