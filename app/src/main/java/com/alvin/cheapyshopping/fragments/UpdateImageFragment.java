package com.alvin.cheapyshopping.fragments;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;


import com.alvin.cheapyshopping.fragments.dialogs.ChoosePictureSourceDialog;
import com.alvin.cheapyshopping.fragments.dialogs.ConfirmDialog;
import com.alvin.cheapyshopping.utils.ImageRotater;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Created by cheng on 11/30/2017.
 */

public class UpdateImageFragment extends Fragment {
    public static final String EXTRA_ID = "com.alvin.cheapyshopping.fragments.UpdateImageFragment.EXTRA_STORE_ID";
    public static final String EXTRA_TYPE = "com.alvin.cheapyshopping.fragments.UpdateImageFragment.EXTRA_TYPE";

    public static final String IMAGE_UPDATED = "com.alvin.cheapyshopping.fragments.UpdateImageFragment.IMAGE_UPDATED";
    public static final String IMAGE_NOT_UPDATED = "com.alvin.cheapyshopping.fragments.UpdateImageFragment.IMAGE_NOT_UPDATED";
    public static final String IMAGE_DELETED = "com.alvin.cheapyshopping.fragments.UpdateImageFragment.IMAGE_DELETED";


    private static final int REQUEST_IMAGE_FROM_CAMERA = 1;
    private static final int REQUEST_IMAGE_FROM_GALLERY = 2;


    public interface InteractionListener{
        void onGetImageUpdateResult(String result);
    }


    private String mId;
    private String mType;
    private File mImageFile;
    private Uri mImageUri;


    private InteractionListener mInteractionListener;


    public static UpdateImageFragment newInstance(String id, String fileType){
        UpdateImageFragment fragment = new UpdateImageFragment();


        Bundle args = new Bundle();

        args.putString(UpdateImageFragment.EXTRA_ID, id);
        args.putString(UpdateImageFragment.EXTRA_TYPE, fileType);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();

        String id = null;
        String type = null;
        if (args != null){
            if(args.containsKey(UpdateImageFragment.EXTRA_ID)){
                id = args.getString(UpdateImageFragment.EXTRA_ID);
            }
            if(args.containsKey(UpdateImageFragment.EXTRA_ID)){
                type = args.getString(UpdateImageFragment.EXTRA_TYPE);
            }
        }

        if (id == null || type == null){
            throw new RuntimeException("Missing data in args!");
        }

        mId = id;
        mType = type;

        String imageFileName = mType + "_" + mId;
        File storageDir = this.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        mImageFile = new File(storageDir, imageFileName + ".jpg");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        startImageUpdate();

    }



    public void setInteractionListener(InteractionListener interactionListener){
        if(mInteractionListener == null){
            this.mInteractionListener = interactionListener;
        }
    }

    private void setImageUpdateResult(String result){
        mInteractionListener.onGetImageUpdateResult(result);
    }

    private void startImageUpdate(){
        final ChoosePictureSourceDialog dialog = ChoosePictureSourceDialog.newInstance();
        dialog.setInteractionListener(new ChoosePictureSourceDialog.InteractionListener() {
            @Override
            public void PictureSourceActionChosen(String action) {
                if (action != null){
                    dialog.dismiss();
                    switch (action){
                        case ChoosePictureSourceDialog.DIALOG_CAMERA :
                            newImageFromCamera();
                            break;
                        case ChoosePictureSourceDialog.DIALOG_GALLERY:
                            newImageFromGallery();
                            break;
                        case ChoosePictureSourceDialog.DIALOG_DELETE:
                            deleteImage();
                            break;
                        case ChoosePictureSourceDialog.DIALOG_CANCEL:
                            getActivity().getSupportFragmentManager().popBackStack();
                            break;
                        default:
                            throw new IllegalArgumentException("Invalid action: " + action);
                    }

                }
            }
        });
        dialog.show(this.getFragmentManager(), null);
    }

    /*
    ************************************************************************************************
    * Get image from camera
    ************************************************************************************************
     */
    private void newImageFromCamera(){
        // Check for existing image and delete
        if (mImageFile.exists()){
            mImageFile.delete();
        }

        // New intent for camera
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(this.getContext().getPackageManager()) != null) {
            Uri photoURI = FileProvider.getUriForFile(this.getContext(),
                    "com.alvin.fileprovider",
                    mImageFile);
            mImageUri = photoURI;
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            takePictureIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
            this.startActivityForResult(takePictureIntent, REQUEST_IMAGE_FROM_CAMERA);
        }
    }


    /*
    ************************************************************************************************
    * Get image from gallery
    ************************************************************************************************
     */
    private void newImageFromGallery(){
        // TODO: fix image rotation problem

        // Check for existing image and delete
        if (mImageFile.exists()){
            mImageFile.delete();
        }

        // Intent for gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        this.startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_IMAGE_FROM_GALLERY);
    }


    /*
    ************************************************************************************************
    * Delete existing custom image
    ************************************************************************************************
     */

    private void deleteImage(){
        final ConfirmDialog dialog = ConfirmDialog.newInstance("Confirm delete product image?");
        dialog.setInteractionListener(new ConfirmDialog.InteractionListener() {
            @Override
            public void onOKAction() {
                if(mImageFile.exists()){
                    mImageFile.delete();

                    setImageUpdateResult(IMAGE_DELETED);
                }
            }
            @Override
            public void onCancelAction() {dialog.dismiss();}
        });

        dialog.show(this.getFragmentManager(), null);
    }

    /*
    ************************************************************************************************
    * Activity result from image capture function
    ************************************************************************************************
     */

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_IMAGE_FROM_CAMERA:
                if (resultCode == Activity.RESULT_OK){
                    Uri selectedImageUri = this.mImageUri;
                    imageUpdateFromActivityResult(selectedImageUri, REQUEST_IMAGE_FROM_CAMERA);

                }
                break;
            case REQUEST_IMAGE_FROM_GALLERY:
                if (resultCode == Activity.RESULT_OK){
                    Uri selectedImageUri = data.getData();
                    imageUpdateFromActivityResult(selectedImageUri, REQUEST_IMAGE_FROM_GALLERY);
                }
                break;

        }
    }

    private void imageUpdateFromActivityResult(Uri selectedImageUri, int request){
        getActivity().getContentResolver().notifyChange(selectedImageUri, null);
        ContentResolver cr = getActivity().getContentResolver();
        Bitmap bitmap;
        try {
            bitmap = android.provider.MediaStore.Images.Media
                    .getBitmap(cr, selectedImageUri);


            if (request == REQUEST_IMAGE_FROM_GALLERY){
                OutputStream outputStream;
                outputStream = new FileOutputStream(mImageFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 40, outputStream);
            }
            else if(request == REQUEST_IMAGE_FROM_CAMERA){
                bitmap = ImageRotater.getsInstance(getContext()).rotateImage(mImageFile);

                OutputStream outputStream;
                outputStream = new FileOutputStream(mImageFile);

                bitmap.compress(Bitmap.CompressFormat.JPEG, 10, outputStream);
            }


            setImageUpdateResult(IMAGE_UPDATED);

        } catch (Exception e) {
            Toast.makeText(getActivity(), "Failed to import image", Toast.LENGTH_SHORT)
                    .show();
            Log.e("Update image error", e.toString());
        }
    }


}
