package com.alvin.cheapyshopping.fragments;


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
import com.alvin.cheapyshopping.databinding.AccountFragmentBinding;
import com.alvin.cheapyshopping.db.entities.Account;
import com.alvin.cheapyshopping.utils.ImageRotater;
import com.alvin.cheapyshopping.viewmodels.AccountFragmentViewModel;

import java.io.File;

/**
 * Created by cheng on 11/26/2017.
 */

public class AccountFragment extends Fragment {


    private static final int REQUEST_IMAGE_FROM_CAMERA = 1;
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

    private String mAccountId;
    private Account mAccount;


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
                mAccount = account;
                mAccountId = account.getAccountId();

                // Setup account image
                setAccountImage(account.isImageExist());
            }
        });


        // Update account image
        mBinding.imageEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateImageFragment updateImageFragment = UpdateImageFragment.newInstance(mAccountId, "Account");
                updateImageFragment.setInteractionListener(new UpdateImageFragmentInteractionListener());
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(mBinding.container.getId(), updateImageFragment)
                        .addToBackStack(null)
                        .commit();
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

    private class UpdateImageFragmentInteractionListener implements
            UpdateImageFragment.InteractionListener{
        @Override
        public void onGetImageUpdateResult(String result) {
            if (result.equals(UpdateImageFragment.IMAGE_UPDATED)){
                mViewModel.addCustomAccountImage(mAccount);
            }else if(result.equals(UpdateImageFragment.IMAGE_DELETED)){
                mViewModel.removeCustomAccountImage(mAccount);
            }
            // Remove the UpdateImageFragment
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }

    // Setup the profile image. Check if not custom image, set as default
    private void setAccountImage(boolean isCustom){
        String imageFileName = IMAGE_FILE_TYPE + "_" + mAccountId;
        File storageDir = AccountFragment.this.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = new File(storageDir, imageFileName + ".jpg");

        if (isCustom){
            if (imageFile.exists()) {
                Bitmap bitmap = ImageRotater.getsInstance(this.getContext()).rotateImage(imageFile);
                // Update image view with rotated bitmap
                mBinding.imageProfile.setImageBitmap(bitmap);
            }
        } else{
            mBinding.imageProfile.setImageResource(R.drawable.ic_account_circle_white);
        }
    }



}
