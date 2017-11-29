package com.alvin.cheapyshopping.fragments;


import android.animation.Animator;
import android.app.Activity;
import android.app.AlertDialog;
import android.arch.core.util.Function;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ContentResolver;
import android.content.DialogInterface;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.alvin.cheapyshopping.fragments.dialogs.ChoosePictureSourceDialog;
import com.alvin.cheapyshopping.fragments.dialogs.ChooseShoppingListProductRelationQuantityDialog;
import com.alvin.cheapyshopping.fragments.dialogs.ChooseShoppingListsDialog;
import com.alvin.cheapyshopping.fragments.dialogs.ConfirmDialog;
import com.alvin.cheapyshopping.utils.ImageRotater;
import com.alvin.cheapyshopping.R;
import com.alvin.cheapyshopping.StoreActivity;
import com.alvin.cheapyshopping.databinding.ProductInfoFragmentBinding;
import com.alvin.cheapyshopping.db.entities.Product;
import com.alvin.cheapyshopping.db.entities.ShoppingList;
import com.alvin.cheapyshopping.db.entities.Store;
import com.alvin.cheapyshopping.utils.ImageExpander;
import com.alvin.cheapyshopping.viewmodels.ProductInfoFragmentViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

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
    private Uri mImageUri;
    private Bitmap mBitmap;
    private File mImageFile;


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

                // Setup product image
                if (product.isImageExist()){
                    setProductImage(true);
                }else{
                    setProductImage(false);
                }
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
                updateImage();
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
                saveProductToShoppingLists();
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
    * Dialogs for adding product to shopping List
    ************************************************************************************************
     */

    public void saveProductToShoppingLists() {
        // Only add to shopping list(s) without the product
        mViewModel.findShoppingListsNotContainProductNow(mCurrentProductID, new Function<List<ShoppingList>, Void>() {
            @Override
            public Void apply(List<ShoppingList> shoppingLists) {
                boolean canProceed = false;
                if (shoppingLists != null){
                    if(shoppingLists.size() > 0){
                        canProceed = true;
                    }
                }
                if (canProceed){
                    ChooseShoppingListsDialog dialog = ChooseShoppingListsDialog.newInstance(shoppingLists);
                    dialog.show(ProductInfoFragment.this.getFragmentManager(), null);
                    dialog.setInteractionListener(new ChooseShoppingListsDialog.InteractionListener() {
                        @Override
                        public void onSelectShoppingListsConfirmed(List<ShoppingList> shoppingLists) {
                            // Get ShoppingLists' ID
                            List<String> shoppingListIds = new ArrayList<>(shoppingLists.size());
                            for(ShoppingList shoppingList: shoppingLists){
                                shoppingListIds.add(shoppingList.getShoppingListId());
                            }
                            ProductInfoFragment.this.saveProductToSelectedShoppingLists(shoppingListIds);
                        }
                    });
                }else {
                    ConfirmDialog dialog = ConfirmDialog.newInstance("No available shopping list.");
                    dialog.show(ProductInfoFragment.this.getFragmentManager(), null);
                }
                return null;
            }
        });

    }

    private void saveProductToSelectedShoppingLists(final List<String> shoppingListIds){
        // Choose the quantity to be added
        ChooseShoppingListProductRelationQuantityDialog dialog = ChooseShoppingListProductRelationQuantityDialog.newInstance();
        dialog.setInteractionListener(new ChooseShoppingListProductRelationQuantityDialog.InteractionListener() {
            @Override
            public void onQuantityChosen(int quantity) {
                mViewModel.addProductToShoppingLists(mCurrentProductID, shoppingListIds, quantity);

                Toast.makeText(ProductInfoFragment.this.getContext() ,
                        "Added to  " + shoppingListIds.size() + " shopping list(s)",
                        Toast.LENGTH_LONG).show();
            }
        });
        dialog.show(this.getFragmentManager(), null);
    }

    /*
    ************************************************************************************************
    *  Update product image
    ************************************************************************************************
     */

    private void updateImage(){
        final ChoosePictureSourceDialog dialog = ChoosePictureSourceDialog.newInstance();
        dialog.setInteractionListener(new ChoosePictureSourceDialog.InteractionListener() {
            @Override
            public void PictureSourceActionChosen(String action) {
                if (action != null){
                    dialog.dismiss();
                    if (action.equals(ChoosePictureSourceDialog.DIALOG_CAMERA)){
                        ProductInfoFragment.this.newImageFromCamera();;
                    }else if(action.equals(ChoosePictureSourceDialog.DIALOG_GALLERY)){
                        ProductInfoFragment.this.newImageFromGallery();;
                    }else if(action.equals(ChoosePictureSourceDialog.DIALOG_DELETE)){
                        ProductInfoFragment.this.deleteImage();;
                    }
                }
            }
        });
        dialog.show(this.getFragmentManager(), null);

    }

    // Get image from camera
    private void newImageFromCamera(){
        // Check for existing image and delete
        if (mImageFile.exists()){
            mImageFile.delete();
        }

        // New intent for camera
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(this.getContext().getPackageManager()) != null) {
            Uri photoURI = FileProvider.getUriForFile(this.getContext(),
                    "com.alvin.fileprovider",
                    mImageFile);
            mImageUri = photoURI;
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            takePictureIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
            this.startActivityForResult(takePictureIntent, REQUEST_IMAGE_FROM_CAMERA);
        }
    }

    // Get image from gallery
    private void newImageFromGallery(){
        // TODO: fix image rotation problem

        // Check for existing image and delete
        if (mImageFile.exists()){
            mImageFile.delete();
        }

        // Intent for gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        this.startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_IMAGE_FROM_GALLERY);
    }

    // Delete existing custom image
    private void deleteImage(){
        final ConfirmDialog dialog = ConfirmDialog.newInstance("Confirm delete product image?");
        dialog.setInteractionListener(new ConfirmDialog.InteractionListener() {
            @Override
            public void onOKAction() {
                if(mImageFile.exists()){
                    mImageFile.delete();
                    //Update database
                    mCurrentProduct.setImageExist(false);
                    mViewModel.removeCustomProductImage(mCurrentProduct);
                }
            }
            @Override
            public void onCancelAction() {dialog.dismiss();}
        });

        dialog.show(this.getFragmentManager(), null);
    }


    // Setup the product image. Check if not custom image, set as default
    private void setProductImage(boolean isCustom){
        // Get new image name and path
        String imageFileName = IMAGE_FILE_TYPE + "_" + mCurrentProductID;
        File storageDir = this.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        mImageFile = new File(storageDir, imageFileName + ".jpg");

        if (isCustom){
            if (mImageFile.exists()){
                mBitmap = ImageRotater.getsInstance(this.getContext()).rotateImage(mImageFile);
                // Update image view with rotated bitmap
                mBinding.imageProduct.setImageBitmap(mBitmap);
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


    /*
    ************************************************************************************************
    * Activity result, mainly for image capture function
    ************************************************************************************************
     */

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_IMAGE_FROM_CAMERA:
                if (resultCode == Activity.RESULT_OK){
                    Uri selectedImage = this.mImageUri;
                    imageUpdateFromActivityResult(selectedImage, REQUEST_IMAGE_FROM_CAMERA);

                }
                break;
            case REQUEST_IMAGE_FROM_GALLERY:
                if (resultCode == Activity.RESULT_OK){
                    Uri selectedImage = data.getData();
                    imageUpdateFromActivityResult(selectedImage, REQUEST_IMAGE_FROM_GALLERY);
                }
                break;

        }
    }

    private void imageUpdateFromActivityResult(Uri selectedImageUri, int request){
        getActivity().getContentResolver().notifyChange(selectedImageUri, null);
        ContentResolver cr = getActivity().getContentResolver();
        Bitmap bitmap;
        try {
            bitmap = android.provider.MediaStore.Images.Media
                    .getBitmap(cr, selectedImageUri);


            if (request == REQUEST_IMAGE_FROM_GALLERY){
                // Copy image to app dir and compress
                OutputStream outputStream;
                outputStream = new FileOutputStream(mImageFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
            }

            // Update database
            mCurrentProduct.setImageExist(true);
            mViewModel.addCustomProductImage(mCurrentProduct);

        } catch (Exception e) {
            Toast.makeText(getActivity(), "Failed to import image", Toast.LENGTH_SHORT)
                    .show();
            Log.e("Update image error", e.toString());
        }
    }


}
