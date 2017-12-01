package com.alvin.cheapyshopping.fragments.dialogs;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.alvin.cheapyshopping.databinding.ChooseImageSourceDialogBinding;

/**
 * Created by cheng on 11/28/2017.
 */

public class ChoosePictureSourceDialog extends DialogFragment{
    public static final String DIALOG_GALLERY = "com.alvin.cheapyshopping.ChoosePictureSourceDialog.DIALOG_GALLERY";
    public static final String DIALOG_CAMERA = "com.alvin.cheapyshopping.ChoosePictureSourceDialog.DIALOG_CAMERA";
    public static final String DIALOG_DELETE = "com.alvin.cheapyshopping.ChoosePictureSourceDialog.DIALOG_DELETE";
    public static final String DIALOG_CANCEL = "com.alvin.cheapyshopping.ChoosePictureSourceDialog.DIALOG_CANCEL";


    public interface InteractionListener {
        void PictureSourceActionChosen(String action);
    }


    public static ChoosePictureSourceDialog newInstance() {return new ChoosePictureSourceDialog();}


    private ChoosePictureSourceDialog.InteractionListener mInteractionListener;


    public ChoosePictureSourceDialog() {}

    private ChooseImageSourceDialogBinding mBinding;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        mBinding = ChooseImageSourceDialogBinding.inflate(getActivity().getLayoutInflater(), null);

//        String[] items = new String[] {"Gallery","Camera"};


        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(mBinding.getRoot())
                .create();

        // set dialog at the bottom
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);



        mBinding.containerCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChoosePictureSourceDialog.this.onActionChosen(DIALOG_CAMERA);
            }
        });

        mBinding.containerGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChoosePictureSourceDialog.this.onActionChosen(DIALOG_GALLERY);
            }
        });

        mBinding.containerDelete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                ChoosePictureSourceDialog.this.onActionChosen(DIALOG_DELETE);
            }
        });

        dialog.setCanceledOnTouchOutside(true);

        return dialog;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        onActionChosen(DIALOG_CANCEL);
    }

    public void setInteractionListener(ChoosePictureSourceDialog.InteractionListener listener) {
        this.mInteractionListener = listener;
    }

    private void onActionChosen(String action) {
        if (this.mInteractionListener != null) {
            this.mInteractionListener.PictureSourceActionChosen(action);
        }
    }


}
