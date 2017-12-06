package com.alvin.cheapyshopping.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Path;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by cheng on 11/27/2017.
 */

public class ImageRotater {

    @SuppressLint("StaticFieldLeak")
    private static ImageRotater sInstance;

    public static ImageRotater getsInstance(Context context) {
        if (sInstance == null) {
            sInstance = new ImageRotater(context);
        }
        return sInstance;
    }

    private Context mContext;
    private Bitmap mBitmap;

    private ImageRotater(Context context){
        this.mContext = context.getApplicationContext();
    }

    public Bitmap rotateImage(final File image){
        mBitmap = BitmapFactory.decodeFile(image.getAbsolutePath());

        // Rotate the image
        try {
            ExifInterface exif = new ExifInterface(image.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
//            Toast.makeText(mContext, "" + orientation, Toast.LENGTH_LONG).show();
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
            ImageRotater.this.mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true); // rotating bitmap
        }
        catch (Exception e) {
            Log.e("Rotate bitmap error", e.toString());
        }

        return mBitmap;

    }




}
