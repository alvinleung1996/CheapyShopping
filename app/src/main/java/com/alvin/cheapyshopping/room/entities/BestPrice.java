package com.alvin.cheapyshopping.room.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.support.annotation.NonNull;

/**
 * Created by Alvin on 21/11/2017.
 */

@Entity(
    primaryKeys = {
        "foreign_shopping_list_product_shopping_list_id",
        "foreign_shopping_list_product_product_id",
        "foreign_price_id"
    },
    foreignKeys = {
        @ForeignKey(
            childColumns = {"foreign_shopping_list_product_shopping_list_id", "foreign_shopping_list_product_product_id"},
            entity = ShoppingListProduct.class,
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
public class BestPrice {

    @ColumnInfo(name = "foreign_shopping_list_product_shopping_list_id")
    private long mForeignShoppingListProductShoppingListId;

    @ColumnInfo(name = "foreign_shopping_list_product_product_id")
    private long mForeignShoppingListProductProductId;

    @ColumnInfo(name = "foreign_price_id", index = true)
    private long mForeignPriceId;


    public long getForeignShoppingListProductShoppingListId() {
        return this.mForeignShoppingListProductShoppingListId;
    }

    public void setForeignShoppingListProductShoppingListId(long foreignShoppingListProductShoppingListId) {
        this.mForeignShoppingListProductShoppingListId = foreignShoppingListProductShoppingListId;
    }

    public long getForeignShoppingListProductProductId() {
        return this.mForeignShoppingListProductProductId;
    }

    public void setForeignShoppingListProductProductId(long foreignShoppingListProductProductId) {
        this.mForeignShoppingListProductProductId = foreignShoppingListProductProductId;
    }

    public long getForeignPriceId() {
        return this.mForeignPriceId;
    }

    public void setForeignPriceId(long foreignPriceId) {
        this.mForeignPriceId = foreignPriceId;
    }
}
