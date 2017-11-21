package com.alvin.cheapyshopping.fragments;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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

import com.alvin.cheapyshopping.AddShoppingListActivity;
import com.alvin.cheapyshopping.AddShoppingListProductActivity;
import com.alvin.cheapyshopping.MainActivity;
import com.alvin.cheapyshopping.ProductActivity;
import com.alvin.cheapyshopping.R;
import com.alvin.cheapyshopping.databinding.ShoppingListFragmentBinding;
import com.alvin.cheapyshopping.databinding.ShoppingListProductItemBinding;
import com.alvin.cheapyshopping.databinding.ShoppingListStoreItemBinding;
import com.alvin.cheapyshopping.room.daos.ShoppingListProductDao.ShoppingListProductDetail;
import com.alvin.cheapyshopping.room.entities.Product;
import com.alvin.cheapyshopping.room.entities.ShoppingList;
import com.alvin.cheapyshopping.room.entities.Store;
import com.alvin.cheapyshopping.viewmodels.ShoppingListFragmentViewModel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class ShoppingListFragment extends Fragment implements MainActivity.FloatingActionButtonInteractionListener {

    private static final int REQUEST_ADD_SHOPPING_LIST = 1;


    private class ShoppingListItem {

        private static final int TYPE_STORE = 0;
        private static final int TYPE_PRODUCT = 1;

        private ShoppingListItem(Store store) {
            this.type = TYPE_STORE;
            this.store = store;
            this.product = null;
        }

        private ShoppingListItem(Product product) {
            this.type = TYPE_PRODUCT;
            this.store = null;
            this.product = product;
        }

        private int type;
        private Store store;
        private Product product;

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

        private void setShoppingListItems(List<ShoppingListItem> items) {
            this.mShoppingListItems = items;
            this.notifyDataSetChanged();
        }
    }

    private abstract class ShoppingListItemViewHolder extends RecyclerView.ViewHolder {

        private ShoppingListItemViewHolder(View v) {
            super(v);
        }

        protected abstract void onRecycled();
    }

    private class StoreItemViewHolder extends ShoppingListItemViewHolder {

        private ShoppingListStoreItemBinding mBinding;

        private StoreItemViewHolder(ViewGroup parent) {
            super(ShoppingListStoreItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false).getRoot());
            this.mBinding = DataBindingUtil.getBinding(this.itemView);
        }

        private void onBind(Store store) {
            this.mBinding.setStore(store);
            this.mBinding.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ShoppingListFragment.this.onStoreItemClick(view, StoreItemViewHolder.this.mBinding.getStore());
                }
            });
        }

        @Override
        protected void onRecycled() {
            this.mBinding.setStore(null);
            this.mBinding.setOnClickListener(null);
        }
    }

    private class ProductItemViewHolder extends ShoppingListItemViewHolder {

        private ShoppingListProductItemBinding mBinding;

        private ProductItemViewHolder(ViewGroup parent) {
            super(ShoppingListProductItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false).getRoot());
            this.mBinding = DataBindingUtil.getBinding(this.itemView);
        }

        private void onBind(Product product) {
            this.mBinding.setProduct(product);
            this.mBinding.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ShoppingListFragment.this.onProductItemClick(view, ProductItemViewHolder.this.mBinding.getProduct());
                }
            });
        }

        @Override
        protected void onRecycled() {
            this.mBinding.setProduct(null);
            this.mBinding.setOnClickListener(null);
        }
    }



    public static ShoppingListFragment newInstance() {
        ShoppingListFragment fragment = new ShoppingListFragment();
        return fragment;
    }


    public ShoppingListFragment() {

    }


    private ShoppingListFragmentViewModel mViewModel;
    private ShoppingListFragmentBinding mBinding;
    private ShoppingListAdapter mShoppingListItemListAdapter;

    private List<ShoppingList> mShoppingLists;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.mBinding = ShoppingListFragmentBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.mViewModel = ViewModelProviders.of(this).get(ShoppingListFragmentViewModel.class);

        this.mBinding.listShoppingListItems.setLayoutManager(new LinearLayoutManager(this.getContext()));

        this.mShoppingListItemListAdapter = new ShoppingListAdapter();
        this.mBinding.listShoppingListItems.setAdapter(this.mShoppingListItemListAdapter);

        this.mViewModel.getShoppingLists().observe(this, new Observer<List<ShoppingList>>() {
            @Override
            public void onChanged(@Nullable List<ShoppingList> shoppingLists) {
                ShoppingListFragment.this.mShoppingLists = shoppingLists;
                ShoppingListFragment.this.getActivity().invalidateOptionsMenu();
            }
        });

        this.mViewModel.getResult().observe(this, new Observer<Map<Store, List<ShoppingListProductDetail>>>() {
            @Override
            public void onChanged(@Nullable Map<Store, List<ShoppingListProductDetail>> storeListMap) {
                List<ShoppingListItem> items = ShoppingListFragment.this.resultToListItems(storeListMap);
                ShoppingListFragment.this.mShoppingListItemListAdapter.setShoppingListItems(items);
            }
        });
    }



    private static final int MENU_GROUP_ID = 1;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.shopping_list_fragment_menu, menu);
        if (this.mShoppingLists != null) {
            for (ShoppingList list : this.mShoppingLists) {
                MenuItem item = menu.add(MENU_GROUP_ID, (int)list.getShoppingListId(), Menu.NONE, list.getName());
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_add:
                this.onAddShoppingListButtonClick(item);
                return true;
        }
        if (this.mShoppingLists != null) {
            for (ShoppingList list: this.mShoppingLists) {
                if (list.getShoppingListId() == item.getItemId()) {

                    return true;
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }


    private void onAddShoppingListButtonClick(MenuItem item) {
        Intent intent = new Intent(this.getContext(), AddShoppingListActivity.class);
        this.startActivityForResult(intent, REQUEST_ADD_SHOPPING_LIST);
    }



    @Override
    public void onConfigureFloatingActionButton(FloatingActionButton button) {

    }

    @Override
    public void onFloatingActionButtonClick(FloatingActionButton button) {
        Intent intent = new Intent(this.getContext(), AddShoppingListProductActivity.class);
        this.startActivity(intent);
    }



    private void onStoreItemClick(View view, Store model) {

    }

    private void onProductItemClick(View view, Product model) {
        Intent intent = new Intent(this.getContext(), ProductActivity.class);
        intent.putExtra(ProductActivity.EXTRA_PRODUCT_ID, model.getProductId());
        this.startActivity(intent);
    }




    private List<ShoppingListItem> resultToListItems(Map<Store, List<ShoppingListProductDetail>> result) {
        List<ShoppingListItem> items = new ArrayList<>();
        for (Map.Entry<Store, List<ShoppingListProductDetail>> entry : result.entrySet()) {
            final Store store = entry.getKey();
            final List<ShoppingListProductDetail> products = entry.getValue();

            if (products.size() > 0) {
                items.add(new ShoppingListItem(store));

                for (Product product : products) {
                    items.add(new ShoppingListItem(product));
                }
            }
        }
        return items;
    }

}
