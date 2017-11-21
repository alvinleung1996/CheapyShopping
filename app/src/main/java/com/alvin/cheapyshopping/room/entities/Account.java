package com.alvin.cheapyshopping.room.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by Alvin on 21/11/2017.
 */

@Entity(
    foreignKeys = {
        @ForeignKey(
            childColumns = "active_shopping_list_id",
            entity = ShoppingList.class,
            parentColumns = "shopping_list_id",
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.SET_NULL
        )
    }
)
public class Account {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "account_id")
    private long mAccountId;

    @ColumnInfo(name = "active_shopping_list_id", index = true)
    private Long mActiveShoppingListId;


    public long getAccountId() {
        return this.mAccountId;
    }

    public void setAccountId(long accountId) {
        this.mAccountId = accountId;
    }

    public Long getActiveShoppingListId() {
        return this.mActiveShoppingListId;
    }

    public void setActiveShoppingListId(Long activeShoppingListId) {
        this.mActiveShoppingListId = activeShoppingListId;
    }
}
