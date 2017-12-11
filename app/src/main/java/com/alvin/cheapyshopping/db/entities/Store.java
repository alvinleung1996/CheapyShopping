package com.alvin.cheapyshopping.db.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by Alvin on 19/11/2017.
 */

@Entity
public class Store {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "store_id")
    private String mStoreId;

    @ColumnInfo(name = "name")
    private String mName;

    @ColumnInfo(name = "address")
    private String mAddress;

    @ColumnInfo(name = "longitude")
    private double mLongitude;

    @ColumnInfo(name = "latitude")
    private double mLatitude;

    @ColumnInfo(name = "image_exist")
    private boolean mImageExist = false;

    @ColumnInfo(name = "rating")
    private double mRating = 0;

    @ColumnInfo(name = "description")
    private String mDescription = "Not specified";


    public String getStoreId() {
        return this.mStoreId;
    }

    public void setStoreId(String storeId) {
        this.mStoreId = storeId;
    }

    public String getName() {
        return this.mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getAddress() {
        return this.mAddress;
    }

    public void setAddress(String address) {
        this.mAddress = address;
    }

    public double getLongitude() {
        return this.mLongitude;
    }

    public void setLongitude(double longitude) {
        this.mLongitude = longitude;
    }

    public double getLatitude() {
        return this.mLatitude;
    }

    public void setLatitude(double latitude) {
        this.mLatitude = latitude;
    }

    public boolean isImageExist() {
        return mImageExist;
    }

    public void setImageExist(boolean imageExist) {
        mImageExist = imageExist;
    }

    public double getRating() {
        return mRating;
    }

    public void setRating(double rating) {
        mRating = rating;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }
}
