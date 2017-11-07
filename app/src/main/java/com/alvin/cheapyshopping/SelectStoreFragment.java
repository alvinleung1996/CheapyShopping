package com.alvin.cheapyshopping;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.alvin.cheapyshopping.db.models.ProductModel;
import com.alvin.cheapyshopping.db.models.StoreModel;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SelectStoreFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SelectStoreFragment extends Fragment {

    public static final String FRAGMENT_TRANSACTION_NEW_STORE = "FRAGMENT_TRANSACTION_NEW_STORE";



    public static SelectStoreFragment newInstance() {
        SelectStoreFragment fragment = new SelectStoreFragment();
        return fragment;
    }



    public class StoreListAdapter extends RecyclerView.Adapter<StoreListAdapter.ViewHolder> {

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView mStoreNameTextView;
            public TextView mStoreLocationTextView;
            public ViewHolder(View v) {
                super(v);
                this.mStoreNameTextView = v.findViewById(R.id.text_store_name);
                this.mStoreLocationTextView = v.findViewById(R.id.text_store_location);
            }
        }

        public StoreListAdapter(List<StoreModel> stores) {
            super();
            this.mStores = stores;
        }

        public List<StoreModel> mStores;

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_store, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final StoreModel model = this.mStores.get(position);
            holder.mStoreNameTextView.setText(model.name);
            holder.mStoreLocationTextView.setText(Long.toString(model.storeId) + " " + model.location);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SelectStoreFragment.this.onStoreItemClick(view, model);
                }
            });
        }

        @Override
        public int getItemCount() {
            return this.mStores.size();
        }
    }


    public SelectStoreFragment() {
    }


    private RecyclerView mStoreList;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        this.mStoreList.setAdapter(new StoreListAdapter(StoreModel.manager.getAll(this.getContext())));

        Button newProductButton = this.getView().findViewById(R.id.button_new_store);
        newProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectStoreFragment.this.onNewStoreButtonClick(view);
            }
        });
    }



    private void onNewStoreButtonClick(View view) {
        this.getFragmentManager().beginTransaction()
                .addToBackStack(FRAGMENT_TRANSACTION_NEW_STORE)
                .replace(R.id.fragment_container, AddStoreFragment.newInstance())
                .commit();
    }

    private void onStoreItemClick(View view, StoreModel model) {
        this.getFragmentManager().beginTransaction()
                .addToBackStack(null)
                .replace(R.id.fragment_container, SelectProductFragment.newInstance(model.storeId))
                .commit();
    }

}
