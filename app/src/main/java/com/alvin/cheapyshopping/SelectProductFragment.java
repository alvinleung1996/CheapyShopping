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

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SelectProductFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SelectProductFragment extends Fragment {

    private static final String EXTRA_STORE_ID = "com.alvin.cheapyshopping.SelectProductFragment.EXTRA_STORE_ID";

    public static final String FRAGMENT_TRANSACTION_NEW_PRODUCT = "FRAGMENT_TRANSACTION_NEW_STORE";



    public static SelectProductFragment newInstance(long storeId) {
        SelectProductFragment fragment = new SelectProductFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(EXTRA_STORE_ID, storeId);
        fragment.setArguments(bundle);
        return fragment;
    }



    public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ViewHolder> {

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView mProductNameTextView;
            public TextView mProductIdTextView;
            public ViewHolder(View v) {
                super(v);
                this.mProductNameTextView = v.findViewById(R.id.text_product_name);
                this.mProductIdTextView = v.findViewById(R.id.text_product_id);
            }
        }

        public ProductListAdapter(List<ProductModel> products) {
            super();
            this.mProducts = products;
        }

        public List<ProductModel> mProducts;

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final ProductModel model = this.mProducts.get(position);
            holder.mProductNameTextView.setText(model.name);
            holder.mProductIdTextView.setText(Long.toString(model.productId));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SelectProductFragment.this.onProductItemClick(view, model);
                }
            });
        }

        @Override
        public int getItemCount() {
            return this.mProducts.size();
        }
    }


    public SelectProductFragment() {
    }


    private RecyclerView mProductList;

    private long mStoreId;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = savedInstanceState != null ? savedInstanceState : this.getArguments();
        if (args != null) {
            this.mStoreId = args.getLong(EXTRA_STORE_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_select_product, container, false);
        this.mProductList = v.findViewById(R.id.list_products);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.mProductList.setLayoutManager(new LinearLayoutManager(this.getContext()));
        this.mProductList.setAdapter(new ProductListAdapter(ProductModel.manager.getAll(this.getContext())));

        Button newProductButton = this.getView().findViewById(R.id.button_new_product);
        newProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectProductFragment.this.onNewProductButtonClick(view);
            }
        });
    }



    private void onNewProductButtonClick(View view) {
        this.getFragmentManager().beginTransaction()
                .addToBackStack(FRAGMENT_TRANSACTION_NEW_PRODUCT)
                .replace(R.id.fragment_container, AddProductFragment.newInstance(this.mStoreId))
                .commit();
    }

    private void onProductItemClick(View view, ProductModel model) {
        this.getFragmentManager().beginTransaction()
                .addToBackStack(null)
                .replace(R.id.fragment_container, AddPriceDataFragment.newInstance(this.mStoreId, model.productId))
                .commit();
    }

}
