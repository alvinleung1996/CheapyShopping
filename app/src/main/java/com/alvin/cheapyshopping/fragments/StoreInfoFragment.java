package com.alvin.cheapyshopping.fragments;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alvin.cheapyshopping.R;
import com.alvin.cheapyshopping.StoreActivity;
import com.alvin.cheapyshopping.databinding.StoreInfoFragmentBinding;
import com.alvin.cheapyshopping.db.entities.Store;
import com.alvin.cheapyshopping.viewmodels.StoreInfoFragmentViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * Created by cheng on 11/26/2017.
 */

public class StoreInfoFragment extends Fragment{

    /*
    ************************************************************************************************
    * Store Fragment Setup
    ************************************************************************************************
     */


    public static StoreInfoFragment newInstance(String storeId) {
        StoreInfoFragment fragment = new StoreInfoFragment();

        Bundle args = new Bundle();
        args.putString(StoreActivity.EXTRA_STORE_ID, storeId);
        fragment.setArguments(args);
        return fragment;
    }


    private StoreInfoFragmentBinding mBinding;
    private StoreInfoFragmentViewModel mViewModel;

    private GoogleMap mMap;

    private String mCurrentStoreId;
    private Store mCurrentStore;


    public StoreInfoFragment(){
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(false);

        // Get Product ID
        Bundle args = getArguments();
        mCurrentStoreId= args.getString(StoreActivity.EXTRA_STORE_ID);

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
        this.mViewModel = ViewModelProviders.of(this).get(StoreInfoFragmentViewModel.class);

        ((SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.fragment_map))
                .getMapAsync(new MapReadyCallback());


        // Get Store
        this.mViewModel.getStore(mCurrentStoreId).observe(this, new Observer<Store>() {
            @Override
            public void onChanged(@Nullable Store store) {
                StoreInfoFragment.this.mCurrentStore = store;
                // Setup Store Basic Info
                mBinding.setStore(mCurrentStore);
                StoreInfoFragment.this.updateMapMarkers();
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

    private void updateMapMarkers() {
        if (this.mMap == null) {
            return;
        }
        this.mMap.clear();
        if (this.mCurrentStore != null) {
            LatLng coordinate = new LatLng(mCurrentStore.getLatitude(), mCurrentStore.getLongitude());
            this.mMap.addMarker(new MarkerOptions().position(coordinate).title(mCurrentStore.getName()));
            this.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 16));
        }
    }
    /*
    ************************************************************************************************
    * Menu
    ************************************************************************************************
     */

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//        inflater.inflate(R.menu.store_fragment_menu, menu);
//    }
//
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.item_favourite:
//                // TODO: Add store to favourite
//                return true;
//            case R.id.item_edit:
//                // TODO: Edit store information
//                return true;
//            case R.id.item_add_product:
//                // TODO: Add new product to store
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }


    /*
    ************************************************************************************************
    * On Click
    ************************************************************************************************
     */

//    private void onProductClick(View view, Product product) {
//        Intent intent = new Intent(this.getContext(), ProductActivity.class);
//        intent.putExtra(ProductActivity.ARGUMENT_PRODUCT_ID, product.getProductId());
//        this.startActivity(intent);
//    }

    /*
    ************************************************************************************************
    * StoreActivity.FloatingActionButtonInteractionListener
    ************************************************************************************************
     */



}
