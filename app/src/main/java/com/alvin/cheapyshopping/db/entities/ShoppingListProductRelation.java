package com.alvin.cheapyshopping.db.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;

/**
 * Created by Alvin on 20/11/2017.
 */

@Entity(
    primaryKeys = {
        "foreign_shopping_list_id",
        "foreign_product_id"
    },
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
    }
)
public class ShoppingListProductRelation {

    @ColumnInfo(name = "foreign_shopping_list_id", index = true)
    private long mForeignShoppingListId;

    @ColumnInfo(name = "foreign_product_id", index = true)
    private long mForeignProductId;

    @ColumnInfo(name = "quantity")
    private int mQuantity;


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
}
