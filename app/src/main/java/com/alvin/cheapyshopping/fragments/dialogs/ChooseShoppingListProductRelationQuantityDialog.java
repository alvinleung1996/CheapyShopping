package com.alvin.cheapyshopping.fragments.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.alvin.cheapyshopping.databinding.ChooseShoppingListProductRelationQuantityDialogBinding;

/**
 * Created by Alvin on 28/11/2017.
 */

public class ChooseShoppingListProductRelationQuantityDialog extends DialogFragment {

    public interface InteractionListener {

        void onQuantityChosen(int quantity);

    }



    public static ChooseShoppingListProductRelationQuantityDialog newInstance() {
        return new ChooseShoppingListProductRelationQuantityDialog();
    }


    private InteractionListener mInteractionListener;

    public ChooseShoppingListProductRelationQuantityDialog() {

    }

    private ChooseShoppingListProductRelationQuantityDialogBinding mBinding;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        this.mBinding = ChooseShoppingListProductRelationQuantityDialogBinding.inflate(this.getActivity().getLayoutInflater(), null);

        this.mBinding.numberPicker.setMinValue(1);
        this.mBinding.numberPicker.setMaxValue(100);
        this.mBinding.numberPicker.setWrapSelectorWheel(false);

        AlertDialog dialog = new AlertDialog.Builder(this.getContext())
                .setTitle("Choose quantity")
                .setView(this.mBinding.getRoot())
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ChooseShoppingListProductRelationQuantityDialog.this.onCancelButtonClick();
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ChooseShoppingListProductRelationQuantityDialog.this.onOKButtonClick();
                    }
                })
                .create();

        return dialog;
    }

    public void setInteractionListener(InteractionListener listener) {
        this.mInteractionListener = listener;
    }

    private void onCancelButtonClick() {
        this.dismiss();
    }

    private void onOKButtonClick() {
        if (this.mInteractionListener != null) {
            this.mInteractionListener.onQuantityChosen(this.mBinding.numberPicker.getValue());
        }
    }
}
