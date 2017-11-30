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
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.alvin.cheapyshopping.db.entities.ShoppingList;
import com.alvin.cheapyshopping.db.entities.ShoppingListProductRelation;
import com.alvin.cheapyshopping.repositories.ShoppingListProductRelationRepository;
import com.alvin.cheapyshopping.repositories.ShoppingListRepository;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;

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


    private ShoppingListRepository mShoppingListRepository;
    private ShoppingListRepository getShoppingListRepository() {
        if (this.mShoppingListRepository == null) {
            this.mShoppingListRepository = ShoppingListRepository.getInstance(this.mContext);
        }
        return this.mShoppingListRepository;
    }


    private ShoppingListProductRelationRepository mShoppingListProductRelationRepository;
    private ShoppingListProductRelationRepository getShoppingListProductRelationRepository() {
        if (this.mShoppingListProductRelationRepository == null) {
            this.mShoppingListProductRelationRepository = ShoppingListProductRelationRepository.getInstance(this.mContext);
        }
        return this.mShoppingListProductRelationRepository;
    }





    private Set<String> mShoppingListsToUpdate;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Task<Location> mRunningLocationTask;
    public void updateShoppingListCenterCoordinate(AppCompatActivity activity, String shoppingListId) {
        if (this.mShoppingListsToUpdate == null) {
            this.mShoppingListsToUpdate = new ArraySet<>();
        }

        this.mShoppingListsToUpdate.add(shoppingListId);

        // TODO refactor below code, it is ugly

        if (this.mRunningLocationTask == null || this.mRunningLocationTask.isComplete()) {

            if (this.mFusedLocationProviderClient == null) {
                this.mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.mContext);
            }

            if (ContextCompat.checkSelfPermission(this.mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                LocationManager.getInstance(this.mContext).updateLocation(activity)
                        .observeResolve(activity, location -> {

                    final ShoppingListLocationUpdater updater = ShoppingListLocationUpdater.this;

                    String text = "Longtitude: " + location.getLongitude() + ", Latitude: " + location.getLatitude();
                    Toast.makeText(updater.mContext, text, Toast.LENGTH_SHORT).show();

                    new Thread(() -> {

                        if (updater.mShoppingListsToUpdate != null) {

                            List<ShoppingList> listToUpdate = new ArrayList<>();

                            for (String id : updater.mShoppingListsToUpdate) {
                                ShoppingList shoppingList = ShoppingListLocationUpdater.this.getShoppingListRepository()
                                        .findShoppingListNow(id);

                                shoppingList.setCenterLongitude(location.getLongitude());
                                shoppingList.setCenterLatitude(location.getLatitude());

                                listToUpdate.add(shoppingList);
                            }

                            ShoppingListLocationUpdater.this.getShoppingListRepository().updateShoppingList(
                                    listToUpdate.toArray(new ShoppingList[listToUpdate.size()]));
                        }

                    }).start();
                });

            } else {
                ActivityCompat.requestPermissions(activity, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

}
