package com.alvin.cheapyshopping.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alvin.cheapyshopping.AddProductActivity;
import com.alvin.cheapyshopping.MainActivity;
import com.alvin.cheapyshopping.ProductActivity;
import com.alvin.cheapyshopping.R;
import com.alvin.cheapyshopping.databinding.DetailProductItemBinding;
import com.alvin.cheapyshopping.databinding.ProductListFragmentBinding;
import com.alvin.cheapyshopping.db.entities.Product;
import com.alvin.cheapyshopping.viewmodels.ProductListFragmentViewModel;

import java.util.ArrayList;
import java.util.List;


public class ProductListFragment extends MainActivity.MainFragment {

    public class ProductListAdapter extends RecyclerView.Adapter<ProductItemHolder> {

        private List<Product> mProducts;

        private ProductListAdapter() {
            this.mProducts = new ArrayList<>();
        }

        @Override
        public int getItemCount() {
            return this.mProducts.size();
        }

        @Override
        public ProductItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ProductItemHolder(parent);
        }

        @Override
        public void onBindViewHolder(ProductItemHolder holder, int position) {
            Product store = this.mProducts.get(position);
            holder.onBind(store);
        }

        @Override
        public void onViewRecycled(ProductItemHolder holder) {
            super.onViewRecycled(holder);
            holder.onRecycled();
        }

        private void setProducts(List<Product> stores) {
            this.mProducts = stores;
            this.notifyDataSetChanged();
        }
    }

    private class ProductItemHolder extends RecyclerView.ViewHolder {

        private final DetailProductItemBinding mBinding;

        private ProductItemHolder(ViewGroup parent) {
            super(DetailProductItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false).getRoot());
            this.mBinding = DataBindingUtil.getBinding(this.itemView);
        }

        private void onBind(Product store) {
            this.mBinding.setProduct(store);
            this.mBinding.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ProductListFragment.this.onProductItemClick(view, ProductListFragment.ProductItemHolder.this.mBinding.getProduct());
                }
            });
        }

        private void onRecycled() {
            this.mBinding.setProduct(null);
            this.mBinding.setOnClickListener(null);
        }

    }


    public static ProductListFragment newInstance() {
        ProductListFragment fragment = new ProductListFragment();
        return fragment;
    }


    private ProductListFragmentViewModel mViewModel;

    private ProductListFragmentBinding mBinding;

    private ProductListAdapter mProductListAdapter;


    public ProductListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.mBinding = ProductListFragmentBinding.inflate(inflater, container, false);
        return this.mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.mViewModel = ViewModelProviders.of(this).get(ProductListFragmentViewModel.class);

        this.mBinding.listProducts.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));

        this.mProductListAdapter = new ProductListAdapter();
        this.mBinding.listProducts.setAdapter(this.mProductListAdapter);

        this.mViewModel.getAllProducts().observe(this, new Observer<List<Product>>() {
            @Override
            public void onChanged(@Nullable List<Product> stores) {
                ProductListFragment.this.mProductListAdapter.setProducts(stores);
            }
        });

        setFloatingActionButtonOptions(new MainActivity.FloatingActionButtonOptions(
                R.drawable.ic_add_white_24dp, this::onFloatingActionButtonClick));
    }

    /*
    ************************************************************************************************
    * MainActivity.FloatingActionButtonInteractionListener
    ************************************************************************************************
     */

    public void onFloatingActionButtonClick(FloatingActionButton button) {
        Intent intent = new Intent(this.getContext(), AddProductActivity.class);
        this.startActivity(intent);
    }

    /*
    ************************************************************************************************
    * UI views interaction
    ************************************************************************************************
     */

    public void onProductItemClick(View view, Product product) {
        Intent intent = new Intent(this.getContext(), ProductActivity.class);
        intent.putExtra(ProductActivity.EXTRA_PRODUCT_ID, product.getProductId());
        this.startActivity(intent);

    }

}
