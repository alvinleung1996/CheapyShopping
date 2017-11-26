package com.alvin.cheapyshopping.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArraySet;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.Set;

/**
 * Created by Alvin on 26/11/2017.
 */

public class ShoppingListLocationUpdater {

    @SuppressLint("StaticFieldLeak")
    private static ShoppingListLocationUpdater sInstance;

    public static ShoppingListLocationUpdater getsInstance(Context context) {
        if (sInstance == null) {
            sInstance = new ShoppingListLocationUpdater(context);
        }
        return sInstance;
    }


    private final Context mContext;

    private ShoppingListLocationUpdater(Context context) {
        this.mContext = context.getApplicationContext();
    }


    private Set<Long> mShoppingListsToUpdate;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Task<Location> mRunningLocationTask;
    public void updateShoppingListCenterCoordinate(Activity activity, long shoppingListId) {
        if (this.mShoppingListsToUpdate == null) {
            this.mShoppingListsToUpdate = new ArraySet<>();
        }
        this.mShoppingListsToUpdate.add(shoppingListId);
        if (this.mRunningLocationTask == null || this.mRunningLocationTask.isComplete()) {
            if (this.mFusedLocationProviderClient == null) {
                this.mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.mContext);
            }
            if (ContextCompat.checkSelfPermission(this.mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                this.mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        final ShoppingListLocationUpdater updater = ShoppingListLocationUpdater.this;
                        if (location == null) {
                            Toast.makeText(updater.mContext, "Cannot find location!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String text = "Longtitude: " + location.getLongitude() + ", Latitude: " + location.getLatitude();
                        Toast.makeText(updater.mContext, text, Toast.LENGTH_SHORT).show();
//                    if (updater.mShoppingListsToUpdate != null) {
//                        for (Long id : updater.mShoppingListsToUpdate) {
//
//                        }
//                    }
                    }
                });
            } else {
                ActivityCompat.requestPermissions(activity, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

}
