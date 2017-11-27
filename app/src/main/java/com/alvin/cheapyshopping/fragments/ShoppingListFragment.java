package com.alvin.cheapyshopping.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alvin.cheapyshopping.AddShoppingListActivity;
import com.alvin.cheapyshopping.AddShoppingListProductRelationActivity;
import com.alvin.cheapyshopping.MainActivity;
import com.alvin.cheapyshopping.ProductActivity;
import com.alvin.cheapyshopping.R;
import com.alvin.cheapyshopping.SampleMapsActivity;
import com.alvin.cheapyshopping.StoreActivity;
import com.alvin.cheapyshopping.databinding.ShoppingListFragmentBinding;
import com.alvin.cheapyshopping.databinding.ShoppingListProductItemBinding;
import com.alvin.cheapyshopping.databinding.ShoppingListStoreItemBinding;
import com.alvin.cheapyshopping.db.entities.Account;
import com.alvin.cheapyshopping.db.entities.Product;
import com.alvin.cheapyshopping.db.entities.ShoppingList;
import com.alvin.cheapyshopping.db.entities.Store;
import com.alvin.cheapyshopping.db.entities.pseudo.ShoppingListProduct;
import com.alvin.cheapyshopping.viewmodels.ShoppingListFragmentViewModel;
import com.alvin.cheapyshopping.viewmodels.ShoppingListFragmentViewModel.ShoppingListItem;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ShoppingListFragment extends Fragment implements MainActivity.FloatingActionButtonInteractionListener {

    private static final int REQUEST_ADD_SHOPPING_LIST = 1;




    private class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListItemViewHolder> {

        private List<ShoppingListItem> mShoppingListItems;

        private ShoppingListAdapter() {
            this.mShoppingListItems = new ArrayList<>();
        }

        @Override
        public int getItemViewType(int position) {
            return this.mShoppingListItems.get(position).type;
        }

        @Override
        public int getItemCount() {
            return this.mShoppingListItems.size();
        }

        @Override
        public ShoppingListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case ShoppingListItem.TYPE_STORE:
                    return new StoreItemViewHolder(parent);
                case ShoppingListItem.TYPE_PRODUCT:
                    return new ProductItemViewHolder(parent);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(ShoppingListItemViewHolder holder, int position) {
            ShoppingListItem item = this.mShoppingListItems.get(position);
            switch (item.type) {
                case ShoppingListItem.TYPE_STORE:
                    ((StoreItemViewHolder) holder).onBind(item.store);
                    break;
                case ShoppingListItem.TYPE_PRODUCT:
                    ((ProductItemViewHolder) holder).onBind(item.product);
                    break;
            }
        }

        @Override
        public void onViewRecycled(ShoppingListItemViewHolder holder) {
            super.onViewRecycled(holder);
            holder.onRecycled();

        }

        private void setShoppingListItems(List<ShoppingListItem> items) {
            this.mShoppingListItems = items;
            this.notifyDataSetChanged();
        }
    }

    private abstract class ShoppingListItemViewHolder extends RecyclerView.ViewHolder {

        private ShoppingListItemViewHolder(View v) {
            super(v);
        }

        protected abstract void onRecycled();
    }

    private class StoreItemViewHolder extends ShoppingListItemViewHolder {

        private ShoppingListStoreItemBinding mBinding;

        private StoreItemViewHolder(ViewGroup parent) {
            super(ShoppingListStoreItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false).getRoot());
            this.mBinding = DataBindingUtil.getBinding(this.itemView);
        }

        private void onBind(Store store) {
            this.mBinding.setStore(store);
            this.mBinding.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ShoppingListFragment.this.onStoreItemClick(view, StoreItemViewHolder.this.mBinding.getStore());
                }
            });
        }

        @Override
        protected void onRecycled() {
            this.mBinding.setStore(null);
            this.mBinding.setOnClickListener(null);
        }
    }

    private class ProductItemViewHolder extends ShoppingListItemViewHolder {

        private ShoppingListProductItemBinding mBinding;

        private ProductItemViewHolder(ViewGroup parent) {
            super(ShoppingListProductItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false).getRoot());
            this.mBinding = DataBindingUtil.getBinding(this.itemView);
        }

        private void onBind(Product product) {
            this.mBinding.setProduct(product);
            this.mBinding.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ShoppingListFragment.this.onProductItemClick(view, ProductItemViewHolder.this.mBinding.getProduct());
                }
            });
        }

        @Override
        protected void onRecycled() {
            this.mBinding.setProduct(null);
            this.mBinding.setOnClickListener(null);
        }
    }



    public static ShoppingListFragment newInstance() {
        ShoppingListFragment fragment = new ShoppingListFragment();
        return fragment;
    }


    public ShoppingListFragment() {

    }


    private ShoppingListFragmentViewModel mViewModel;
    private ShoppingListFragmentBinding mBinding;
    private ShoppingListAdapter mShoppingListItemListAdapter;

    private GoogleMap mMap;

    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private Account mCurrentAccount;
    private List<ShoppingList> mCurrentAccountShoppingLists;
    private ShoppingList mCurrentAccountActiveShoppingList;
    private Map<Store, List<ShoppingListProduct>> mCurrentAccountGroupedActiveShoppingListProducts;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.mBinding = ShoppingListFragmentBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.mViewModel = ViewModelProviders.of(this).get(ShoppingListFragmentViewModel.class);

        ((SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.fragment_map))
                .getMapAsync(new MapReadyCallback());

        this.mBinding.listShoppingListItems.setLayoutManager(new LinearLayoutManager(this.getContext()));
        this.mBinding.listShoppingListItems.setNestedScrollingEnabled(false);

        this.mShoppingListItemListAdapter = new ShoppingListAdapter();
        this.mBinding.listShoppingListItems.setAdapter(this.mShoppingListItemListAdapter);

        // Current Account
        this.mViewModel.findCurrentAccount().observe(this, new Observer<Account>() {
            @Override
            public void onChanged(@Nullable Account account) {
                ShoppingListFragment.this.mCurrentAccount = account;
            }
        });

        // Current Account All Shopping Lists
        this.mViewModel.findCurrentAccountShoppingLists().observe(this, new Observer<List<ShoppingList>>() {
            @Override
            public void onChanged(@Nullable List<ShoppingList> shoppingLists) {
                ShoppingListFragment.this.mCurrentAccountShoppingLists = shoppingLists;
                if (ShoppingListFragment.this.getActivity() != null) {
                    ShoppingListFragment.this.getActivity().invalidateOptionsMenu();
                }
            }
        });

        // Current Account Active Shopping List
        this.mViewModel.findCurrentAccountActiveShoppingList().observe(this, new Observer<ShoppingList>() {
            @Override
            public void onChanged(@Nullable ShoppingList shoppingList) {
                final ShoppingListFragment fragment = ShoppingListFragment.this;

                fragment.mCurrentAccountActiveShoppingList = shoppingList;
                ShoppingListFragment.this.mBinding.setShoppingList(shoppingList);

                if (fragment.mShoppingListIdMenuItemMap != null) {
                    for (MenuItem item : fragment.mShoppingListIdMenuItemMap.values()) {
                        item.setChecked(false);
                    }
                    if (shoppingList != null
                        && fragment.mShoppingListIdMenuItemMap.containsKey(shoppingList.getShoppingListId())) {

                        fragment.mShoppingListIdMenuItemMap
                                .get(shoppingList.getShoppingListId())
                                .setChecked(true);
                    }
                }

                ShoppingListFragment.this.updateMapMarkers();
            }
        });

        // Current Account Grouped Active Shopping List Products
        this.mViewModel.findCurrentAccountGroupedActiveShoppingListProducts().observe(this, new Observer<Map<Store, List<ShoppingListProduct>>>() {
            @Override
            public void onChanged(@Nullable Map<Store, List<ShoppingListProduct>> storeListMap) {
                ShoppingListFragment.this.mCurrentAccountGroupedActiveShoppingListProducts = storeListMap;
                ShoppingListFragment.this.updateMapMarkers();
            }
        });

        // Shopping List Items
        this.mViewModel.findCurrentAccountShoppingListItems().observe(this, new Observer<List<ShoppingListItem>>() {
            @Override
            public void onChanged(@Nullable List<ShoppingListItem> items) {
                ShoppingListFragment.this.mShoppingListItemListAdapter
                        .setShoppingListItems(items);
            }
        });


        this.mGeoDataClient = Places.getGeoDataClient(this.getContext(),     null);
        this.mPlaceDetectionClient = Places.getPlaceDetectionClient(this.getContext(), null);
        this.mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.getContext());
    }


    /*
    ************************************************************************************************
    * Menu
    ************************************************************************************************
     */

    private Map<String, MenuItem> mShoppingListIdMenuItemMap;
    private Map<MenuItem, String> mMenuItemShoppingListIdMap;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.shopping_list_fragment_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.item_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        // TODO: Add listener to search view

        MenuItem itemToCheck = null;

        this.mShoppingListIdMenuItemMap = new ArrayMap<>();
        this.mMenuItemShoppingListIdMap = new ArrayMap<>();
        if (this.mCurrentAccountShoppingLists != null) {

            for (ShoppingList list : this.mCurrentAccountShoppingLists) {

                MenuItem item = menu.add(R.id.group_shopping_lists, Menu.NONE, Menu.NONE, list.getName());

                this.mShoppingListIdMenuItemMap.put(list.getShoppingListId(), item);
                this.mMenuItemShoppingListIdMap.put(item, list.getShoppingListId());

                if (this.mCurrentAccountActiveShoppingList != null
                        && list.getShoppingListId() == this.mCurrentAccountActiveShoppingList.getShoppingListId()) {
                    itemToCheck = item;
                }
            }
        }

        menu.setGroupCheckable(R.id.group_shopping_lists, true, true);
        if (itemToCheck != null) {
            itemToCheck.setChecked(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_add_shopping_list:
                this.onAddShoppingListOptionSelected(item);
                return true;
            case R.id.item_refresh_best_price:
                this.onRefreshBestPriceOptionSelected(item);
                return true;
            case R.id.item_sample_map_activity:
                Intent intent = new Intent(this.getContext(), SampleMapsActivity.class);
                this.startActivity(intent);
        }

        if (this.mMenuItemShoppingListIdMap != null && this.mMenuItemShoppingListIdMap.containsKey(item)) {
            this.onShoppingListOptionSelected(item, this.mMenuItemShoppingListIdMap.get(item));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void onAddShoppingListOptionSelected(MenuItem item) {
        if (this.mCurrentAccount != null) {
            Intent intent = new Intent(this.getContext(), AddShoppingListActivity.class);
            intent.putExtra(AddShoppingListActivity.EXTRA_ACCOUNT_ID, this.mCurrentAccount.getActiveShoppingListId());
            this.startActivityForResult(intent, REQUEST_ADD_SHOPPING_LIST);
        }
    }

    private void onRefreshBestPriceOptionSelected(MenuItem item) {
        if (this.mCurrentAccountActiveShoppingList != null) {
            this.mViewModel.refreshBestPriceRelations(
                    this.getActivity(),
                    this.mCurrentAccountActiveShoppingList.getShoppingListId());
        }
    }

    private void onShoppingListOptionSelected(MenuItem item, String shoppingListId) {
        if (this.mCurrentAccount.getActiveShoppingListId() != shoppingListId) {
            item.setChecked(true);
            this.mViewModel.setShoppingListId(shoppingListId);
        }
    }


    /*
    ************************************************************************************************
    * Map
    ************************************************************************************************
     */

    private class MapReadyCallback implements OnMapReadyCallback {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            ShoppingListFragment.this.mMap = googleMap;
            if (googleMap != null) {
                try {
                    googleMap.setMyLocationEnabled(true);
                    googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                } catch (SecurityException e) {
                    Toast.makeText(ShoppingListFragment.this.getContext(), "Security Exception when setting google map", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    private void updateMapMarkers() {
        if (this.mMap == null) {
            return;
        }
        this.mMap.clear();
        if (this.mCurrentAccountGroupedActiveShoppingListProducts != null) {
            for (Store store : this.mCurrentAccountGroupedActiveShoppingListProducts.keySet()) {
                if (store != null) {
                    LatLng coordinate = new LatLng(store.getLatitude(), store.getLongitude());
                    this.mMap.addMarker(new MarkerOptions().position(coordinate).title(store.getName()));
                }
            }

        }
        if (this.mCurrentAccountActiveShoppingList != null) {
            Double longitude = this.mCurrentAccountActiveShoppingList.getCenterLongitude();
            Double latitude = this.mCurrentAccountActiveShoppingList.getCenterLatitude();
            if (longitude != null && latitude != null) {
                this.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 16));
            }
        }
    }


    /*
    ************************************************************************************************
    * Floating Action Button
    ************************************************************************************************
     */

    @Override
    public void onConfigureFloatingActionButton(FloatingActionButton button) {

    }

    @Override
    public void onFloatingActionButtonClick(FloatingActionButton button) {
        if (this.mCurrentAccount != null && this.mCurrentAccount.getActiveShoppingListId() != null) {
            Intent intent = new Intent(this.getContext(), AddShoppingListProductRelationActivity.class);
            intent.putExtra(AddShoppingListProductRelationActivity.EXTRA_SHOPPING_LIST_ID,
                    this.mCurrentAccountActiveShoppingList.getShoppingListId());
            this.startActivity(intent);
        }
    }



    private void onStoreItemClick(View view, Store model) {
        Intent intent = new Intent(this.getContext(), StoreActivity.class);
        intent.putExtra(StoreActivity.EXTRA_STORE_ID, model.getStoreId());
        this.startActivity(intent);
    }

    private void onProductItemClick(View view, Product model) {
        Intent intent = new Intent(this.getContext(), ProductActivity.class);
        intent.putExtra(ProductActivity.EXTRA_PRODUCT_ID, model.getProductId());
        this.startActivity(intent);
    }

}
