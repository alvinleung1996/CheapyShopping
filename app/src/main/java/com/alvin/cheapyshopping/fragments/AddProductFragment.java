package com.alvin.cheapyshopping.fragments;


import android.arch.core.util.Function;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.alvin.cheapyshopping.R;
import com.alvin.cheapyshopping.databinding.AddProductFragmentBinding;
import com.alvin.cheapyshopping.db.AppDatabaseCallback;
import com.alvin.cheapyshopping.db.entities.Setting;
import com.alvin.cheapyshopping.utils.ImageRotater;
import com.alvin.cheapyshopping.viewmodels.AddProductFragmentViewModel;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddProductFragment extends Fragment {

    public interface InteractionListener {

        void onDiscardOptionSelected(AddProductFragment fragment);

        void onNewProductAdded(AddProductFragment fragment, String productId);

    }


    private static final String ARGUMENT_CREATE_OPTIONS_MENU = "com.alvin.cheapyshopping.fragments.AddProductFragment.ARGUMENT_CREATE_OPTIONS_MENU";
    private static final String IMAGE_FILE_TYPE = "Product";


    public static AddProductFragment newInstance(boolean createOptionsMenu) {
        AddProductFragment fragment = new AddProductFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARGUMENT_CREATE_OPTIONS_MENU, createOptionsMenu);
        fragment.setArguments(args);
        return fragment;
    }


    public AddProductFragment() {
        // Required empty public constructor
    }


    private AddProductFragmentViewModel mViewModel;

    private AddProductFragmentBinding mBinding;

    private InteractionListener mInteractionListener;

    private boolean mProductImageIsSet = false;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = this.getArguments();
        if (args != null) {
            this.setHasOptionsMenu(args.getBoolean(ARGUMENT_CREATE_OPTIONS_MENU, true));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.mBinding = AddProductFragmentBinding.inflate(inflater, container, false);
        return this.mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.mViewModel = ViewModelProviders.of(this).get(AddProductFragmentViewModel.class);


        // Setup product image
        this.mViewModel.getSetting(AppDatabaseCallback.SETTING_NEW_PRODUCT_IMAGE).observe(this, new Observer<Setting>() {
            @Override
            public void onChanged(@Nullable Setting imageSetting) {
                if (imageSetting != null){
                    AddProductFragment.this.setProductImage(imageSetting.getSettingBoolean());
                }
            }
        });


        // Add product Image
        this.mBinding.imageEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateImageFragment updateImageFragment = UpdateImageFragment.newInstance("temp", "Product");
                updateImageFragment.setInteractionListener(new UpdateImageFragmentInteractionListener());
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(mBinding.container.getId(), updateImageFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.add_product_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_discard:
                this.onDiscardOptionItemSelected(item);
                return true;
            case R.id.item_save:
                this.onSaveOptionItemSelected(item);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDestroy() {
        mViewModel.newProductImageSettingIsSet(false);
        super.onDestroy();
    }

    public void setInteractionListener(InteractionListener listener) {
        this.mInteractionListener = listener;
    }


    private void onDiscardOptionItemSelected(MenuItem item) {
        if (this.mInteractionListener != null) {
            this.mInteractionListener.onDiscardOptionSelected(this);
            this.mViewModel.newProductImageSettingIsSet(false);
        }
    }

    private void onSaveOptionItemSelected(MenuItem item) {
        this.saveInput();
    }

    public void saveInput() {
        String productName = this.mBinding.inputLayoutProductName.getEditText().getText().toString();
        String productDescription = this.mBinding.inputLayoutProductDescription.getEditText().getText().toString();

        boolean error = false;

        if (productName.isEmpty()) {
            this.mBinding.inputLayoutProductName.setError("enter a product name pls");
            error = true;
        } else {
            this.mBinding.inputLayoutProductName.setError(null);
        }
        if (productDescription.isEmpty()) {
            this.mBinding.inputLayoutProductDescription.setError("enter a product description pls");
            error = true;
        } else {
            this.mBinding.inputLayoutProductDescription.setError(null);
        }

        if (error) {
            return;
        }

        this.mViewModel.addProduct(productName, productDescription, mProductImageIsSet, new Function<String, Void>() {
            @Override
            public Void apply(String productId) {
                if (AddProductFragment.this.mInteractionListener != null) {
                    AddProductFragment.this.mInteractionListener.onNewProductAdded(AddProductFragment.this, productId);
                    AddProductFragment.this.updateTempImageName(productId);
                }
                return null;
            }
        });

        this.mViewModel.newProductImageSettingIsSet(false);
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
                mProductImageIsSet = true;
                mViewModel.newProductImageSettingIsSet(true);
            }else if(result.equals(UpdateImageFragment.IMAGE_DELETED)){
                mProductImageIsSet = false;
                mViewModel.newProductImageSettingIsSet(false);
            }
            // Remove the UpdateImageFragment
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }

    // Setup the product image. Check if not custom image, set as default
    private void setProductImage(boolean isCustom){
        // Get new image name and path
        String imageFileName = IMAGE_FILE_TYPE + "_" + "temp";
        File storageDir = this.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = new File(storageDir, imageFileName + ".jpg");

        if (isCustom){
            if (imageFile.exists()){
                Bitmap bitmap = ImageRotater.getsInstance(this.getContext()).rotateImage(imageFile);
                // Update image view with rotated bitmap
                mBinding.imageView.setImageBitmap(bitmap);
            }
        } else {
            mBinding.imageView.setImageResource(R.drawable.ic_product_black_24dp);
        }
    }

    // Change temp image name
    private void updateTempImageName(String productId){
        String imageTempFileName = IMAGE_FILE_TYPE + "_" + "temp";
        String imageFileName = IMAGE_FILE_TYPE + "_" + productId;

        File storageDir = this.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageTempFile = new File(storageDir, imageTempFileName + ".jpg");
        File imageFile = new File(storageDir, imageFileName + ".jpg");

        imageTempFile.renameTo(imageFile);
    }

}
