package com.alvin.cheapyshopping.fragments.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * Created by cheng on 11/29/2017.
 */

public class ConfirmDialog extends DialogFragment {
    public static final String DIALOG_OK = "com.alvin.cheapyshopping.ChoosePictureSourceDialog.DIALOG_OK";
    public static final String DIALOG_CANCEL = "com.alvin.cheapyshopping.ChoosePictureSourceDialog.DIALOG_CANCEL";
    public static final String ARGUMENT_MESSAGE = "com.alvin.cheapyshopping.ChoosePictureSourceDialog.ARGUMENT_MESSAGE";


    public interface InteractionListener {
        void onOKAction();

        void onCancelAction();
    }


    public static ConfirmDialog newInstance(String dialogMessage) {
        ConfirmDialog dialogFragment = new ConfirmDialog();
        Bundle args = new Bundle();
        args.putString(ARGUMENT_MESSAGE, dialogMessage);
        dialogFragment.setArguments(args);

        return dialogFragment;
    }

    public static ConfirmDialog newInstance(String dialogMessage, boolean showCancel) {
        ConfirmDialog dialogFragment = new ConfirmDialog();
        Bundle args = new Bundle();
        args.putString(ARGUMENT_MESSAGE, dialogMessage);
        dialogFragment.setArguments(args);

        dialogFragment.showCancel = showCancel;

        return dialogFragment;
    }



    private ConfirmDialog.InteractionListener mInteractionListener;

    private String mDialogMessage;
    private boolean showCancel = true;

    public ConfirmDialog() {}

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Get dialog message
        Bundle args = getArguments();
        mDialogMessage = args.getString(ARGUMENT_MESSAGE);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity())
                .setTitle("Alert")
                .setMessage(mDialogMessage)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ConfirmDialog.this.onActionChosen(DIALOG_OK);
                    }
                });
        if (showCancel){
            dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ConfirmDialog.this.onActionChosen(DIALOG_CANCEL);
                }
            });
        }


        return dialogBuilder.create();
    }

    public void setInteractionListener(ConfirmDialog.InteractionListener listener) {
        this.mInteractionListener = listener;
    }

    private void onActionChosen(String action) {
        if (this.mInteractionListener != null) {
            if (action.equals(DIALOG_OK)){
                mInteractionListener.onOKAction();
            }else if (action.equals(DIALOG_CANCEL)){
                mInteractionListener.onCancelAction();
            }
        }
    }

}