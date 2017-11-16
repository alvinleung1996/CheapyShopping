package com.alvin.cheapyshopping.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alvin.cheapyshopping.R;
import com.alvin.cheapyshopping.db.models.ProductModel;
import com.alvin.cheapyshopping.db.models.ShoppingListModel;
import com.alvin.cheapyshopping.db.models.StoreModel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class ShoppingListFragment extends Fragment {

    public static interface ShoppingListFragmentListener {

        public void onShoppingListStoreItemSelected(ShoppingListFragment fragment, StoreModel model);

        public void onShoppingListProductItemSelected(ShoppingListFragment fragment, ProductModel model);

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

    private class ShoppingListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private class StoreViewHolder extends RecyclerView.ViewHolder {
            private StoreModel model;
            private TextView storeNameTextView;
            private TextView storeLocationTextView;
            private TextView storeIdTextView;
            private StoreViewHolder(View v) {
                super(v);
                this.storeNameTextView = v.findViewById(R.id.text_store_name);
                this.storeLocationTextView = v.findViewById(R.id.text_store_location);
                this.storeIdTextView = v.findViewById(R.id.text_store_id);
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ShoppingListFragment.this.onStoreItemClick(view, StoreViewHolder.this.model);
                    }
                });
            }
        }

        private class ProductViewHolder extends RecyclerView.ViewHolder {
            private ProductModel model;
            private TextView productNameTextView;
            private TextView productBestPriceTextView;
            private TextView productIDTextView;
            private ProductViewHolder(View v) {
                super(v);
                this.productNameTextView = v.findViewById(R.id.text_product_name);
                this.productBestPriceTextView = v.findViewById(R.id.text_product_best_price);
                this.productIDTextView = v.findViewById(R.id.text_product_id);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ShoppingListFragment.this.onProductItemClick(view, ProductViewHolder.this.model);
                    }
                });
            }
        }

        private ShoppingListAdapter(List<ShoppingListItem> shoppingListItems) {
            this.shoppingListItems = shoppingListItems;
        }

        private List<ShoppingListItem> shoppingListItems;

        @Override
        public int getItemViewType(int position) {
            return this.shoppingListItems.get(position).type;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v;
            switch (viewType) {
                case ShoppingListItem.TYPE_STORE:
                    v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_shopping_list_store, parent, false);
                    return new StoreViewHolder(v);
                case ShoppingListItem.TYPE_PRODUCT:
                    v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_shopping_list_product, parent, false);
                    return new ProductViewHolder(v);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof StoreViewHolder) {
                StoreViewHolder h = (StoreViewHolder) holder;
                StoreModel store = this.shoppingListItems.get(position).store;
                h.model = store;
                h.storeNameTextView.setText(store.name);
                h.storeLocationTextView.setText(store.location);
                h.storeIdTextView.setText("id:" + store.storeId);

            } else if (holder instanceof ProductViewHolder) {
                ProductViewHolder h = (ProductViewHolder) holder;
                ProductModel product = this.shoppingListItems.get(position).product;
                h.model = product;
                h.productNameTextView.setText(product.name); // Set product name in Textview
                h.productBestPriceTextView.setText(10 + "Fake");
                h.productIDTextView.setText("id:" + product.productId); // Set productID in Textview
            }
        }

        @Override
        public int getItemCount() {
            return this.shoppingListItems.size();
        }

        private void updateShoppingListItems(List<ShoppingListItem> items) {
            this.shoppingListItems = items;
            this.notifyDataSetChanged();
            // TODO: Can we compute the modification of the data set?
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

    private ShoppingListFragmentListener mShoppingListFragmentListener;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ShoppingListFragmentListener) {
            this.mShoppingListFragmentListener = (ShoppingListFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ShoppingListFragmentListener");
        }
    }

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

        this.mShoppingListItemListAdapter = new ShoppingListAdapter(new ArrayList<ShoppingListItem>());
        this.mShoppingListItemList.setAdapter(this.mShoppingListItemListAdapter);

        this.updateShoppingListItemList();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.mShoppingListFragmentListener = null;
    }



    private void onStoreItemClick(View view, StoreModel model) {
        if (this.mShoppingListFragmentListener != null) {
            this.mShoppingListFragmentListener.onShoppingListStoreItemSelected(this, model);
        }
    }

    private void onProductItemClick(View view, ProductModel model) {
        if (this.mShoppingListFragmentListener != null) {
            this.mShoppingListFragmentListener.onShoppingListProductItemSelected(this, model);
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
