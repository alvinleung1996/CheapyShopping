package com.alvin.cheapyshopping.fragments;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.alvin.cheapyshopping.R;
import com.alvin.cheapyshopping.databinding.SelectStoreFragmentBinding;
import com.alvin.cheapyshopping.databinding.SimpleStoreItemBinding;
import com.alvin.cheapyshopping.db.entities.Store;
import com.alvin.cheapyshopping.viewmodels.SelectStoreFragmentViewModel;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SelectStoreFragment#newInstance} factory method to
 * createReference an instance of this fragment.
 */
public class SelectStoreFragment extends Fragment {

    public interface InteractionListener {

        void onAddStoreOptionSelected(SelectStoreFragment fragment);

        void onStoreItemSelected(SelectStoreFragment fragment, Store store);

    }


    private static final String ARGUMENT_CREATE_OPTIONS_MENU = "com.alvin.cheapyshopping.fragments.SelectStoreFragment.ARGUMENT_CREATE_OPTIONS_MENU";



    public static SelectStoreFragment newInstance() {
        return newInstance(true);
    }

    public static SelectStoreFragment newInstance(boolean createOptionsMenu) {
        SelectStoreFragment fragment = new SelectStoreFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARGUMENT_CREATE_OPTIONS_MENU, createOptionsMenu);
        fragment.setArguments(args);
        return fragment;
    }



    public class StoreListAdapter extends RecyclerView.Adapter<StoreItemHolder> {

        private StoreListAdapter() {
            super();
            this.mStores = new ArrayList<>();
        }

        private List<Store> mStores;

        @Override
        public int getItemCount() {
            return this.mStores.size();
        }

        @Override
        public StoreItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new StoreItemHolder(parent);
        }

        @Override
        public void onBindViewHolder(StoreItemHolder holder, int position) {
            Store model = this.mStores.get(position);
            holder.onBind(model);
        }

        private void setStores(List<Store> stores) {
            this.mStores = stores != null ? stores : new ArrayList<Store>();
            this.notifyDataSetChanged();
        }
    }

    private class StoreItemHolder extends RecyclerView.ViewHolder {

        private final SimpleStoreItemBinding mBinding;

        private StoreItemHolder(ViewGroup parent) {
            super(SimpleStoreItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false).getRoot());
            this.mBinding = DataBindingUtil.getBinding(this.itemView);
        }

        private void onBind(Store store) {
            this.mBinding.setStore(store);
            this.mBinding.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SelectStoreFragment.this.onStoreItemClick(view, StoreItemHolder.this.mBinding.getStore());
                }
            });
        }

        private void onRecycled() {
            this.mBinding.setStore(null);
            this.mBinding.setOnClickListener(null);
        }
    }


    private SelectStoreFragmentViewModel mViewModel;
    private SelectStoreFragmentBinding mBinding;
    private StoreListAdapter mStoreListAdapter;

    private InteractionListener mInteractionListener;


    public SelectStoreFragment() {

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = this.getArguments();
        if (args != null) {
            this.setHasOptionsMenu(args.getBoolean(ARGUMENT_CREATE_OPTIONS_MENU, true));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.mBinding = SelectStoreFragmentBinding.inflate(inflater, container, false);
        return this.mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.mViewModel = ViewModelProviders.of(this).get(SelectStoreFragmentViewModel.class);

        this.mBinding.listStores.setLayoutManager(new LinearLayoutManager(this.getContext()));
        this.mStoreListAdapter = new StoreListAdapter();
        this.mBinding.listStores.setAdapter(this.mStoreListAdapter);

        this.mViewModel.getAllStores().observe(this, new Observer<List<Store>>() {
            @Override
            public void onChanged(@Nullable List<Store> stores) {
                SelectStoreFragment.this.mStoreListAdapter.setStores(stores);
            }
        });
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.select_store_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_add:
                this.onAddStoreOptionItemSelected(item);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    public void setInteractionListener(InteractionListener listener) {
        this.mInteractionListener = listener;
    }



    private void onAddStoreOptionItemSelected(MenuItem item) {
        if (this.mInteractionListener != null) {
            this.mInteractionListener.onAddStoreOptionSelected(this);
        }
    }

    private void onStoreItemClick(View view, Store model) {
        if (this.mInteractionListener != null) {
            this.mInteractionListener.onStoreItemSelected(this, model);
        }
    }

}
