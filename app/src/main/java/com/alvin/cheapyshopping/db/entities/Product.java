package com.alvin.cheapyshopping.db.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by Alvin on 19/11/2017.
 */

@Entity
public class Product {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "product_id")
    private String mProductId;

    @ColumnInfo(name = "name")
    private String mName;

    @ColumnInfo(name = "description")
    private String mDescription;

    @ColumnInfo(name = "image_exist")
    private boolean mImageExist = false;

    public String getProductId() {
        return this.mProductId;
    }

    public void setProductId(String productId) {
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

    public boolean isImageExist(){
        return this.mImageExist;
    }

    public void setImageExist(boolean imageExist) {
        mImageExist = imageExist;
    }
}
