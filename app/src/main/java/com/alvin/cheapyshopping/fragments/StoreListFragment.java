package com.alvin.cheapyshopping.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alvin.cheapyshopping.MainActivity;
import com.alvin.cheapyshopping.ProductActivity;
import com.alvin.cheapyshopping.StoreActivity;
import com.alvin.cheapyshopping.databinding.DetailStoreItemBinding;
import com.alvin.cheapyshopping.databinding.StoreListFragmentBinding;
import com.alvin.cheapyshopping.db.entities.Store;
import com.alvin.cheapyshopping.viewmodels.StoreListFragmentViewModel;

import java.util.ArrayList;
import java.util.List;


public class StoreListFragment extends Fragment implements MainActivity.FloatingActionButtonInteractionListener {

    public interface InteractionListener {

        void onStoreSelected(StoreListFragment fragment, Store store);

        void onRequestNewStore(StoreListFragment fragment);

    }

    public class StoreListAdapter extends RecyclerView.Adapter<StoreItemHolder> {

        private List<Store> mStores;

        private StoreListAdapter() {
            this.mStores = new ArrayList<>();
        }

        @Override
        public int getItemCount() {
            return this.mStores.size();
        }

        @Override
        public StoreItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            DetailStoreItemBinding binding = DetailStoreItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new StoreItemHolder(binding.getRoot(), binding);
        }

        @Override
        public void onBindViewHolder(StoreItemHolder holder, int position) {
            Store store = this.mStores.get(position);
            holder.onBind(store);
        }

        @Override
        public void onViewRecycled(StoreItemHolder holder) {
            super.onViewRecycled(holder);
            holder.onRecycled();
        }

        private void setStores(List<Store> stores) {
            this.mStores = stores;
            this.notifyDataSetChanged();
        }
    }

    private class StoreItemHolder extends RecyclerView.ViewHolder {

        private final DetailStoreItemBinding mBinding;

        private StoreItemHolder(View view, DetailStoreItemBinding binding) {
            super(view);
            this.mBinding = binding;
            this.mBinding.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    StoreListFragment.this.onStoreItemClick(view, StoreItemHolder.this.mBinding.getStore());
                }
            });
        }

        private void onBind(Store store) {
            this.mBinding.setStore(store);
            this.mBinding.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    StoreListFragment.this.onStoreItemClick(view, StoreItemHolder.this.mBinding.getStore());
                }
            });
        }

        private void onRecycled() {
            this.mBinding.setStore(null);
            this.mBinding.setOnClickListener(null);
        }

    }


    public static StoreListFragment newInstance() {
        StoreListFragment fragment = new StoreListFragment();
        return fragment;
    }


    private StoreListFragmentViewModel mViewModel;

    private StoreListFragmentBinding mBinding;

    private StoreListAdapter mStoreListAdapter;

    private InteractionListener mInteractionListener;


    public StoreListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.mBinding = StoreListFragmentBinding.inflate(inflater, container, false);
        return this.mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.mViewModel = ViewModelProviders.of(this).get(StoreListFragmentViewModel.class);

        this.mBinding.listStores.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));

        this.mStoreListAdapter = new StoreListAdapter();
        this.mBinding.listStores.setAdapter(this.mStoreListAdapter);

        this.mViewModel.getAllStores().observe(this, new Observer<List<Store>>() {
            @Override
            public void onChanged(@Nullable List<Store> stores) {
                StoreListFragment.this.mStoreListAdapter.setStores(stores);
            }
        });
    }


    public void setInteractableListener(InteractionListener listener) {
        this.mInteractionListener = listener;
    }

    /*
    ************************************************************************************************
    * MainActivity.FloatingActionButtonInteractionListener
    ************************************************************************************************
     */

    @Override
    public void onConfigureFloatingActionButton(FloatingActionButton button) {

    }

    @Override
    public void onFloatingActionButtonClick(FloatingActionButton button) {
        if (this.mInteractionListener != null) {
            this.mInteractionListener.onRequestNewStore(this);
        }
    }

    /*
    ************************************************************************************************
    * UI views interaction
    ************************************************************************************************
     */

    public void onStoreItemClick(View view, Store store) {
        if (mInteractionListener != null) {
            mInteractionListener.onStoreSelected(this, store);
        }
    }

}
