package com.alvin.cheapyshopping.db.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.support.annotation.NonNull;

/**
 * Created by Alvin on 21/11/2017.
 */

@Entity(
    primaryKeys = {
        "foreign_shopping_list_id",
        "foreign_product_id",
        "foreign_price_id"
    },
    foreignKeys = {
        @ForeignKey(
            childColumns = {"foreign_shopping_list_id", "foreign_product_id"},
            entity = ShoppingListProductRelation.class,
            parentColumns = {"foreign_shopping_list_id", "foreign_product_id"},
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        ),
        @ForeignKey(
            childColumns = {"foreign_price_id"},
            entity = Price.class,
            parentColumns = {"price_id"},
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    }
)
public class BestPriceRelation {

    @ColumnInfo(name = "foreign_shopping_list_id")
    @NonNull
    private String mForeignShoppingListId;

    @ColumnInfo(name = "foreign_product_id")
    @NonNull
    private String mForeignProductId;

    @ColumnInfo(name = "foreign_price_id", index = true)
    @NonNull
    private String mForeignPriceId;


    public String getForeignShoppingListId() {
        return this.mForeignShoppingListId;
    }

    public void setForeignShoppingListId(String foreignShoppingListId) {
        this.mForeignShoppingListId = foreignShoppingListId;
    }

    public String getForeignProductId() {
        return this.mForeignProductId;
    }

    public void setForeignProductId(String foreignProductId) {
        this.mForeignProductId = foreignProductId;
    }

    public String getForeignPriceId() {
        return this.mForeignPriceId;
    }

    public void setForeignPriceId(String foreignPriceId) {
        this.mForeignPriceId = foreignPriceId;
    }
}
