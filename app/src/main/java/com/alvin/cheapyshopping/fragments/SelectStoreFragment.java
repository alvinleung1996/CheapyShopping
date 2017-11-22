package com.alvin.cheapyshopping.fragments;


import android.content.Context;
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
import android.widget.TextView;

import com.alvin.cheapyshopping.R;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SelectStoreFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SelectStoreFragment extends Fragment {

//    public static interface SelectStoreFragmentListener {
//
//        public void onAddStoreOptionSelected(SelectStoreFragment fragment);
//
//        public void onStoreItemSelected(SelectStoreFragment fragment, StoreModel model);
//
//    }


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



//    public class StoreListAdapter extends RecyclerView.Adapter<StoreListAdapter.ViewHolder> {
//
//        public class ViewHolder extends RecyclerView.ViewHolder {
////            private StoreModel model;
//            private TextView storeNameTextView;
//            private TextView storeLocationTextView;
//            private TextView storeIdTextView;
//            private ViewHolder(View v) {
//                super(v);
//                this.storeNameTextView = v.findViewById(R.id.text_store_name);
//                this.storeLocationTextView = v.findViewById(R.id.text_store_location);
//                this.storeIdTextView = v.findViewById(R.id.text_store_id);
//                v.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        SelectStoreFragment.this.onStoreItemClick(view, ViewHolder.this.model);
//                    }
//                });
//            }
//        }
//
//        private StoreListAdapter(List<StoreModel> stores) {
//            super();
//            this.mStores = stores;
//        }
//
//        private List<StoreModel> mStores;
//
//        @Override
//        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_store, parent, false);
//            return new ViewHolder(v);
//        }
//
//        @Override
//        public void onBindViewHolder(ViewHolder holder, int position) {
//            StoreModel model = this.mStores.get(position);
//            holder.model = model;
//            holder.storeNameTextView.setText(model.name);
//            holder.storeLocationTextView.setText(model.location);
//            holder.storeIdTextView.setText("id:" + model.storeId);
//        }
//
//        @Override
//        public int getItemCount() {
//            return this.mStores.size();
//        }
//    }


    public SelectStoreFragment() {

    }


    private RecyclerView mStoreList;

//    private SelectStoreFragmentListener mSelectStoreFragmentListener;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof SelectStoreFragmentListener) {
//            this.mSelectStoreFragmentListener = (SelectStoreFragmentListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement SelectStoreFragmentListener");
//        }
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
        View v = inflater.inflate(R.layout.fragment_select_store, container, false);
        this.mStoreList = v.findViewById(R.id.list_stores);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.mStoreList.setLayoutManager(new LinearLayoutManager(this.getContext()));
//        this.mStoreList.setAdapter(new StoreListAdapter(StoreModel.manager.getAllProducts(this.getContext())));
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        this.mSelectStoreFragmentListener = null;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.select_store_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_add:
                this.onAddStoreOptionItemSelected(item);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void onAddStoreOptionItemSelected(MenuItem item) {
//        if (this.mSelectStoreFragmentListener != null) {
//            this.mSelectStoreFragmentListener.onAddStoreOptionSelected(this);
//        }
    }

//    private void onStoreItemClick(View view, StoreModel model) {
////        if (this.mSelectStoreFragmentListener != null) {
////            this.mSelectStoreFragmentListener.onStoreItemSelected(this, model);
////        }
//    }

}
