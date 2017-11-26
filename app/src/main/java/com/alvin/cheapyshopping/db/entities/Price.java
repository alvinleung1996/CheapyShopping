package com.alvin.cheapyshopping.db.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.IntDef;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Date;

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

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "price_id")
    private long mPriceId;

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
    private Date mCreationTime;

    @ColumnInfo(name = "foreign_product_id", index = true)
    private long mForeignProductId;

    @ColumnInfo(name = "foreign_store_id", index = true)
    private long mForeignStoreId;


    public long getPriceId() {
        return this.mPriceId;
    }

    public void setPriceId(long priceId) {
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

    public Date getCreationTime() {
        return this.mCreationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.mCreationTime = creationTime;
    }

    public long getForeignProductId() {
        return this.mForeignProductId;
    }

    public void setForeignProductId(long foreignProductId) {
        this.mForeignProductId = foreignProductId;
    }

    public long getForeignStoreId() {
        return this.mForeignStoreId;
    }

    public void setForeignStoreId(long foreignStoreId) {
        this.mForeignStoreId = foreignStoreId;
    }

}