package com.alvin.cheapyshopping.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.alvin.cheapyshopping.BaseActivity;
import com.alvin.cheapyshopping.fragments.BaseFragment;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

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


    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_LOCATION:
                this.onRequestLocationPermissionResult(requestCode, permissions, grantResults);
                break;
        }
    }


    /*
    ************************************************************************************************
    * Fine location request
    ************************************************************************************************
     */

    private static final int PERMISSION_REQUEST_LOCATION = 2048;

    private LocationPermissionRequester mLocationPermissionRequester;

    public LivePromise0<Void, Void> requestLocationPermission(BaseActivity activity) {
        return this.requestLocationPermission(activity, null);
    }

    public LivePromise0<Void, Void> requestLocationPermission(BaseFragment fragment) {
        return this.requestLocationPermission(null, fragment);
    }

    private LivePromise0<Void, Void> requestLocationPermission(BaseActivity activity, BaseFragment fragment) {
        if (this.mLocationPermissionRequester == null) {
            this.mLocationPermissionRequester = new LocationPermissionRequester();
        }
        return this.mLocationPermissionRequester.request(activity, fragment);
    }

    private void onRequestLocationPermissionResult(int requestCode, String[] permissions, int[] grantResults) {
        if (this.mLocationPermissionRequester != null) {
            this.mLocationPermissionRequester.onPermissionRequestResult(requestCode, permissions, grantResults);
        }
    }

    private class LocationPermissionRequester {

        private MutableLivePromise0<Void, Void> mPromise;

        private LivePromise0<Void, Void> request(BaseActivity activity, BaseFragment fragment) {
            if (this.mPromise != null) {
                return this.mPromise;
            }

            LivePromise0<Void, Void> promise = this.mPromise = new MutableLivePromise0<>();

            if (ContextCompat.checkSelfPermission(PermissionHelper.this.mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PERMISSION_GRANTED) {
                this.onPermissionOk();

            } else {
                String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
                if (activity != null) {
                    ActivityCompat.requestPermissions(activity, permissions, PERMISSION_REQUEST_LOCATION);
                } else if (fragment != null) {
                    fragment.requestPermissions(permissions, PERMISSION_REQUEST_LOCATION);
                }
            }

            return promise;
        }

        private void onPermissionRequestResult(int requestCode, String[] permissions, int[] grantResults) {
            if (this.mPromise == null) {
                return;
            }

            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }

            if (allGranted) {
                this.onPermissionOk();
            } else {
                this.onPermissionNotOk();
            }
        }

        private void onPermissionNotOk() {
            this.mPromise.setRejectValue(null);
            this.mPromise = null;
        }

        private void onPermissionOk() {
            this.mPromise.setResolveValue(null);
            this.mPromise = null;
        }

    }

}
