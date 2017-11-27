package com.alvin.cheapyshopping.fragments;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
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


    public static ProductStorePricesFragment newInstance(long productId) {
        Bundle args = new Bundle();
        args.putLong(ARGUMENT_PRODUCT_ID, productId);
        ProductStorePricesFragment fragment = new ProductStorePricesFragment();
        fragment.setArguments(args);
        return fragment;
    }


    private long productId;

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
        Long productId = null;
        if (args != null) {
            if (args.containsKey(ARGUMENT_PRODUCT_ID)) {
                productId = args.getLong(ARGUMENT_PRODUCT_ID);
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

        this.mViewModel.findProductStorePrices(this.productId).observe(this, new Observer<List<StorePrice>>() {
            @Override
            public void onChanged(@Nullable List<StorePrice> storePrices) {
                ProductStorePricesFragment.this.mStorePriceListAdapter
                        .setStorePrices(storePrices == null ? new ArrayList<StorePrice>() : storePrices);
            }
        });
    }



    /*
    ************************************************************************************************
    * List
    ************************************************************************************************
     */

    private class StorePriceListAdapter extends RecyclerView.Adapter<StorePriceItemHolder> {

        private List<StorePrice> mStorePrices;

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
        public void onBindViewHolder(StorePriceItemHolder holder, int position) {
            holder.onBind(this.mStorePrices.get(position));
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

    private class StorePriceItemHolder extends RecyclerView.ViewHolder {

        private final ProductStorePriceItemBinding mBinding;

        private StorePriceItemHolder(ViewGroup parent) {
            super(ProductStorePriceItemBinding
                    .inflate(LayoutInflater.from(parent.getContext()), parent, false)
                    .getRoot());
            this.mBinding = DataBindingUtil.getBinding(this.itemView);
            this.mBinding.setCreationTimeFormatter(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss"));
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
}
