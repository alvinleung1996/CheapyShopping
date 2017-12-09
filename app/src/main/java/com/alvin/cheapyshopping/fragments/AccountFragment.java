package com.alvin.cheapyshopping.fragments;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Debug;
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

import com.alvin.cheapyshopping.MainActivity;
import com.alvin.cheapyshopping.R;
import com.alvin.cheapyshopping.databinding.AccountFragmentBinding;
import com.alvin.cheapyshopping.db.entities.Account;
import com.alvin.cheapyshopping.db.entities.Rank;
import com.alvin.cheapyshopping.fragments.dialogs.EditAccountInfoDialog;
import com.alvin.cheapyshopping.utils.ImageRotater;
import com.alvin.cheapyshopping.viewmodels.AccountFragmentViewModel;

import java.io.File;
import java.util.List;

/**
 * Created by cheng on 11/26/2017.
 */

public class AccountFragment extends MainActivity.MainFragment {


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

    private Account mAccount;

    private boolean mEditButtonIsClicked = false;


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

        setFragmentTitle("Account");

        // Setup ViewModel
        this.mViewModel = ViewModelProviders.of(this).get(AccountFragmentViewModel.class);


        // Get current account
        this.mViewModel.findCurrentAccount().observe(this, new Observer<Account>() {
            @Override
            public void onChanged(@Nullable Account account) {
                mBinding.setAccount(account);
                mAccount = account;

                // Setup account image
                setAccountImage(account.isImageExist());
            }
        });

        // Get current account ranks
        this.mViewModel.getCurrentAccountRanks().observe(this, new Observer<List<Rank>>() {
            @Override
            public void onChanged(@Nullable List<Rank> ranks) {
                if(ranks != null){
                    mBinding.textAccountRank.setText(ranks.get(0).getRank());
                    // Setup rank progress bar
                    if(mAccount != null){
                        updateRankProgressBar(ranks.get(0).getMinScore(), ranks.get(0).getMaxScore(), mAccount.getAccountScore(), (ranks.size() - 1));
                    }
                }
            }
        });


        // Update account image
        mBinding.imageEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateImageFragment updateImageFragment = UpdateImageFragment.newInstance(mAccount.getAccountId(), "Account");
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
                mEditButtonIsClicked = true;
                editAccount();
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
        String imageFileName = IMAGE_FILE_TYPE + "_" + mAccount.getAccountId();
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

    /*
    ************************************************************************************************
    * Update rank progress bar
    ************************************************************************************************
     */

    private void updateRankProgressBar(int minRankScore, int maxRankScore, int accountScore, int rank){
        int progress;
        progress = 100 * (accountScore - minRankScore) / (maxRankScore - minRankScore);
        mBinding.progressBarRank.setProgress(progress);
        mBinding.textProgressMinScore.setText(Integer.toString(minRankScore));
        mBinding.textProgressMaxCore.setText(Integer.toString(maxRankScore));

        int resId = getResources().getIdentifier(
                "badge_rank_" + rank,
                "drawable", getActivity().getPackageName());
        mBinding.imageAccountBadge.setImageResource(resId);

    }

    /*
    ************************************************************************************************
    * Edit account
    ************************************************************************************************
     */

    private void editAccount(){
        this.mViewModel.findCurrentAccount().observe(this, new Observer<Account>() {
            @Override
            public void onChanged(@Nullable Account account) {
                if(mEditButtonIsClicked && account != null){
                    EditAccountInfoDialog dialog = EditAccountInfoDialog.newInstance(account);
                    dialog.show(getFragmentManager(),null);
                }
                mEditButtonIsClicked = false;
            }
        });
    }



}
