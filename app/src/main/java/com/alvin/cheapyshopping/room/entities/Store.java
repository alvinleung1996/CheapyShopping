package com.alvin.cheapyshopping.room.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by Alvin on 19/11/2017.
 */

@Entity
public class Store {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "store_id")
    private long mStoreId;

    @ColumnInfo(name = "name")
    private String mName;

    @ColumnInfo(name = "location")
    private String mLocation;


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

    public String getLocation() {
        return this.mLocation;
    }

    public void setLocation(String location) {
        this.mLocation = location;
    }
}
