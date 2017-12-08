package com.alvin.cheapyshopping.fragments.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;

import com.alvin.cheapyshopping.databinding.EditProductInfoDialogBinding;
import com.alvin.cheapyshopping.db.entities.Product;
import com.alvin.cheapyshopping.viewmodels.EditProductDialogViewModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * Created by cheng on 12/8/2017.
 */

public class EditProductInfoDialog extends DialogFragment {
    public static final String DIALOG_OK = "com.alvin.cheapyshopping.EditProductInfoDialog.DIALOG_OK";
    public static final String DIALOG_CANCEL = "com.alvin.cheapyshopping.EditProductInfoDialog.DIALOG_CANCEL";
    public static final String EXTRA_PRODUCT = "com.alvin.cheapyshopping.EditProductInfoDialog.EXTRA_PRODUCT";


//    public interface InteractionListener {
//        void onOKAction(Product editedProduct);
//
//        void onCancelAction();
//    }


    public static EditProductInfoDialog newInstance(Product product) {
        EditProductInfoDialog dialogFragment = new EditProductInfoDialog();

        // Convert product to Jason
        String productDataJason = new Gson().toJson(product);

        Bundle args = new Bundle();
        args.putString(EXTRA_PRODUCT, productDataJason);
        dialogFragment.setArguments(args);

        return dialogFragment;
    }


    private EditProductInfoDialogBinding mBinding;
    private EditProductDialogViewModel mViewModel;

    private Product mProduct;
    private Product mEditedProduct;

    public EditProductInfoDialog() {}

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mViewModel = ViewModelProviders.of(this).get(EditProductDialogViewModel.class);

        // Convert Jason data back to Product
        Type type = new TypeToken<Product>(){}.getType();
        String productFromJason = getArguments().getString(EXTRA_PRODUCT);
        mProduct = new Gson().fromJson(productFromJason, type);

        mBinding = EditProductInfoDialogBinding.inflate(getActivity().getLayoutInflater(), null);

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(mBinding.getRoot())
                .create();

        mBinding.inputTextProductName.setText(mProduct.getName());

        mBinding.inputTextProductDescription.setText(mProduct.getDescription());

        mBinding.ratingBar.setRating((float)mProduct.getRating());

        mBinding.textCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        mBinding.textOk.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mEditedProduct = mProduct;
                mEditedProduct.setName(mBinding.inputTextProductName.getText().toString());
                mEditedProduct.setDescription(mBinding.inputTextProductDescription.getText().toString());
                mEditedProduct.setRating(mBinding.ratingBar.getRating());

                ConfirmDialog confirmDialog = ConfirmDialog.newInstance("Confirm product editing?");
                confirmDialog.setInteractionListener(new ConfirmDialog.InteractionListener() {
                    @Override
                    public void onOKAction() {
                        mViewModel.updateProduct(mEditedProduct);
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
