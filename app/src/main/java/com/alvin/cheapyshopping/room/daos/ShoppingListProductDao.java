package com.alvin.cheapyshopping.room.daos;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.alvin.cheapyshopping.room.entities.Price;
import com.alvin.cheapyshopping.room.entities.Product;
import com.alvin.cheapyshopping.room.entities.ShoppingListProduct;

import java.util.List;

/**
 * Created by Alvin on 20/11/2017.
 */

@Dao
public abstract class ShoppingListProductDao {

    public static class ShoppingListProductDetail extends Product {

        @ColumnInfo(name = "foreign_shopping_list_id")
        private long mForeignShoppingListId;

        @ColumnInfo(name = "quantity")
        private int mQuantity;

        @Ignore /* @Relation is not applicable */
        private List<Price> mBestPrices;


        public long getForeignShoppingListId() {
            return this.mForeignShoppingListId;
        }

        public void setForeignShoppingListId(long foreignShoppingListId) {
            this.mForeignShoppingListId = foreignShoppingListId;
        }

        public int getQuantity() {
            return this.mQuantity;
        }

        public void setQuantity(int quantity) {
            this.mQuantity = quantity;
        }

        public List<Price> getBestPrices() {
            return this.mBestPrices;
        }

        public void setBestPrices(List<Price> bestPrices) {
            this.mBestPrices = bestPrices;
        }
    }


    /* Use by sample data */
    @Query("SELECT * FROM ShoppingListProduct")
    public abstract List<ShoppingListProduct> getAllNow();


//    @Query("SELECT * FROM ShoppingListProduct"
//           + " WHERE foreign_shopping_list_id = :shoppingListId")
//    public abstract LiveData<List<ShoppingListProduct>> findByShoppingListId(long shoppingListId);

    @Query("SELECT Product.*, ShoppingListProduct.foreign_shopping_list_id, ShoppingListProduct.quantity"
            + " FROM ShoppingListProduct, Product"
            + " WHERE ShoppingListProduct.foreign_shopping_list_id = :shoppingListId"
                + " AND ShoppingListProduct.foreign_product_id = Product.product_id")
    public abstract LiveData<List<ShoppingListProductDetail>> findDetailsByShoppingListId(long shoppingListId);



    @Insert
    public abstract long[] insert(ShoppingListProduct... relations);

    @Update
    public abstract int update(ShoppingListProduct... relations);

    @Delete
    public abstract int delete(ShoppingListProduct... relations);

}
