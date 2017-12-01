package com.alvin.cheapyshopping.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.util.Log;

import com.alvin.cheapyshopping.BaseActivity;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by Alvin on 28/11/2017.
 */

public class LocationManager {

    public static final int REQUEST_LOCATION_SETTING = 1024;


    @SuppressLint("StaticFieldLeak")
    private static LocationManager sInstance;

    public static LocationManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new LocationManager(context);
        }
        return sInstance;
    }



    private final Context mContext;

    private LocationManager(Context context) {
        this.mContext = context.getApplicationContext();
    }




    private LiveLocation mLiveLocation;

    /**
     * Get a observable live data containing the location.
     * The location is not automatically updated until a call to
     * <code>updateLocation(activity)</code>.
     * @return
     */
    public LiveData<Location> getLocation() {
        if (this.mLiveLocation == null) {
            this.mLiveLocation = new LiveLocation();
        }
        return this.mLiveLocation;
    }

    /**
     * Update the location cache. Return a live promise once the location is updated.
     * The returned live promise is only updated once. To monitor location Change continuously,
     * observe the live data returned by <code>getLocation()</code>
     * @param activity
     * @return
     */
    public LivePromise<Location, Void> updateLocation(BaseActivity activity) {
        if (this.mLiveLocation == null) {
            this.mLiveLocation = new LiveLocation();
        }
        return this.mLiveLocation.update(activity);
    }

    public void onLocationRequestResult(int requestCode, int resultCode, Intent data) {
        if (this.mLiveLocation != null)
            switch (resultCode) {
                case RESULT_OK:
                    this.mLiveLocation.onLocationSettingEnabled();
                    break;
                case RESULT_CANCELED:
                    this.mLiveLocation.onLocationSettingDisabled();
                    break;
        }
    }

    private class LiveLocation extends MutableLiveData<Location> {

        private static final int STATE_IDLE = 0;
        private static final int STATE_REQUESTING_PERMISSION = 1;
        private static final int STATE_CHECKING_SETTINGS = 2;
        private static final int STATE_ASKING_SETTINGS = 3;
        private static final int STATE_GETTING_LOCATION = 4;
        private static final int STATE_LOCATION_READY = 5;

        private int mState;

        private LocationRequest mLocationRequest;
        private FusedLocationProviderClient mLocationProviderClient;
        private LocationCallback mLocationCallback;

        private MutableLivePromise<Location, Void> mPromise;

        private LiveLocation() {
            this.mState = STATE_IDLE;
        }

        private LivePromise<Location, Void> update(BaseActivity activity) {

            if (this.mState == STATE_IDLE) {
                this.mState = STATE_REQUESTING_PERMISSION;
                this.mPromise = new MutableLivePromise<>();

                LivePromise<Void, Void> promise = PermissionHelper.getsInstance(LocationManager.this.mContext)
                        .requestLocationPermission(activity);
                promise.observeReject(activity, v -> this.onLocationPermissionNotOk());
                promise.observeResolve(activity, v -> this.onLoationPermissionOk(activity));
            }

            return this.mPromise;
        }

        private void onLocationPermissionNotOk() {
            this.mState = STATE_IDLE;
            this.mPromise.setRejectValue(null);
            this.mPromise = null;
        }

        private void onLoationPermissionOk(BaseActivity activity) {
            this.mState = STATE_CHECKING_SETTINGS;

            this.mLocationRequest = new LocationRequest();
            this.mLocationRequest.setInterval(0);
            this.mLocationRequest.setFastestInterval(0);
            this.mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            LocationSettingsRequest settingsRequest = new LocationSettingsRequest.Builder()
                    .addLocationRequest(this.mLocationRequest).build();

            SettingsClient settingsClient = LocationServices.getSettingsClient(LocationManager.this.mContext);

            Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(settingsRequest);
            task.addOnFailureListener(e -> this.onLocationSettingNotOk((ApiException) e, activity));
            task.addOnSuccessListener(r -> this.onLocationSettingOK());
        }

        private void onLocationSettingNotOk(ApiException exception, BaseActivity activity) {
            switch (exception.getStatusCode()) {

                case CommonStatusCodes.RESOLUTION_REQUIRED:
                    this.mState = STATE_ASKING_SETTINGS;
                    try {
                        ((ResolvableApiException) exception).startResolutionForResult(activity, REQUEST_LOCATION_SETTING);
                    } catch (IntentSender.SendIntentException e) {
                        Log.e("location", "intent error", e);
                    }
                    break;

                default:
                    this.mPromise.setRejectValue(null);
                    this.mPromise = null;
                    this.mState = STATE_IDLE;
                    Log.e("location", "location setting not available");
            }
        }

        private void onLocationSettingDisabled() {
            this.mPromise.setRejectValue(null);
            this.mPromise = null;
            this.mState = STATE_IDLE;
        }

        private void onLocationSettingEnabled() {
            this.onLocationSettingOK();
        }

        @SuppressLint("MissingPermission")
        private void onLocationSettingOK() {
            this.mState = STATE_GETTING_LOCATION;
            this.mLocationProviderClient = new FusedLocationProviderClient(LocationManager.this.mContext);
            this.mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    LiveLocation.this.onLocationUpdate(locationResult);
                }
            };
            this.mLocationProviderClient.requestLocationUpdates(this.mLocationRequest, this.mLocationCallback, null);
        }

        private void onLocationUpdate(LocationResult locationResult) {
            Location location = locationResult.getLastLocation();
            if (location != null) {
                this.mState = STATE_IDLE;
                this.mLocationProviderClient.removeLocationUpdates(this.mLocationCallback);
                this.mPromise.setResolveValue(location);
                this.mPromise = null;
                this.setValue(location);
                String text = "Longitude: " + location.getLongitude() + ", Latitude: " + location.getLatitude();
                Log.d("location", text);
            }
        }

    }

}
