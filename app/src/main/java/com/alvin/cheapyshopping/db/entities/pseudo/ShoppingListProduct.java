package com.alvin.cheapyshopping.db.entities.pseudo;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Ignore;

import com.alvin.cheapyshopping.db.entities.Product;

import java.util.List;

/**
 * Created by Alvin on 22/11/2017.
 */

/**
 * This is not a real table in the database
 * i.e. why it is in the pseudo package.
 *
 * The data contained by this class is actually a composition of
 * multiple table,
 * and the query make use of other daos
 */
public class ShoppingListProduct extends Product {

    @ColumnInfo(name = "foreign_shopping_list_id")
    private String mForeignShoppingListId;

    @ColumnInfo(name = "quantity")
    private int mQuantity;

    @Ignore
    private List<StorePrice> mBestStorePrices;


    public String getForeignShoppingListId() {
        return this.mForeignShoppingListId;
    }

    public void setForeignShoppingListId(String foreignShoppingListId) {
        this.mForeignShoppingListId = foreignShoppingListId;
    }

    public int getQuantity() {
        return this.mQuantity;
    }

    public void setQuantity(int quantity) {
        this.mQuantity = quantity;
    }

    public List<StorePrice> getBestStorePrices() {
        return this.mBestStorePrices;
    }

    public void setBestStorePrices(List<StorePrice> bestStorePrices) {
        this.mBestStorePrices = bestStorePrices;
    }


    public Double getBestPriceValue() {
        Double value = null;
        if (this.mBestStorePrices != null && this.mBestStorePrices.size() > 0) {
            value = this.mBestStorePrices.get(0).getPriceValue(this.mQuantity);
        }
        return value;
    }
}
