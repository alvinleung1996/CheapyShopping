package com.alvin.cheapyshopping.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;

import java.io.File;

/**
 * Created by cheng on 11/27/2017.
 */

public class ImageRotate {

    @SuppressLint("StaticFieldLeak")
    private static ImageRotate sInstance;

    public static ImageRotate getsInstance(Context context) {
        if (sInstance == null) {
            sInstance = new ImageRotate(context);
        }
        return sInstance;
    }

    private Context mContext;

    private ImageRotate(Context context){
        this.mContext = context;
    }

    public Bitmap rotateImage(File image){
        // Rotate the image
        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath());
        try {
            ExifInterface exif = new ExifInterface(image.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            }
            else if (orientation == 3) {
                matrix.postRotate(180);
            }
            else if (orientation == 8) {
                matrix.postRotate(270);
            }
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true); // rotating bitmap
        }
        catch (Exception e) {

        }
        return bitmap;
    }

}
