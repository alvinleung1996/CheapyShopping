package com.alvin.cheapyshopping.db.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Calendar;

/**
 * Created by Alvin on 20/11/2017.
 */

@Entity
public class ShoppingList {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "shopping_list_id")
    private String mShoppingListId;

    @ColumnInfo(name = "name")
    private String mName;

    @ColumnInfo(name = "time")
    private Calendar mCreationTime;

    @ColumnInfo(name = "foreign_account_id")
    @NonNull
    private String mForeignAccountId;

    @ColumnInfo(name = "center_place_id")
    private String mCenterPlaceId;

    @ColumnInfo(name = "center_longitude")
    private Double mCenterLongitude;

    @ColumnInfo(name = "center_latitude")
    private Double mCenterLatitude;

    @ColumnInfo(name = "center_longitude_range")
    private Double mCenterLongitudeRange;

    @ColumnInfo(name = "center_latitude_range")
    private Double mCenterLatitudeRange;


    public ShoppingList() {}

    public ShoppingList(ShoppingList shoppingList) {
        mShoppingListId = shoppingList.mShoppingListId;
        mName = shoppingList.mName;
        mCreationTime = shoppingList.mCreationTime;
        mForeignAccountId = shoppingList.mForeignAccountId;
        mCenterPlaceId = shoppingList.mCenterPlaceId;
        mCenterLongitude = shoppingList.mCenterLongitude;
        mCenterLatitude = shoppingList.mCenterLatitude;
        mCenterLongitudeRange = shoppingList.mCenterLongitudeRange;
        mCenterLatitudeRange = shoppingList.mCenterLatitudeRange;
    }


    public String getShoppingListId() {
        return this.mShoppingListId;
    }

    public void setShoppingListId(String shoppingListId) {
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

    public String getForeignAccountId() {
        return this.mForeignAccountId;
    }

    public void setForeignAccountId(String foreignAccountId) {
        this.mForeignAccountId = foreignAccountId;
    }

    public String getCenterPlaceId() {
        return this.mCenterPlaceId;
    }

    public void setCenterPlaceId(String centerPlaceId) {
        this.mCenterPlaceId = centerPlaceId;
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

    public Double getCenterLongitudeRange() {
        return mCenterLongitudeRange;
    }

    public void setCenterLongitudeRange(Double centerLongitudeRange) {
        mCenterLongitudeRange = centerLongitudeRange;
    }

    public Double getCenterLatitudeRange() {
        return mCenterLatitudeRange;
    }

    public void setCenterLatitudeRange(Double centerLatitudeRange) {
        mCenterLatitudeRange = centerLatitudeRange;
    }
}
