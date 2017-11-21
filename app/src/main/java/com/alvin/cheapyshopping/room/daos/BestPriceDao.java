package com.alvin.cheapyshopping.room.daos;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.alvin.cheapyshopping.room.entities.Price;

import java.util.List;

/**
 * Created by Alvin on 21/11/2017.
 */

@Dao
public interface BestPriceDao {

    @Query("SELECT Price.*"
            + " FROM BestPrice, Price"
            + " WHERE BestPrice.foreign_shopping_list_product_shopping_list_id = :shoppingListId"
                + " AND BestPrice.foreign_shopping_list_product_product_id = :productId"
                + " AND BestPrice.foreign_price_id = Price.price_id")
    LiveData<List<Price>> findPriceOfShoppingListProduct(long shoppingListId, long productId);

    @Query("SELECT Price.*"
            + " FROM BestPrice, Price"
            + " WHERE BestPrice.foreign_shopping_list_product_shopping_list_id = :shoppingListId"
                + " AND BestPrice.foreign_shopping_list_product_product_id = :productId"
                + " AND BestPrice.foreign_price_id = Price.price_id")
    List<Price> findPriceOfShoppingListProductNow(long shoppingListId, long productId);

}
