package com.alvin.cheapyshopping.fragments;


import android.animation.Animator;
import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.alvin.cheapyshopping.R;
import com.alvin.cheapyshopping.databinding.AccountFragmentBinding;
import com.alvin.cheapyshopping.db.entities.Account;
import com.alvin.cheapyshopping.utils.ImageExpander;
import com.alvin.cheapyshopping.utils.ImageRotate;
import com.alvin.cheapyshopping.utils.ImageUpdater;
import com.alvin.cheapyshopping.viewmodels.AccountFragmentViewModel;

import java.io.File;

/**
 * Created by cheng on 11/26/2017.
 */

public class AccountFragment extends Fragment {


    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final String IMAGE_FILE_TYPE = "Account";
    private static final String IMAGE_FOLDER = "Account";


    /*
    ************************************************************************************************
    * Account Fragment Setup
    ************************************************************************************************
    */

    public static AccountFragment newInstance() {
        AccountFragment fragment = new AccountFragment();
        return fragment;
    }

    private AccountFragmentBinding mBinding;
    private AccountFragmentViewModel mViewModel;

    private long mCurrentAccountId;
    private Bitmap mBitmap;


    public AccountFragment(){}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        this.mBinding = AccountFragmentBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Setup ViewModel
        this.mViewModel = ViewModelProviders.of(this).get(AccountFragmentViewModel.class);


        // Get current account
        this.mViewModel.findCurrentAccount().observe(this, new Observer<Account>() {
            @Override
            public void onChanged(@Nullable Account account) {
                mBinding.setAccount(account);
                setAccountImage();
            }
        });


        // Update account image
        mBinding.imageEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateImage();
                setAccountImage();
            }
        });


    }


    /*
    ************************************************************************************************
    * Menu
    ************************************************************************************************
     */

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.account_fragment_menu, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_edit:
                // TODO: Edit Account
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /*
    ************************************************************************************************
    *  Update Account image
    ************************************************************************************************
     */

    private void updateImage(){
        final int DIALOG_INDEX_GALLERY = 0;
        final int DIALOG_INDEX_CAMERA = 1;

        String[] items = new String[] {"Gallery","Camera"};
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AccountFragment.this.getContext());
        alertDialogBuilder.setTitle("Please select an image");

        alertDialogBuilder.setItems(items, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                // Choose new product image from gallery
                if (which == DIALOG_INDEX_GALLERY){
                    ImageUpdater.getsInstance(AccountFragment.this.getContext(), IMAGE_FILE_TYPE, mCurrentAccountId)
                            .updateImageFromGallery();

                }
                // Choose new product image using camera
                else if (which == DIALOG_INDEX_CAMERA){
                    ImageUpdater imageUpdater = new ImageUpdater(getActivity() ,AccountFragment.this.getContext(),
                            IMAGE_FILE_TYPE, mCurrentAccountId, IMAGE_FOLDER, REQUEST_IMAGE_CAPTURE);
                    imageUpdater.updateImageFromCamera();

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


    private void setAccountImage(){
        String imageFileName = IMAGE_FILE_TYPE + "_" + mCurrentAccountId;
        File storageDir = AccountFragment.this.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = new File(storageDir, imageFileName + ".jpg");
        if (image.exists()){
            mBitmap = ImageRotate.getsInstance(AccountFragment.this.getContext()).rotateImage(image);

            // Update image view with rotated bitmap
            mBinding.imageProfile.setImageBitmap(mBitmap);

            // Reset image view
            mBinding.imageProfile.invalidate();
        }
    }



}
