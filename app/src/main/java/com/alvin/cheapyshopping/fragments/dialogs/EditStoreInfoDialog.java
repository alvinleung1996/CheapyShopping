package com.alvin.cheapyshopping.fragments.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;

import com.alvin.cheapyshopping.databinding.EditStoreInfoDialogBinding;
import com.alvin.cheapyshopping.db.entities.Store;
import com.alvin.cheapyshopping.viewmodels.EditStoreDialogViewModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * Created by cheng on 12/8/2017.
 */

public class EditStoreInfoDialog extends DialogFragment {
    public static final String DIALOG_OK = "com.alvin.cheapyshopping.EditStoreInfoDialog.DIALOG_OK";
    public static final String DIALOG_CANCEL = "com.alvin.cheapyshopping.EditStoreInfoDialog.DIALOG_CANCEL";
    public static final String EXTRA_STORE = "com.alvin.cheapyshopping.EditStoreInfoDialog.EXTRA_STORE";


//    public interface InteractionListener {
//        void onOKAction(Product editedProduct);
//
//        void onCancelAction();
//    }


    public static EditStoreInfoDialog newInstance(Store store) {
        EditStoreInfoDialog dialogFragment = new EditStoreInfoDialog();

        // Convert product to Jason
        String productDataJason = new Gson().toJson(store);

        Bundle args = new Bundle();
        args.putString(EXTRA_STORE, productDataJason);
        dialogFragment.setArguments(args);

        return dialogFragment;
    }


    private EditStoreInfoDialogBinding mBinding;
    private EditStoreDialogViewModel mViewModel;

    private Store mStore;
    private Store mEditedStore;

    public EditStoreInfoDialog() {}

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mViewModel = ViewModelProviders.of(this).get(EditStoreDialogViewModel.class);

        // Convert Jason data back to Product
        Type type = new TypeToken<Store>(){}.getType();
        String productFromJason = getArguments().getString(EXTRA_STORE);
        mStore = new Gson().fromJson(productFromJason, type);

        mBinding = EditStoreInfoDialogBinding.inflate(getActivity().getLayoutInflater(), null);

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(mBinding.getRoot())
                .create();


        mBinding.inputTextStoreDescription.setText(mStore.getDescription());

        mBinding.ratingBar.setRating((float)mStore.getRating());

        mBinding.textCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        mBinding.textOk.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mEditedStore = mStore;
                mEditedStore.setDescription(mBinding.inputTextStoreDescription.getText().toString());
                mEditedStore.setRating(mBinding.ratingBar.getRating());

                ConfirmDialog confirmDialog = ConfirmDialog.newInstance("Confirm store editing?");
                confirmDialog.setInteractionListener(new ConfirmDialog.InteractionListener() {
                    @Override
                    public void onOKAction() {
                        mViewModel.updateStore(mEditedStore);
                        dialog.dismiss();
                        confirmDialog.dismiss();
                    }

                    @Override
                    public void onCancelAction() {
                        confirmDialog.dismiss();
                    }
                });
                confirmDialog.show(getFragmentManager(),null);

            }
        });


        return dialog;
    }

}