package com.alvin.cheapyshopping.db.daos;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.alvin.cheapyshopping.db.entities.Price;

import java.util.List;

/**
 * Created by Alvin on 20/11/2017.
 */

@Dao
public interface PriceDao {

    String SQL_CALC_PRICE_OF_QUANTITY =
            " CASE type"
                + " WHEN " + Price.TYPE_SINGLE + " THEN"
                    + " total * :quantity"
                + " WHEN " + Price.TYPE_MULTIPLE + " THEN"
                    + " CASE"
                        + " WHEN (:quantity % quantity) = 0 THEN"
                            + " total * (:quantity / quantity)"
                    + " END"
                + " WHEN " + Price.TYPE_DISCOUNT_FOR_X + " THEN"
                    + " CASE"
                        + " WHEN (:quantity % quantity) = 0 THEN"
                            + " total * (1 - discount) * (:quantity / quantity)"
                    + " END"
                + " WHEN " + Price.TYPE_BUY_X_GET_Y_FREE + " THEN"
                    + " CASE"
                        + " WHEN (:quantity % (quantity + free_quantity)) = 0 THEN"
                            + " total * (:quantity / (quantity + free_quantity))"
                    + " END"
            + " END";

    /*
    ************************************************************************************************
    * Query, Async
    ************************************************************************************************
     */

    @Query("SELECT * FROM Price WHERE foreign_product_id = :productId")
    LiveData<List<Price>> findProductPrices(String productId);


    @Query("SELECT Price.*"
            + " FROM BestPriceRelation, Price"
            + " WHERE BestPriceRelation.foreign_shopping_list_id = :shoppingListId"
                + " AND BestPriceRelation.foreign_product_id = :productId"
                + " AND BestPriceRelation.foreign_price_id = Price.price_id")
    LiveData<List<Price>> findShoppingListProductBestPrice(String shoppingListId, String productId);


    @Query("SELECT * FROM PRICE"
            + " WHERE (" + SQL_CALC_PRICE_OF_QUANTITY + ") = ("
                +"SELECT MIN(price) AS best_price FROM ("
                    + " SELECT" + SQL_CALC_PRICE_OF_QUANTITY + " AS price FROM Price"
                    + " WHERE foreign_product_id = :productId AND foreign_store_id IN (:storeIds)"
                + ") WHERE price IS NOT NULL"
            + ") AND foreign_product_id = :productId AND foreign_store_id IN (:storeIds)")
    LiveData<List<Price>> computeProductBestPrices(String productId, List<String> storeIds, int quantity);


    /*
    ************************************************************************************************
    * Query, Sync
    ************************************************************************************************
     */

    @Query("SELECT * FROM Price WHERE foreign_product_id = :productId")
    List<Price> findProductPricesNow(String productId);


    @Query("SELECT Price.*"
            + " FROM BestPriceRelation, Price"
            + " WHERE BestPriceRelation.foreign_shopping_list_id = :shoppingListId"
                + " AND BestPriceRelation.foreign_product_id = :productId"
                + " AND BestPriceRelation.foreign_price_id = Price.price_id")
    List<Price> findShoppingListProductBestPriceNow(String shoppingListId, String productId);


    // TODO where clause can add time constraint, e.g. within 30 days
    @Query("SELECT * FROM PRICE"
            + " WHERE (" + SQL_CALC_PRICE_OF_QUANTITY + ") = ("
                +"SELECT MIN(price) AS best_price FROM ("
                    + " SELECT" + SQL_CALC_PRICE_OF_QUANTITY + " AS price FROM Price"
                    + " WHERE foreign_product_id = :productId AND foreign_store_id IN (:storeIds)"
                + ") WHERE price IS NOT NULL"
            + ") AND foreign_product_id = :productId AND foreign_store_id IN (:storeIds)")
    List<Price> computeProductBestPricesNow(String productId, List<String> storeIds, int quantity);


    /*
    ************************************************************************************************
    * Other
    ************************************************************************************************
     */

    @Insert
    long[] insertPrice(Price... prices);

    @Update
    int updatePrice(Price... prices);

    @Delete
    int deletePrice(Price... prices);

}
