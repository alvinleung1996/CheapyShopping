package com.alvin.cheapyshopping.fragments;


import android.animation.Animator;
import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.alvin.cheapyshopping.utils.ImageUpdater;
import com.alvin.cheapyshopping.ProductActivity;
import com.alvin.cheapyshopping.R;
import com.alvin.cheapyshopping.StoreActivity;
import com.alvin.cheapyshopping.databinding.ProductInfoFragmentBinding;
import com.alvin.cheapyshopping.databinding.ProductStorePriceItemBinding;
import com.alvin.cheapyshopping.db.entities.Product;
import com.alvin.cheapyshopping.db.entities.ShoppingList;
import com.alvin.cheapyshopping.db.entities.Store;
import com.alvin.cheapyshopping.db.entities.pseudo.StorePrice;
import com.alvin.cheapyshopping.utils.ImageExpander;
import com.alvin.cheapyshopping.viewmodels.ProductInfoFragmentViewModel;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static android.support.v4.content.FileProvider.getUriForFile;

/**
 * Created by cheng on 11/21/2017.
 */

public class ProductInfoFragment extends Fragment {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final String IMAGE_FILE_TYPE = "Product";
    private static final String IMAGE_FOLDER = "Product";


    private class ProductStorePriceListAdapter extends RecyclerView.Adapter<ProductStorePriceListAdapter.ProductStorePriceListItemViewHolder> {

        private List<StorePrice> mStorePrices;

        private ProductStorePriceListAdapter() {
            this.mStorePrices = new ArrayList<>();
        }

        public int getItemCount() {
            return mStorePrices.size();
        }

        public class ProductStorePriceListItemViewHolder extends RecyclerView.ViewHolder {
            private ProductStorePriceItemBinding mBinding;

            private ProductStorePriceListItemViewHolder(View v) {
                super(v);
                this.mBinding = DataBindingUtil.getBinding(this.itemView);
            }
        }

        @Override
        public ProductStorePriceListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = ProductStorePriceItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false).getRoot();
            ProductStorePriceListItemViewHolder viewHolder = new ProductStorePriceListItemViewHolder(view);
            return viewHolder;
        }


        @Override
        public void onBindViewHolder(ProductStorePriceListItemViewHolder holder, final int position) {
            holder.mBinding.setStorePrice(mStorePrices.get(position));

            holder.mBinding.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    ProductInfoFragment.this.onStoreClick(view, mStorePrices.get(position).getStore());
                }
            });

            // Set price update date & time
            SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy     HH:mm:ss");
            String updateDate = formatter.format(mStorePrices.get(position).getCreationTime());
            holder.mBinding.setDate(updateDate);
        }


        private void setStorePriceItems(List<StorePrice> items){
            this.mStorePrices = items;
            this.notifyDataSetChanged();
        }

        @Override
        public void onViewRecycled(ProductStorePriceListItemViewHolder holder) {
            super.onViewRecycled(holder);
            holder.mBinding.setStorePrice(null);
        }
    } // End of ProductStorePriceListAdapter



    public static ProductInfoFragment newInstance(long productID) {
        ProductInfoFragment fragment = new ProductInfoFragment();

        Bundle args = new Bundle();
        args.putLong(ProductActivity.EXTRA_PRODUCT_ID, productID);
        fragment.setArguments(args);
        return fragment;
    }


    private ProductInfoFragmentBinding mBinding;
    private ProductInfoFragmentViewModel mViewModel;

    private long mCurrentProductID;
    private Product mCurrentProduct;
    private StorePrice mCurrentBestProductStorePrice;
    private List<ShoppingList> mShoppingLists;

    private ProductStorePriceListAdapter mProductStorePriceItemListAdapter;

    private Animator mCurrentAnimator;


    public ProductInfoFragment(){
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);

        // Get Product ID
        Bundle args = getArguments();
        mCurrentProductID= args.getLong(ProductActivity.EXTRA_PRODUCT_ID, 0);

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

        // Setup recycler view
        this.mBinding.listProductPriceItems.setLayoutManager(new LinearLayoutManager(this.getContext()));
        this.mBinding.listProductPriceItems.setNestedScrollingEnabled(false); // For smoother scrolling
        this.mProductStorePriceItemListAdapter = new ProductStorePriceListAdapter();
        mBinding.listProductPriceItems.setAdapter(mProductStorePriceItemListAdapter);


        // Get Product
        this.mViewModel.getProduct(mCurrentProductID).observe(this, new Observer<Product>() {
            @Override
            public void onChanged(@Nullable Product product) {
                ProductInfoFragment.this.mCurrentProduct = product;

                // Setup Product Basic Info
                mBinding.setProduct(mCurrentProduct);
            }
        });

        // Get StorePrice list
        this.mViewModel.getStorePrices(mCurrentProductID).observe(this, new Observer<List<StorePrice>>() {
            @Override
            public void onChanged(@Nullable List<StorePrice> storePrices) {
                ProductInfoFragment.this.mProductStorePriceItemListAdapter.setStorePriceItems(storePrices);
            }
        });

        // Get best StorePrice
        this.mViewModel.getBestStorePrice(mCurrentProductID).observe(this, new Observer<StorePrice>() {
            @Override
            public void onChanged(@Nullable StorePrice storePrice) {
                ProductInfoFragment.this.mCurrentBestProductStorePrice = storePrice;
                ProductInfoFragment.this.mBinding.setBestPrice(storePrice);


                Log.d("Debug Best price: ", "$" + storePrice.getTotal());
                Log.d("Debug: ", "Best price Store - " + storePrice.getStore().getName());
                Log.d("Debug: ", "Best price  - " + storePrice.getStore().getName());

                // Set price update date & time
                SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
                String updateDate = formatter.format(storePrice.getCreationTime());
                ProductInfoFragment.this.mBinding.setBestPriceDate(updateDate);

            }
        });

        // Setup product image
        setProductImage();


        // Product Image Click to zoom
        this.mBinding.imageProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageExpander.getsInstance(getContext(), mCurrentAnimator, mBinding.container,
                        mBinding.imageProduct, mBinding.imageProductZoomed,
                        R.drawable.ic_product_black_24dp, 300)
                        .expandImage();
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
            Uri photoURI = getUriForFile(ProductInfoFragment.this.getContext(),
                    "com.alvin.fileprovider",
                    image);


            // Rotate the image
            Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath());
            try {
                ExifInterface exif = new ExifInterface(image.getAbsolutePath());
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                Log.d("EXIF", "Exif: " + orientation);
                Matrix matrix = new Matrix();
                if (orientation == 6) {
                    matrix.postRotate(90);
                }
                else if (orientation == 3) {
                    matrix.postRotate(180);
                }
                else if (orientation == 8) {
                    matrix.postRotate(270);
                }
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true); // rotating bitmap
            }
            catch (Exception e) {

            }
            mBinding.imageProduct.setImageBitmap(bitmap);
//            mBinding.imageProduct.setImageURI(null);
//            mBinding.imageProduct.setImageURI(photoURI);
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
