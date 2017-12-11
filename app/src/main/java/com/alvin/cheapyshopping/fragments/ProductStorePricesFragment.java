package com.alvin.cheapyshopping.fragments;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.alvin.cheapyshopping.databinding.ProductStorePriceItemBinding;
import com.alvin.cheapyshopping.databinding.ProductStorePricesFragmentBinding;
import com.alvin.cheapyshopping.db.entities.pseudo.StorePrice;
import com.alvin.cheapyshopping.viewmodels.ProductStorePricesFragmentViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductStorePricesFragment extends Fragment {

    private static final String ARGUMENT_PRODUCT_ID = "com.alvin.cheapyshopping.fragments.ProductStorePricesFragment.ARGUMENT_PRODUCT_ID";

    public interface InteractionListener {
        void onGoToStoreClicked(String storeId);
    }

    public static ProductStorePricesFragment newInstance(String productId) {
        Bundle args = new Bundle();
        args.putString(ARGUMENT_PRODUCT_ID, productId);
        ProductStorePricesFragment fragment = new ProductStorePricesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private InteractionListener mInteractionListener;

    private String productId;
    private ProductStorePricesFragmentViewModel mViewModel;

    private ProductStorePricesFragmentBinding mBinding;

    private StorePriceListAdapter mStorePriceListAdapter;


    public ProductStorePricesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = this.getArguments();
        String productId = null;
        if (args != null) {
            if (args.containsKey(ARGUMENT_PRODUCT_ID)) {
                productId = args.getString(ARGUMENT_PRODUCT_ID);
            }
        }
        if (productId == null) {
            throw new RuntimeException("Product Id required!");
        }
        this.productId = productId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.mBinding = ProductStorePricesFragmentBinding.inflate(inflater, container, false);
        return this.mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.mViewModel = ViewModelProviders.of(this).get(ProductStorePricesFragmentViewModel.class);

        this.mStorePriceListAdapter = new StorePriceListAdapter();
        this.mBinding.listStorePrices.setAdapter(this.mStorePriceListAdapter);
        this.mBinding.listStorePrices.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));

        registerForContextMenu(this.mBinding.listStorePrices);

        this.mViewModel.findProductStorePrices(this.productId).observe(this, new Observer<List<StorePrice>>() {
            @Override
            public void onChanged(@Nullable List<StorePrice> storePrices) {
                ProductStorePricesFragment.this.mStorePriceListAdapter
                        .setStorePrices(storePrices == null ? new ArrayList<StorePrice>() : storePrices);
            }
        });
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        String storeId ;
        try{
            storeId = this.mStorePriceListAdapter.getStoreId();
        }catch (Exception e){
            Log.d("Context Menu: ",e.getLocalizedMessage(),e);
            return super.onContextItemSelected(item);
        }

        if(storeId != null){
            this.onGoToStoreClicked(storeId);
        }

        return super.onContextItemSelected(item);
    }

    public void setInteractionListener(InteractionListener interactionListener) {
        this.mInteractionListener = interactionListener;
    }

    private void onGoToStoreClicked(String storeId){
        if(this.mInteractionListener != null){
            this.mInteractionListener.onGoToStoreClicked(storeId);
        }
    }

    /*
    ************************************************************************************************
    * List
    ************************************************************************************************
     */

    private class StorePriceListAdapter extends RecyclerView.Adapter<StorePriceItemHolder> {

        private List<StorePrice> mStorePrices;
        public String mStoreId;

        public String getStoreId(){
            return mStoreId;
        }

        public void setStoreId(String storeId){
            this.mStoreId = storeId;
        }

        private StorePriceListAdapter() {
            this.mStorePrices = new ArrayList<>();
        }

        @Override
        public int getItemCount() {
            return this.mStorePrices.size();
        }

        @Override
        public StorePriceItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new StorePriceItemHolder(parent);
        }

        @Override
        public void onBindViewHolder(StorePriceItemHolder holder, final int position) {
            holder.onBind(this.mStorePrices.get(position));
            holder.mBinding.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    setStoreId(mStorePrices.get(position).getForeignStoreId());
                    return false;
                }
            });
        }

        @Override
        public void onViewRecycled(StorePriceItemHolder holder) {
            holder.onRecycled();
        }

        private void setStorePrices(List<StorePrice> storePrices) {
            this.mStorePrices = storePrices;
            this.notifyDataSetChanged();
        }

    }

    private class StorePriceItemHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        public final ProductStorePriceItemBinding mBinding;

        private StorePriceItemHolder(ViewGroup parent) {
            super(ProductStorePriceItemBinding
                    .inflate(LayoutInflater.from(parent.getContext()), parent, false)
                    .getRoot());
            this.mBinding = DataBindingUtil.getBinding(this.itemView);
            this.mBinding.setCreationTimeFormatter(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss"));

            parent.setOnCreateContextMenuListener(this);
        }

        private void onBind(StorePrice storePrice) {
            this.mBinding.setStorePrice(storePrice);
            this.mBinding.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ProductStorePricesFragment.this.onStorePriceItemClick(view,
                            StorePriceItemHolder.this.mBinding.getStorePrice());
                }
            });

        }

        private void onRecycled() {
            this.mBinding.setStorePrice(null);
            this.mBinding.setOnClickListener(null);
            this.mBinding.setOnLongClickListener(null);
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.add("Go to store");
        }

    }



    /*
    ************************************************************************************************
    * Item
    ************************************************************************************************
     */

    private void onStorePriceItemClick(View view, StorePrice storePrice) {
        Log.d("item", "price clicked");
    }

    private boolean onStorePriceItemLongClick(View view, StorePrice storePrice){
        Log.d("item", "price long clicked");
        return true;
    }


    /*
    ************************************************************************************************
    * Context Menu
    ************************************************************************************************
     */
}
