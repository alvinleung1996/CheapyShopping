package com.alvin.cheapyshopping.db.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Alvin on 20/11/2017.
 */

@Entity
public class ShoppingList {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "shopping_list_id")
    private long mShoppingListId;

    @ColumnInfo(name = "name")
    private String mName;

    @ColumnInfo(name = "time")
    private Calendar mCreationTime;

    @ColumnInfo(name = "foreign_account_id")
    private long mForeignAccountId;

    @ColumnInfo(name = "center_longitude")
    private Double mCenterLongitude;

    @ColumnInfo(name = "center_latitude")
    private Double mCenterLatitude;


    public long getShoppingListId() {
        return this.mShoppingListId;
    }

    public void setShoppingListId(long shoppingListId) {
        this.mShoppingListId = shoppingListId;
    }

    public String getName() {
        return this.mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public Calendar getCreationTime() {
        return this.mCreationTime;
    }

    public void setCreationTime(Calendar creationTime) {
        this.mCreationTime = creationTime;
    }

    public long getForeignAccountId() {
        return this.mForeignAccountId;
    }

    public void setForeignAccountId(long foreignAccountId) {
        this.mForeignAccountId = foreignAccountId;
    }

    public Double getCenterLongitude() {
        return this.mCenterLongitude;
    }

    public void setCenterLongitude(Double centerLongitude) {
        this.mCenterLongitude = centerLongitude;
    }

    public Double getCenterLatitude() {
        return this.mCenterLatitude;
    }

    public void setCenterLatitude(Double centerLatitude) {
        this.mCenterLatitude = centerLatitude;
    }

}
