package com.alvin.cheapyshopping.db.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;

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
    private long mForeignShoppingListId;

    @ColumnInfo(name = "foreign_product_id")
    private long mForeignProductId;

    @ColumnInfo(name = "foreign_price_id", index = true)
    private long mForeignPriceId;


    public long getForeignShoppingListId() {
        return this.mForeignShoppingListId;
    }

    public void setForeignShoppingListId(long foreignShoppingListId) {
        this.mForeignShoppingListId = foreignShoppingListId;
    }

    public long getForeignProductId() {
        return this.mForeignProductId;
    }

    public void setForeignProductId(long foreignProductId) {
        this.mForeignProductId = foreignProductId;
    }

    public long getForeignPriceId() {
        return this.mForeignPriceId;
    }

    public void setForeignPriceId(long foreignPriceId) {
        this.mForeignPriceId = foreignPriceId;
    }
}
