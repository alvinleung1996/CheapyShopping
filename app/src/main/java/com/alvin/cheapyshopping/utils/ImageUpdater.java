package com.alvin.cheapyshopping.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.support.v4.content.FileProvider.getUriForFile;

/**
 * Created by cheng on 11/26/2017.
 */

public class ImageUpdater {


    @SuppressLint("StaticFieldLeak")
    private static ImageUpdater sInstance;

    public static ImageUpdater getsInstance(Activity activity , Context context, String fileType, String fileId, String folder, int captureNum ) {
        if (sInstance == null) {
            sInstance = new ImageUpdater(activity, context, fileType, fileId, folder, captureNum);
        }
        return sInstance;
    }

    public static ImageUpdater getsInstance(Context context, String fileType, String fileId) {
        if (sInstance == null) {
            sInstance = new ImageUpdater(context, fileType, fileId);
        }
        return sInstance;
    }

    private Activity mActivity;
    private Context mContext;
    private String mFileId;
    private String mFileType;
    private String mFolder;
    private int mCaptureNum;


    public ImageUpdater(Activity activity, Context context, String fileType, String fileId, String folder, int captureNum) {
        this.mActivity = activity;
        this.mContext = context.getApplicationContext();
        this.mFileId = fileId;
        this.mFileType = fileType;
        this.mFolder = folder;
        this.mCaptureNum = captureNum;
    }

    public ImageUpdater(Context context, String fileType, String fileId) {
        this.mContext = context.getApplicationContext();
        this.mFileId = fileId;
    }


    public void updateImageFromCamera(){
        Toast.makeText(mContext, "Camera", Toast.LENGTH_LONG).show();
        dispatchTakePictureIntent();
    }

    public void updateImageFromGallery(){
        Toast.makeText(mContext, "Gallery", Toast.LENGTH_LONG).show();

    }


    String mCurrentPhotoPath;

    // Create image file with predefined file name
    private File createImageFile() throws IOException {
        // Create an image file name
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = mFileType + "_" + mFileId;
        File storageDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = new File(storageDir, imageFileName + ".jpg");
        if (image.exists()){
            image.delete();
            image = new File(storageDir, imageFileName + ".jpg");
        }


        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    // Camera intent for taking the photo
    private void dispatchTakePictureIntent(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(mContext.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = getUriForFile(mContext,
                        "com.alvin.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                takePictureIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
                mActivity.startActivityForResult(takePictureIntent, mCaptureNum);

            }
        }
    }

}
