package com.alvin.cheapyshopping.fragments;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
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
import com.alvin.cheapyshopping.databinding.SimpleProductItemBinding;
import com.alvin.cheapyshopping.databinding.SelectProductFragmentBinding;
import com.alvin.cheapyshopping.db.entities.Product;
import com.alvin.cheapyshopping.utils.ImageRotater;
import com.alvin.cheapyshopping.viewmodels.SelectProductFragmentViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SelectProductFragment#newInstance} factory method to
 * createReference an instance of this fragment.
 */
public class SelectProductFragment extends Fragment {

    public interface InteractionListener {

        void onAddProductOptionSelected(SelectProductFragment fragment);

        void onProductItemsSelected(SelectProductFragment fragment, List<String> products);

    }


    private static final String ARGUMENT_CREATE_OPTIONS_MENU = "com.alvin.cheapyshopping.fragments.SelectProductFragment.ARGUMENT_CREATE_OPTIONS_MENU";
    private static final String ARGUMENT_EXCLUDE_SHOPPING_LIST_PRODUCTS = "com.alvin.cheapyshopping.fragments.SelectProductFragment.ARGUMENT_EXCLUDE_SHOPPING_LIST_PRODUCTS";



    public static SelectProductFragment newInstance(boolean createOptionsMenu, String excludeShoppingListId) {
        SelectProductFragment fragment = new SelectProductFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARGUMENT_CREATE_OPTIONS_MENU, createOptionsMenu);
        if (excludeShoppingListId != null) {
            args.putString(ARGUMENT_EXCLUDE_SHOPPING_LIST_PRODUCTS, excludeShoppingListId);
        }
        fragment.setArguments(args);
        return fragment;
    }



    public class ProductListAdapter extends RecyclerView.Adapter<ViewHolder> {

        private List<Product> mProducts;

        private ProductListAdapter() {
            super();
            this.mProducts = new ArrayList<>();
        }

        @Override
        public int getItemCount() {
            return this.mProducts.size();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final Product product = this.mProducts.get(position);
            holder.onBind(product);
        }

        @Override
        public void onViewRecycled(ViewHolder holder) {
            super.onViewRecycled(holder);
            holder.onRecycled();
        }

        private void setProducts(List<Product> products) {
            this.mProducts = products;
            this.notifyDataSetChanged();
        }

    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        private SimpleProductItemBinding mBinding;

        private ViewHolder(ViewGroup parent) {
            super(SimpleProductItemBinding.inflate(SelectProductFragment.this.getLayoutInflater(), parent, false).getRoot());
            this.mBinding = DataBindingUtil.getBinding(this.itemView);
        }

        private void onBind(Product product) {
            this.mBinding.setProduct(product);
            this.mBinding.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SelectProductFragment.this.onProductItemClick(view, ViewHolder.this.mBinding.getProduct());
                }
            });

            // Setup photo
            if (product.isImageExist()){
                String imageFileName = "Product" + "_" + product.getProductId();
                File storageDir = SelectProductFragment.this.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                File imageFile = new File(storageDir, imageFileName + ".jpg");
                if (imageFile.exists()) {
                    Bitmap bitmap = ImageRotater.getsInstance(SelectProductFragment.this.getContext()).rotateImage(imageFile);
                    // Update image view with rotated bitmap
                    mBinding.imageProductPhoto.setImageBitmap(bitmap);
                }
            } else {
                mBinding.imageProductPhoto.setImageResource(R.drawable.ic_product_black_24dp);
            }
        }

        private void onRecycled() {
            this.mBinding.setProduct(null);
            this.mBinding.setOnClickListener(null);
        }
    }


    public SelectProductFragment() {

    }


    private String exludeShoppingListId;

    private SelectProductFragmentViewModel mViewModel;
    private SelectProductFragmentBinding mBinding;
    private ProductListAdapter mProductListAdapter;

    private InteractionListener mInteractionListener;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = this.getArguments();
        if (args != null) {
            this.setHasOptionsMenu(args.getBoolean(ARGUMENT_CREATE_OPTIONS_MENU, true));
            if (args.containsKey(ARGUMENT_EXCLUDE_SHOPPING_LIST_PRODUCTS)) {
                this.exludeShoppingListId = args.getString(ARGUMENT_EXCLUDE_SHOPPING_LIST_PRODUCTS);
            } else {
                this.exludeShoppingListId = null;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.mBinding = SelectProductFragmentBinding.inflate(inflater, container, false);
        return this.mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.mViewModel = ViewModelProviders.of(this).get(SelectProductFragmentViewModel.class);

        this.mBinding.listProducts.setLayoutManager(new LinearLayoutManager(this.getContext()));
        this.mProductListAdapter = new ProductListAdapter();
        this.mBinding.listProducts.setAdapter(this.mProductListAdapter);

        LiveData<List<Product>> products = this.exludeShoppingListId != null ?
                this.mViewModel.findProductsNotInShoppingList(this.exludeShoppingListId)
                : this.mViewModel.getProducts();
        products.observe(this, new Observer<List<Product>>() {
            @Override
            public void onChanged(@Nullable List<Product> products) {
                SelectProductFragment.this.mProductListAdapter.setProducts(products);
            }
        });
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.select_product_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_add:
                this.onAddProductOptionItemSelected(item);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void setInteractionListener(InteractionListener interactionListener) {
        this.mInteractionListener = interactionListener;
    }


    private void onAddProductOptionItemSelected(MenuItem item) {
        if (this.mInteractionListener != null) {
            this.mInteractionListener.onAddProductOptionSelected(this);
        }
    }

    private void onProductItemClick(View view, Product product) {
        if (this.mInteractionListener != null) {
            this.mInteractionListener.onProductItemsSelected(this, Arrays.asList(product.getProductId()));
        }
    }

}
