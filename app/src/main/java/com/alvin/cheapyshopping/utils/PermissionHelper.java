package com.alvin.cheapyshopping.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

/**
 * Created by Alvin on 27/11/2017.
 */

public class PermissionHelper {

    @SuppressLint("StaticFieldLeak")
    private static PermissionHelper sInstance;

    public static PermissionHelper getsInstance(Context context) {
        if (sInstance == null) {
            sInstance = new PermissionHelper(context);
        }
        return sInstance;
    }



    private final Context mContext;

    private PermissionHelper(Context context) {
        this.mContext = context.getApplicationContext();
    }



    private static final int REQUEST_LOCATION = 1;

    public boolean getLocationPermission(Activity activity) {
        return this.getLocationPermission(activity, null);
    }
    public boolean getLocationPermission(Fragment fragment) {
        return this.getLocationPermission(null, fragment);
    }
    private boolean getLocationPermission(Activity activity, Fragment fragment) {
        if (ContextCompat.checkSelfPermission(this.mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
            if (activity != null) {
                ActivityCompat.requestPermissions(activity, permissions, REQUEST_LOCATION);
            } else if (fragment != null) {
                fragment.requestPermissions(permissions, REQUEST_LOCATION);
            }
            return false;
        }
    }

}
