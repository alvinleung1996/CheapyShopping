package com.alvin.cheapyshopping.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alvin.cheapyshopping.MainActivity;
import com.alvin.cheapyshopping.ProductActivity;
import com.alvin.cheapyshopping.R;
import com.alvin.cheapyshopping.olddb.models.ProductModel;
import com.alvin.cheapyshopping.olddb.models.ShoppingListModel;
import com.alvin.cheapyshopping.olddb.models.StoreModel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class ShoppingListFragment extends Fragment implements MainActivity.FloatingActionButtonInteractionListener {

    public interface InteractionListener {

        void onRequestAddProduct(ShoppingListFragment fragment);

        void onStoreSelected(ShoppingListFragment fragment, StoreModel store);

        void onProductSelected(ShoppingListFragment fragment, ProductModel product);

    }


    private class ShoppingListItem {

        private static final int TYPE_STORE = 0;
        private static final int TYPE_PRODUCT = 1;

        private ShoppingListItem(StoreModel store) {
            this.type = TYPE_STORE;
            this.store = store;
            this.product = null;
        }

        private ShoppingListItem(ProductModel product) {
            this.type = TYPE_PRODUCT;
            this.store = null;
            this.product = product;
        }

        private int type;
        private StoreModel store;
        private ProductModel product;

    }

    private class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListItemViewHolder> {

        private List<ShoppingListItem> mShoppingListItems;

        private ShoppingListAdapter() {
            this.mShoppingListItems = new ArrayList<>();
        }

        @Override
        public int getItemViewType(int position) {
            return this.mShoppingListItems.get(position).type;
        }

        @Override
        public int getItemCount() {
            return this.mShoppingListItems.size();
        }

        @Override
        public ShoppingListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case ShoppingListItem.TYPE_STORE:
                    return new StoreItemViewHolder(parent);
                case ShoppingListItem.TYPE_PRODUCT:
                    return new ProductItemViewHolder(parent);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(ShoppingListItemViewHolder holder, int position) {
            ShoppingListItem item = this.mShoppingListItems.get(position);
            switch (item.type) {
                case ShoppingListItem.TYPE_STORE:
                    ((StoreItemViewHolder) holder).onBind(item.store);
                    break;
                case ShoppingListItem.TYPE_PRODUCT:
                    ((ProductItemViewHolder) holder).onBind(item.product);
                    break;
            }
        }

        @Override
        public void onViewRecycled(ShoppingListItemViewHolder holder) {
            super.onViewRecycled(holder);
            holder.onRecycled();

        }

        private void updateShoppingListItems(List<ShoppingListItem> items) {
            this.mShoppingListItems = items;
            this.notifyDataSetChanged();
            // TODO: Can we compute the modification of the data set?
        }
    }

    private abstract class ShoppingListItemViewHolder extends RecyclerView.ViewHolder {

        private ShoppingListItemViewHolder(View v) {
            super(v);
        }

        protected abstract void onRecycled();
    }

    private class StoreItemViewHolder extends ShoppingListItemViewHolder {

        private StoreModel mStore;
        private TextView mStoreNameTextView;
        private TextView mStoreLocationTextView;
        private TextView mStoreIdTextView;

        private StoreItemViewHolder(ViewGroup parent) {
            super(ShoppingListFragment.this.getLayoutInflater().inflate(R.layout.item_shopping_list_store, parent, false));
            View view = this.itemView;
            this.mStoreNameTextView = view.findViewById(R.id.text_store_name);
            this.mStoreLocationTextView = view.findViewById(R.id.text_store_location);
            this.mStoreIdTextView = view.findViewById(R.id.text_store_id);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ShoppingListFragment.this.onStoreItemClick(view, StoreItemViewHolder.this.mStore);
                }
            });
        }

        private void onBind(StoreModel store) {
            this.mStore = store;
            this.mStoreNameTextView.setText(store.name);
            this.mStoreLocationTextView.setText(store.location);
            this.mStoreIdTextView.setText("id:" + store.storeId);
        }

        @Override
        protected void onRecycled() {
            this.mStore = null;
        }
    }

    private class ProductItemViewHolder extends ShoppingListItemViewHolder {

        private ProductModel mProduct;
        private TextView mProductNameTextView;
        private TextView mProductBestPriceTextView;
        private TextView mProductIdTextView;

        private ProductItemViewHolder(ViewGroup parent) {
            super(ShoppingListFragment.this.getLayoutInflater().inflate(R.layout.item_shopping_list_product, parent, false));
            View view = this.itemView;
            this.mProductNameTextView = view.findViewById(R.id.text_product_name);
            this.mProductBestPriceTextView = view.findViewById(R.id.text_product_best_price);
            this.mProductIdTextView = view.findViewById(R.id.text_product_id);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ShoppingListFragment.this.onProductItemClick(view, ProductItemViewHolder.this.mProduct);
                }
            });
        }

        private void onBind(ProductModel product) {
            this.mProduct = product;
            this.mProductNameTextView.setText(product.name); // Set product name in Textview
            this.mProductBestPriceTextView.setText(10 + "Fake");
            this.mProductIdTextView.setText("id:" + product.productId); // Set productID in Textview
        }

        @Override
        protected void onRecycled() {
            this.mProduct = null;
        }
    }



    public static ShoppingListFragment newInstance() {
        ShoppingListFragment fragment = new ShoppingListFragment();
        return fragment;
    }


    public ShoppingListFragment() {

    }


    private RecyclerView mShoppingListItemList;
    private ShoppingListAdapter mShoppingListItemListAdapter;

    private ShoppingListModel mShoppingList;

    private InteractionListener mInteractionListener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shopping_list, container, false);
        this.mShoppingListItemList = view.findViewById(R.id.list_shopping_list_items);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.mShoppingListItemList.setLayoutManager(new LinearLayoutManager(this.getContext()));

        this.mShoppingList = ShoppingListModel.manager.getLatestShoppingList(this.getContext());

        this.mShoppingListItemListAdapter = new ShoppingListAdapter();
        this.mShoppingListItemList.setAdapter(this.mShoppingListItemListAdapter);

        this.updateShoppingListItemList();
    }

    public void setInteractionListener(InteractionListener listener) {
        this.mInteractionListener = listener;
    }


    @Override
    public void onConfigureFloatingActionButton(FloatingActionButton button) {

    }

    @Override
    public void onFloatingActionButtonClick(FloatingActionButton button) {
        this.mInteractionListener.onRequestAddProduct(this);
    }



    private void onStoreItemClick(View view, StoreModel model) {
        if (this.mInteractionListener != null) {
            this.mInteractionListener.onStoreSelected(this, model);
        }
    }

    private void onProductItemClick(View view, ProductModel model) {
        if (this.mInteractionListener != null) {
            this.mInteractionListener.onProductSelected(this, model);
        }
    }



    public void updateShoppingListItemList() {
        List<ShoppingListItem> items = this.getShoppingListItems();
        this.mShoppingListItemListAdapter.updateShoppingListItems(items);
    }

    private List<ShoppingListItem> getShoppingListItems() {
        LinkedHashMap<StoreModel, List<ProductModel>> result = this.getProductBestPriceResult();

        List<ShoppingListItem> items = new ArrayList<>();
        for (Map.Entry<StoreModel, List<ProductModel>> entry : result.entrySet()) {
            final StoreModel store = entry.getKey();
            final List<ProductModel> products = entry.getValue();

            if (products.size() > 0) {
                items.add(new ShoppingListItem(store));

                for (ProductModel product : products) {
                    items.add(new ShoppingListItem(product));
                }
            }
        }
        return items;
    }

    private LinkedHashMap<StoreModel, List<ProductModel>> getProductBestPriceResult() {
        LinkedHashMap<StoreModel, List<ProductModel>> result = new LinkedHashMap<>();

        // TODO: Implement best price algorithm (in ShoppingListModel)
        // Now it can only support 12 products: 3 stores and each store contain 4 products
        List<ProductModel> products = this.mShoppingList.getProducts();
        List<StoreModel> stores = StoreModel.manager.getAll(this.getContext());

        int storeIndex = 0;
        int productIndex = 0;
        for (; storeIndex < stores.size() && productIndex < products.size(); ++storeIndex) {
            final StoreModel store = stores.get(storeIndex);
            List<ProductModel> storeProducts = new ArrayList<>();

            for (; productIndex < (storeIndex+1)*4 && productIndex < products.size(); ++productIndex) {
                storeProducts.add(products.get(productIndex));
            }

            result.put(store, storeProducts);
        }

        return result;
    }

}
