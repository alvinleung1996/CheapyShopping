package com.alvin.cheapyshopping.fragments;


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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alvin.cheapyshopping.R;
import com.alvin.cheapyshopping.databinding.StoreProductPriceItemBinding;
import com.alvin.cheapyshopping.databinding.StoreProductPricesFragmentBinding;
import com.alvin.cheapyshopping.db.entities.Product;
import com.alvin.cheapyshopping.db.entities.pseudo.ProductPrice;
import com.alvin.cheapyshopping.db.entities.pseudo.StorePrice;
import com.alvin.cheapyshopping.utils.ImageRotater;
import com.alvin.cheapyshopping.viewmodels.StoreProductPricesFragmentViewModel;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class StoreProductPricesFragment extends Fragment {

    private static final String ARGUMENT_STORE_ID = "com.alvin.cheapyshopping.fragments.StoreProductPricesFragment.ARGUMENT_STORE_ID";


    public static StoreProductPricesFragment newInstance(String storeId) {
        Bundle args = new Bundle();
        args.putString(ARGUMENT_STORE_ID, storeId);
        StoreProductPricesFragment fragment = new StoreProductPricesFragment();
        fragment.setArguments(args);
        return fragment;
    }


    private String storeId;

    private StoreProductPricesFragmentViewModel mViewModel;

    private StoreProductPricesFragmentBinding mBinding;

    private ProductPriceListAdapter mProductPriceListAdapter;


    public StoreProductPricesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = this.getArguments();
        String productId = null;
        if (args != null) {
            if (args.containsKey(ARGUMENT_STORE_ID)) {
                productId = args.getString(ARGUMENT_STORE_ID);
            }
        }
        if (productId == null) {
            throw new RuntimeException("Store Id required!");
        }
        this.storeId = productId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.mBinding = StoreProductPricesFragmentBinding.inflate(inflater, container, false);
        return this.mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.mViewModel = ViewModelProviders.of(this).get(StoreProductPricesFragmentViewModel.class);

        this.mProductPriceListAdapter = new ProductPriceListAdapter();
        this.mBinding.listProductPrices.setAdapter(this.mProductPriceListAdapter);
        this.mBinding.listProductPrices.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));

        this.mViewModel.findStoreProductPrices(this.storeId).observe(this, new Observer<List<ProductPrice>>() {
            @Override
            public void onChanged(@Nullable List<ProductPrice> productPrices) {
                StoreProductPricesFragment.this.mProductPriceListAdapter
                        .setStorePrices(productPrices == null ? new ArrayList<ProductPrice>() : productPrices);
            }
        });
    }



    /*
    ************************************************************************************************
    * List
    ************************************************************************************************
     */

    private class ProductPriceListAdapter extends RecyclerView.Adapter<ProductPriceItemHolder> {

        private List<ProductPrice> mProductPrices;

        private ProductPriceListAdapter() {
            this.mProductPrices = new ArrayList<>();
        }

        @Override
        public int getItemCount() {
            return this.mProductPrices.size();
        }

        @Override
        public ProductPriceItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ProductPriceItemHolder(parent);
        }

        @Override
        public void onBindViewHolder(ProductPriceItemHolder holder, int position) {
            holder.onBind(this.mProductPrices.get(position));
        }

        @Override
        public void onViewRecycled(ProductPriceItemHolder holder) {
            holder.onRecycled();
        }

        private void setStorePrices(List<ProductPrice> productPrices) {
            this.mProductPrices = productPrices;
            this.notifyDataSetChanged();
        }

    }

    private class ProductPriceItemHolder extends RecyclerView.ViewHolder {

        private final StoreProductPriceItemBinding mBinding;

        private ProductPriceItemHolder(ViewGroup parent) {
            super(StoreProductPriceItemBinding
                    .inflate(LayoutInflater.from(parent.getContext()), parent, false)
                    .getRoot());
            this.mBinding = DataBindingUtil.getBinding(this.itemView);
            this.mBinding.setCreationTimeFormatter(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss"));
        }

        private void onBind(ProductPrice productPrice) {
            this.mBinding.setProductPrice(productPrice);
            this.mBinding.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    StoreProductPricesFragment.this.onProductPriceItemClick(view,
                            ProductPriceItemHolder.this.mBinding.getProductPrice());
                }
            });


            // Setup photo
            String imageFileName = "Product" + "_" + productPrice.getProduct().getProductId();
            File storageDir = StoreProductPricesFragment.this.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File imageFile = new File(storageDir, imageFileName + ".jpg");

            if (productPrice.getProduct().isImageExist()){
                if (imageFile.exists()) {
                    Bitmap bitmap = ImageRotater.getsInstance(StoreProductPricesFragment.this.getContext()).rotateImage(imageFile);
                    // Update image view with rotated bitmap
                    mBinding.imageProductPhoto.setImageBitmap(bitmap);
                }
            } else {
                mBinding.imageProductPhoto.setImageResource(R.drawable.ic_product_black_24dp);
            }
        }

        private void onRecycled() {
            this.mBinding.setProductPrice(null);
            this.mBinding.setOnClickListener(null);
        }
    }



    /*
    ************************************************************************************************
    * Item
    ************************************************************************************************
     */

    private void onProductPriceItemClick(View view, ProductPrice productPrice) {
        Log.d("item", "price clicked");
    }
}
