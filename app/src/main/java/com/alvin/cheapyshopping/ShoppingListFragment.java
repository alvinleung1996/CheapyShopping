package com.alvin.cheapyshopping;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alvin.cheapyshopping.db.models.PriceModel;
import com.alvin.cheapyshopping.db.models.ProductModel;
import com.alvin.cheapyshopping.db.models.ShoppingListModel;
import com.alvin.cheapyshopping.db.models.StoreModel;

import java.util.ArrayList;
import java.util.List;


public class ShoppingListFragment extends Fragment {

    private class ListItem {

        private static final int TYPE_STORE = 0;
        private static final int TYPE_PRODUCT = 1;

        private ListItem(StoreModel store) {
            this.type = TYPE_STORE;
            this.store = store;
            this.product = null;
        }

        private ListItem(ProductModel product) {
            this.type = TYPE_PRODUCT;
            this.store = null;
            this.product = product;
        }

        private int type;
        private StoreModel store;
        private ProductModel product;

    }

    public static class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.AbstractViewHolder> {

        public static abstract class AbstractViewHolder extends RecyclerView.ViewHolder {
            private AbstractViewHolder(View v) { super(v); }
        }

        public static class StoreViewHolder extends AbstractViewHolder {
            private StoreViewHolder(View v) {
                super(v);
            }
        }

        public static class ProductViewHolder extends AbstractViewHolder {
            private TextView mProductNameTextView;
            private TextView mProductIDTextView;
            private TextView mProductBestPriceTextView;
            private long mProductID; // For passing into the Product Activity
            private ProductViewHolder(View v) {
                super(v);
                this.mProductNameTextView = v.findViewById(R.id.text_product_name);
                this.mProductIDTextView = v.findViewById(R.id.text_product_id);
                this.mProductBestPriceTextView = v.findViewById(R.id.text_product_best_price);
                // Go to product activity
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Context context = view.getContext();
                        Intent intent = new Intent(view.getContext(), ProductActivity.class);
                        intent.putExtra("mProductID", mProductID);
                        context.startActivity(intent);
                    }
                });
            }
        }

        private ShoppingListAdapter(List<ListItem> listItems) {
            this.listItems = listItems;
        }

        private List<ListItem> listItems;

        @Override
        public int getItemViewType(int position) {
            return this.listItems.get(position).type;
        }

        @Override
        public AbstractViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v;
            switch (viewType) {
                case ListItem.TYPE_STORE:
                    v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
                    return new StoreViewHolder(v);
                case ListItem.TYPE_PRODUCT:
                    v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
                    return new ProductViewHolder(v);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(AbstractViewHolder holder, int position) {
            if (holder instanceof StoreViewHolder) {
                StoreViewHolder h = (StoreViewHolder) holder;

            } else if (holder instanceof ProductViewHolder) {
                ProductViewHolder h = (ProductViewHolder) holder;
                h.mProductNameTextView.setText(this.listItems.get(position).product.name); // Set product name in Textview
                h.mProductIDTextView.setText(String.valueOf(this.listItems.get(position).product.productId)); // Set productID in Textview
                h.mProductID = this.listItems.get(position).product.productId;
            }
        }

        @Override
        public int getItemCount() {
            return this.listItems.size();
        }
    }


    public static ShoppingListFragment newInstance() {
        ShoppingListFragment fragment = new ShoppingListFragment();
        return fragment;
    }


    public ShoppingListFragment() {

    }


    private RecyclerView mProductList;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shopping_list, container, false);
        this.mProductList = view.findViewById(R.id.list_products);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.mProductList.setLayoutManager(new LinearLayoutManager(this.getContext()));


        ShoppingListModel shoppingList = ShoppingListModel.manager.getLatestShoppingList(this.getContext());
        List<ProductModel> products = shoppingList.getProducts();
        List<ListItem> items = new ArrayList<>();
        for (ProductModel model : products) {
            items.add(new ListItem(model));
        }

        mProductList.setAdapter(new ShoppingListAdapter(items));
    }
}
