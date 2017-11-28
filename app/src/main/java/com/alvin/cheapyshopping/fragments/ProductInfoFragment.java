package com.alvin.cheapyshopping.fragments;


import android.animation.Animator;
import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.alvin.cheapyshopping.utils.ImageRotate;
import com.alvin.cheapyshopping.utils.ImageUpdater;
import com.alvin.cheapyshopping.R;
import com.alvin.cheapyshopping.StoreActivity;
import com.alvin.cheapyshopping.databinding.ProductInfoFragmentBinding;
import com.alvin.cheapyshopping.db.entities.Product;
import com.alvin.cheapyshopping.db.entities.ShoppingList;
import com.alvin.cheapyshopping.db.entities.Store;
import com.alvin.cheapyshopping.utils.ImageExpander;
import com.alvin.cheapyshopping.viewmodels.ProductInfoFragmentViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cheng on 11/21/2017.
 */

public class ProductInfoFragment extends Fragment {

    private static final String ARGUMENT_PRODUCT_ID = "com.alvin.cheapyshopping.fragments.ProductInfoFragment.ARGUMENT_PRODUCT_ID";

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private static final String IMAGE_FILE_TYPE = "Product";
    private static final String IMAGE_FOLDER = "Product";



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
    ImageExpander mImageExpander;
    Bitmap mBitmap;


    public ProductInfoFragment(){
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);

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
                setProductImage();
            }
        });

        // Setup product image
        setProductImage();


        // Product Image Click to zoom
        this.mBinding.imageProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mImageExpander == null){
                    mImageExpander = new ImageExpander(getContext(), mCurrentAnimator, mBinding.container,
                            mBinding.imageProduct, mBinding.imageProductZoomed,
                            300);
                }
                if (mBitmap != null){
                    mImageExpander.setBitmap(mBitmap);
                }else {
                    mImageExpander.setImgResId(R.drawable.ic_product_black_24dp);
                }
                mImageExpander.expandImage();
            }
        });


        // Edit product Image
        this.mBinding.imageEdit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                updateImage();
                setProductImage();
            }
        } );

    }




    /*
    ************************************************************************************************
    * Menu
    ************************************************************************************************
     */

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.product_fragment_menu, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_save:
                selectShoppingListDialog();
                return true;
            case R.id.item_edit:
                // TODO: Edit product information
                return true;
            case R.id.item_add_price:
                // TODO: Add new price
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /*
    ************************************************************************************************
    * Dialog for selecting shopping list
    ************************************************************************************************
     */

    public void selectShoppingListDialog() {
        final List<ShoppingList> mDialogShoppingLists = new ArrayList<>();
        final List<String> shoppinglistNames = new ArrayList<>();
        final List<Integer> mSelecteditems = new ArrayList<>();

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProductInfoFragment.this.getContext());

        // Set dialog title
        alertDialogBuilder.setTitle("Save Product");


        // Set dialog main message
        alertDialogBuilder.setTitle("Please select the shopping list");

        // Get shopping lists and select
        mViewModel.findCurrentAccountShoppingLists().observe(this, new Observer<List<ShoppingList>>() {
            @Override
            public void onChanged(@Nullable List<ShoppingList> shoppingLists) {
                if (shoppingLists != null){

                    // Add shopping list names
                    for (int i = 0; i < shoppingLists.size(); i++){
                        shoppinglistNames.add(shoppingLists.get(i).getName());
                        mDialogShoppingLists.add(shoppingLists.get(i));
                    }

                    // Convert List<String> to Array
                    String[] items = shoppinglistNames.toArray(new String[0]);

                    // Set items for the alert dialog
                    alertDialogBuilder.setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int index, boolean isChecked) {
                            if (isChecked) {
                                // If the user checked the item, add it to the selected items
                                mSelecteditems.add(index);
                            } else if (mSelecteditems.contains(index)) {
                                // Else, if the item is already in the array, remove it
                                mSelecteditems.remove(Integer.valueOf(index));
                            }

                        }
                    });
                }
            }
        });


        // Set the action buttons
        alertDialogBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int index) {

                // Filter for checked shoppinglist from ArrayList
                List<ShoppingList> mSelectedShoppingLists = new ArrayList<>();
                for(int i = 0; i < mSelecteditems.size(); i++){
                    mSelectedShoppingLists.add(mDialogShoppingLists.get(mSelecteditems.get(i)));
                }

                saveProductToShopplingLists(mSelectedShoppingLists);


            }
        });

        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        alertDialogBuilder.show();

    }

    private void saveProductToShopplingLists(List<ShoppingList> shoppingLists){

        // TODO: add product to shopping list
        // check if the product is already in the shopping list



        Toast.makeText(ProductInfoFragment.this.getContext() ,
                "Added to  " + shoppingLists.size() + " shopping list(s)",
                Toast.LENGTH_LONG).show();
    }

    /*
    ************************************************************************************************
    *  Update product image
    ************************************************************************************************
     */

    private void updateImage(){
        final int DIALOG_INDEX_GALLERY = 0;
        final int DIALOG_INDEX_CAMERA = 1;

        String[] items = new String[] {"Gallery","Camera"};
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProductInfoFragment.this.getContext());
        alertDialogBuilder.setTitle("Please select an image");

        alertDialogBuilder.setItems(items, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                // Choose new product image from gallery
                if (which == DIALOG_INDEX_GALLERY){
                    ImageUpdater.getsInstance(ProductInfoFragment.this.getContext(), IMAGE_FILE_TYPE, mCurrentProductID)
                            .updateImageFromGallery();

                }
                // Choose new product image using camera
                else if (which == DIALOG_INDEX_CAMERA){
                    ImageUpdater imageUpdater = new ImageUpdater(getActivity() ,ProductInfoFragment.this.getContext(),
                            IMAGE_FILE_TYPE, mCurrentProductID, IMAGE_FOLDER, REQUEST_IMAGE_CAPTURE);
                    imageUpdater.updateImageFromCamera();

                    Log.d("Debug", "current product id: " + mCurrentProductID);
                }

            }
        });

        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        alertDialogBuilder.show();
    }


    private void setProductImage(){
        String imageFileName = IMAGE_FILE_TYPE + "_" + mCurrentProductID;
        File storageDir = ProductInfoFragment.this.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = new File(storageDir, imageFileName + ".jpg");
        if (image.exists()){

            mBitmap = ImageRotate.getsInstance(this.getContext()).rotateImage(image);

            // Update image view with rotated bitmap
            mBinding.imageProduct.setImageBitmap(mBitmap);
            mBinding.imageProductZoomed.setImageBitmap(mBitmap);

            // Reset image view
            mBinding.imageProduct.invalidate();
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
