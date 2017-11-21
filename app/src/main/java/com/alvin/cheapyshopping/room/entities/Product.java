package com.alvin.cheapyshopping.room.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by Alvin on 19/11/2017.
 */

@Entity
public class Product {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "product_id")
    private long mProductId;

    @ColumnInfo(name = "name")
    private String mName;

    @ColumnInfo(name="description")
    private String mDescription;


    public long getProductId() {
        return this.mProductId;
    }

    public void setProductId(long productId) {
        this.mProductId = productId;
    }

    public String getName() {
        return this.mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getDescription() {
        return this.mDescription;
    }

    public void setDescription(String description) {
        this.mDescription = description;
    }

}
