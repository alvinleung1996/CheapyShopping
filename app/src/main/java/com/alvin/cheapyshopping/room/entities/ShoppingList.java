package com.alvin.cheapyshopping.room.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

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

    @ColumnInfo(name = "creation_time")
    private Date mCreationTime;


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

    public Date getCreationTime() {
        return this.mCreationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.mCreationTime = creationTime;
    }
}
