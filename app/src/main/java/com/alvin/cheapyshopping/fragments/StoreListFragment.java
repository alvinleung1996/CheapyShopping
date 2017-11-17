package com.alvin.cheapyshopping.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alvin.cheapyshopping.MainActivity;
import com.alvin.cheapyshopping.R;
import com.alvin.cheapyshopping.db.models.StoreModel;

import java.util.ArrayList;
import java.util.List;


public class StoreListFragment extends Fragment implements MainActivity.FloatingActionButtonInteractionListener {

    public interface InteractionListener {

        void onStoreSelected(StoreListFragment fragment, StoreModel store);

        void onRequestNewStore(StoreListFragment fragment);

    }

    public class StoreListAdapter extends RecyclerView.Adapter<StoreItemHolder> {

        private List<StoreModel> mStores;

        private StoreListAdapter() {
            this.mStores = new ArrayList<>();
        }

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
            StoreModel store = this.mStores.get(position);
            holder.onBind(store);
        }

        @Override
        public void onViewRecycled(StoreItemHolder holder) {
            super.onViewRecycled(holder);
            holder.onRecycled();
        }

        private void updateStores(List<StoreModel> stores) {
            this.mStores = stores;
            this.notifyDataSetChanged();
        }
    }

    private class StoreItemHolder extends RecyclerView.ViewHolder {

        private StoreModel mStore;

        private ImageView mStorePhotoImageView;
        private TextView mStoreNameTextView;
        private TextView mStoreLocationTextView;

        private StoreItemHolder(ViewGroup parent) {
            super(StoreListFragment.this.getLayoutInflater().inflate(R.layout.item_store_detail, parent, false));
            View view = this.itemView;
            this.mStorePhotoImageView = view.findViewById(R.id.image_store_photo);
            this.mStoreNameTextView = view.findViewById(R.id.text_store_name);
            this.mStoreLocationTextView = view.findViewById(R.id.text_store_location);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    StoreListFragment.this.onStoreItemClick(view, StoreItemHolder.this.mStore);
                }
            });
        }

        private void onBind(StoreModel store) {
            this.mStore = store;
            this.mStoreNameTextView.setText(store.name);
            this.mStoreLocationTextView.setText(store.location);
        }

        private void onRecycled() {
            this.mStore = null;
        }

    }


    public static StoreListFragment newInstance() {
        StoreListFragment fragment = new StoreListFragment();
        return fragment;
    }


    private RecyclerView mStoreList;
    private StoreListAdapter mStoreListAdapter;

    private InteractionListener mInteractionListener;

    public StoreListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_store_list, container, false);
        this.mStoreList = view.findViewById(R.id.list_stores);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.mStoreList.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));
        this.mStoreListAdapter = new StoreListAdapter();
        this.mStoreList.setAdapter(this.mStoreListAdapter);

        this.updateStoreList();
    }


    @Override
    public void onConfigureFloatingActionButton(FloatingActionButton button) {

    }

    @Override
    public void onFloatingActionButtonClick(FloatingActionButton button) {
        if (this.mInteractionListener != null) {
            this.mInteractionListener.onRequestNewStore(this);
        }
    }


    public void onStoreItemClick(View view, StoreModel store) {
        if (mInteractionListener != null) {
            mInteractionListener.onStoreSelected(this, store);
        }
    }


    public void setInteractableListener(InteractionListener listener) {
        this.mInteractionListener = listener;
    }



    public void updateStoreList() {
        List<StoreModel> stores = this.getAllStores();
        this.mStoreListAdapter.updateStores(stores);
    }

    private List<StoreModel> getAllStores() {
        return StoreModel.manager.getAll(this.getContext());
    }


}
