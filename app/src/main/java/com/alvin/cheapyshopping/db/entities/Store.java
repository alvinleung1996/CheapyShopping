package com.alvin.cheapyshopping.db.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by Alvin on 19/11/2017.
 */

@Entity(
    indices = {
        @Index(value = {"place_id"}, unique = true)
    }
)
public class Store {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "store_id")
    private long mStoreId;

    @ColumnInfo(name = "name")
    private String mName;

    @ColumnInfo(name = "address")
    private String mAddress;

    @ColumnInfo(name = "place_id")
    private String mPlaceId;

    @ColumnInfo(name = "longitude")
    private double mLongitude;

    @ColumnInfo(name = "latitude")
    private double mLatitude;


    public long getStoreId() {
        return this.mStoreId;
    }

    public void setStoreId(long storeId) {
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

    public String getPlaceId() {
        return this.mPlaceId;
    }

    public void setPlaceId(String placeId) {
        this.mPlaceId = placeId;
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
}
