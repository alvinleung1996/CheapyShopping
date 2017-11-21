package com.alvin.cheapyshopping.room.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by Alvin on 20/11/2017.
 */

@Entity(
    primaryKeys = {"foreign_shopping_list_id", "foreign_product_id"},
    foreignKeys = {
        @ForeignKey(
            childColumns = "foreign_shopping_list_id",
            entity = ShoppingList.class,
            parentColumns = "shopping_list_id",
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        ),
        @ForeignKey(
            childColumns = "foreign_product_id",
            entity = Product.class,
            parentColumns = "product_id",
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        ),
        @ForeignKey(
            childColumns = "foreign_price_id",
            entity = Price.class,
            parentColumns = "price_id",
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    }
)
public class ShoppingListProduct {

    @ColumnInfo(name = "foreign_shopping_list_id", index = true)
    private long mForeignShoppingListId;

    @ColumnInfo(name = "foreign_product_id", index = true)
    private long mForeignProductId;

    @ColumnInfo(name = "quantity")
    private int mQuantity;

    @ColumnInfo(name = "foreign_price_id", index = true)
    private Long mForeignPriceId;


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

    public int getQuantity() {
        return this.mQuantity;
    }

    public void setQuantity(int quantity) {
        this.mQuantity = quantity;
    }

    public Long getForeignPriceId() {
        return this.mForeignPriceId;
    }

    public void setForeignPriceId(Long foreignPriceId) {
        this.mForeignPriceId = foreignPriceId;
    }
}
