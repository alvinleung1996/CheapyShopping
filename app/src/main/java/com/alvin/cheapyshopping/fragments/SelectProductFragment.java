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
import com.alvin.cheapyshopping.olddb.models.ProductModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SelectProductFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SelectProductFragment extends Fragment {

    public static interface SelectProductFragmentListener {

        public void onAddProductOptionSelected(SelectProductFragment fragment);

        public void onProductItemsSelected(SelectProductFragment fragment, List<ProductModel> models);

    }


    private static final String ARGUMENT_CREATE_OPTIONS_MENU = "com.alvin.cheapyshopping.fragments.SelectProductFragment.ARGUMENT_CREATE_OPTIONS_MENU";
    private static final String ARGUMENT_EXCLUDE_PRODUCT_IDS = "com.alvin.cheapyshopping.fragments.SelectProductFragment.ARGUMENT_EXCLUDE_PRODUCT_IDS";



    public static SelectProductFragment newInstance() {
        return newInstance(true, new long[0]);
    }

    public static SelectProductFragment newInstance(boolean createOptionsMenu, long[] excludeProductIds) {
        SelectProductFragment fragment = new SelectProductFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARGUMENT_CREATE_OPTIONS_MENU, createOptionsMenu);
        args.putLongArray(ARGUMENT_EXCLUDE_PRODUCT_IDS, excludeProductIds);
        fragment.setArguments(args);
        return fragment;
    }



    public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ViewHolder> {

        public class ViewHolder extends RecyclerView.ViewHolder {
            private ProductModel model;
            private TextView productNameTextView;
            private TextView productIdTextView;
            public ViewHolder(View v) {
                super(v);
                this.productNameTextView = v.findViewById(R.id.text_product_name);
                this.productIdTextView = v.findViewById(R.id.text_product_id);
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SelectProductFragment.this.onProductItemClick(view, ViewHolder.this.model);
                    }
                });
            }
        }

        private ProductListAdapter(List<ProductModel> products) {
            super();
            this.mProducts = products;
        }

        private List<ProductModel> mProducts;

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_product, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final ProductModel model = this.mProducts.get(position);
            holder.model = model;
            holder.productNameTextView.setText(model.name);
            holder.productIdTextView.setText(Long.toString(model.productId));
        }

        @Override
        public int getItemCount() {
            return this.mProducts.size();
        }

        private void updateProducts(List<ProductModel> products) {
            this.mProducts = products;
        }
    }


    public SelectProductFragment() {
    }


    private Set<Long> mExcludeProductIds;

    private RecyclerView mProductListView;
    private ProductListAdapter mProductListAdapter;

    private SelectProductFragmentListener mSelectProductFragmentListener;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SelectProductFragmentListener) {
            this.mSelectProductFragmentListener = (SelectProductFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement SelectProductFragmentListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = this.getArguments();
        if (args != null) {
            this.setHasOptionsMenu(args.getBoolean(ARGUMENT_CREATE_OPTIONS_MENU, true));
            this.mExcludeProductIds = new TreeSet<>();
            long[] excludedIds = args.getLongArray(ARGUMENT_EXCLUDE_PRODUCT_IDS);
            if (excludedIds != null) {
                for (long id : excludedIds) {
                    this.mExcludeProductIds.add(id);
                }
            }
        } else {
            this.setHasOptionsMenu(true);
            this.mExcludeProductIds = new TreeSet<>();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_select_product, container, false);
        this.mProductListView = v.findViewById(R.id.list_products);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.mProductListView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        this.mProductListAdapter = new ProductListAdapter(new ArrayList<ProductModel>());
        this.mProductListView.setAdapter(this.mProductListAdapter);

        this.updateProductModelList();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.mSelectProductFragmentListener = null;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.select_product_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_add:
                this.onAddProductOptionItemSelected(item);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void updateProductModelList() {
        this.mProductListAdapter.updateProducts(this.getIncludedProductModels());
        this.mProductListAdapter.notifyDataSetChanged();
    }

    private List<ProductModel> getAllProductModels() {
        return ProductModel.manager.getAll(this.getContext());
    }

    private List<ProductModel> getIncludedProductModels() {
        List<ProductModel> includedProducts = new ArrayList<>();
        for (ProductModel product : this.getAllProductModels()) {
            if (!this.mExcludeProductIds.contains(product.productId)) {
                includedProducts.add(product);
            }
        }
        return includedProducts;
    }


    private void onAddProductOptionItemSelected(MenuItem item) {
        if (this.mSelectProductFragmentListener != null) {
            this.mSelectProductFragmentListener.onAddProductOptionSelected(this);
        }
    }

    private void onProductItemClick(View view, ProductModel model) {
        if (this.mSelectProductFragmentListener != null) {
            this.mSelectProductFragmentListener.onProductItemsSelected(this, Arrays.asList(model));
        }
    }

}
