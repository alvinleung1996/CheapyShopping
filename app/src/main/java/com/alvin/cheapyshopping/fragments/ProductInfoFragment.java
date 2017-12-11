package com.alvin.cheapyshopping.fragments;


import android.animation.Animator;
import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.alvin.cheapyshopping.fragments.dialogs.ChoosePictureSourceDialog;
import com.alvin.cheapyshopping.fragments.dialogs.ConfirmDialog;
import com.alvin.cheapyshopping.utils.ImageRotater;
import com.alvin.cheapyshopping.R;
import com.alvin.cheapyshopping.StoreActivity;
import com.alvin.cheapyshopping.databinding.ProductInfoFragmentBinding;
import com.alvin.cheapyshopping.db.entities.Product;
import com.alvin.cheapyshopping.db.entities.Store;
import com.alvin.cheapyshopping.utils.ImageExpander;
import com.alvin.cheapyshopping.viewmodels.ProductInfoFragmentViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Created by cheng on 11/21/2017.
 */

public class ProductInfoFragment extends Fragment {

    private static final String ARGUMENT_PRODUCT_ID = "com.alvin.cheapyshopping.fragments.ProductInfoFragment.ARGUMENT_PRODUCT_ID";

    private static final int REQUEST_IMAGE_FROM_CAMERA = 1;
    private static final int REQUEST_IMAGE_FROM_GALLERY = 2;

    private static final String IMAGE_FILE_TYPE = "Product";



    public static ProductInfoFragment newInstance(String productID) {
        ProductInfoFragment fragment = new ProductInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARGUMENT_PRODUCT_ID, productID);
        fragment.setArguments(args);
        return fragment;
    }


    private ProductInfoFragmentBinding mBinding;
    private ProductInfoFragmentViewModel mViewModel;

    private String mCurrentProductID;
    private Product mCurrentProduct;

    private Animator mCurrentAnimator;
    private ImageExpander mImageExpander;


    public ProductInfoFragment(){
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(false);

        // Get Product ID
        Bundle args = getArguments();
        mCurrentProductID = args.getString(ARGUMENT_PRODUCT_ID);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.mBinding = ProductInfoFragmentBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Setup viewModel
        this.mViewModel = ViewModelProviders.of(this).get(ProductInfoFragmentViewModel.class);

        // Get Product
        this.mViewModel.getProduct(mCurrentProductID).observe(this, new Observer<Product>() {
            @Override
            public void onChanged(@Nullable Product product) {
                ProductInfoFragment.this.mCurrentProduct = product;

                // Setup Product Basic Info
                mBinding.setProduct(mCurrentProduct);

                // Setup product image
                setProductImage(product.isImageExist());
            }
        });


        // Product Image Click to zoom
        this.mBinding.imageProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (mImageExpander == null){
//                    mImageExpander = new ImageExpander(getContext(), mCurrentAnimator, mBinding.container,
//                            mBinding.imageProduct, mBinding.imageProductZoomed,
//                            300);
//                }
//                if (mBitmap != null){
//                    mImageExpander.setBitmap(mBitmap);
//                }else {
//                    mImageExpander.setImgResId(R.drawable.ic_product_black_24dp);
//                }
//                mImageExpander.expandImage();
            }
        });


        // Edit product Image
        this.mBinding.imageEdit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                UpdateImageFragment updateImageFragment = UpdateImageFragment.newInstance(mCurrentProductID, "Product");
                updateImageFragment.setInteractionListener(new UpdateImageFragmentInteractionListener());
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(mBinding.container.getId(), updateImageFragment)
                        .addToBackStack(null)
                        .commit();

            }
        } );

    }


    /*
    ************************************************************************************************
    *  Update product image
    ************************************************************************************************
     */

    private class UpdateImageFragmentInteractionListener implements
            UpdateImageFragment.InteractionListener{
        @Override
        public void onGetImageUpdateResult(String result) {
            if (result.equals(UpdateImageFragment.IMAGE_UPDATED)){
                mViewModel.addCustomProductImage(mCurrentProduct);
            }else if(result.equals(UpdateImageFragment.IMAGE_DELETED)){
                mViewModel.removeCustomProductImage(mCurrentProduct);
            }
            // Remove the UpdateImageFragment
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }


    // Setup the product image. Check if not custom image, set as default
    private void setProductImage(boolean isCustom){
        // Get new image name and path
        String imageFileName = IMAGE_FILE_TYPE + "_" + mCurrentProductID;
        File storageDir = this.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = new File(storageDir, imageFileName + ".jpg");

        if (isCustom){
            if (imageFile.exists()){
                Bitmap bitmap = ImageRotater.getsInstance(this.getContext()).rotateImage(imageFile);
                // Update image view with rotated bitmap
                mBinding.imageProduct.setImageBitmap(bitmap);
            }
        } else {
            mBinding.imageProduct.setImageResource(R.drawable.ic_product_black_24dp);
        }
    }

    /*
    ************************************************************************************************
    * On Click
    ************************************************************************************************
     */

    private void onStoreClick(View view, Store store) {
        Intent intent = new Intent(this.getContext(), StoreActivity.class);
        intent.putExtra(StoreActivity.EXTRA_STORE_ID, store.getStoreId());
        this.startActivity(intent);
    }



}
