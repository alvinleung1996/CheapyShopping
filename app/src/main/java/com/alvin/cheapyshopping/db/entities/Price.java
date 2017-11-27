package com.alvin.cheapyshopping.db.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Calendar;

/**
 * Created by Alvin on 20/11/2017.
 */

@Entity(
    foreignKeys = {
        @ForeignKey(
            childColumns = {"foreign_product_id"},
            entity = Product.class,
            parentColumns = {"product_id"},
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        ),
        @ForeignKey(
            childColumns = {"foreign_store_id"},
            entity = Store.class,
            parentColumns = {"store_id"},
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    }
)
public class Price {

    @Retention(RetentionPolicy.SOURCE)
    @Documented
    @IntDef({TYPE_SINGLE, TYPE_MULTIPLE, TYPE_DISCOUNT_FOR_X, TYPE_BUY_X_GET_Y_FREE})
    public @interface Type {}
    public static final int TYPE_SINGLE = 1;
    public static final int TYPE_MULTIPLE = 2;
    public static final int TYPE_DISCOUNT_FOR_X = 3;
    public static final int TYPE_BUY_X_GET_Y_FREE = 4;

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "price_id")
    private String mPriceId;

    @Type
    @ColumnInfo(name = "type")
    private int mType;

    @ColumnInfo(name = "total")
    private double mTotal;

    @ColumnInfo(name = "quantity")
    private int mQuantity;

    @ColumnInfo(name = "free_quantity")
    private int mFreeQuantity;

    @ColumnInfo(name = "discount")
    private double mDiscount;

    @ColumnInfo(name = "creation_time")
    private Calendar mCreationTime;

    @ColumnInfo(name = "foreign_product_id", index = true)
    private String mForeignProductId;

    @ColumnInfo(name = "foreign_store_id", index = true)
    private String mForeignStoreId;


    public String getPriceId() {
        return this.mPriceId;
    }

    public void setPriceId(String priceId) {
        this.mPriceId = priceId;
    }

    @Type
    public int getType() {
        return this.mType;
    }

    public void setType(@Type int type) {
        this.mType = type;
    }

    public double getTotal() {
        return this.mTotal;
    }

    public void setTotal(double total) {
        this.mTotal = total;
    }

    public int getQuantity() {
        return this.mQuantity;
    }

    public void setQuantity(int quantity) {
        this.mQuantity = quantity;
    }

    public int getFreeQuantity() {
        return this.mFreeQuantity;
    }

    public void setFreeQuantity(int freeQuantity) {
        this.mFreeQuantity = freeQuantity;
    }

    public double getDiscount() {
        return this.mDiscount;
    }

    public void setDiscount(double discount) {
        this.mDiscount = discount;
    }

    public Calendar getCreationTime() {
        return this.mCreationTime;
    }

    public void setCreationTime(Calendar creationTime) {
        this.mCreationTime = creationTime;
    }

    public String getForeignProductId() {
        return this.mForeignProductId;
    }

    public void setForeignProductId(String foreignProductId) {
        this.mForeignProductId = foreignProductId;
    }

    public String getForeignStoreId() {
        return this.mForeignStoreId;
    }

    public void setForeignStoreId(String foreignStoreId) {
        this.mForeignStoreId = foreignStoreId;
    }

}
