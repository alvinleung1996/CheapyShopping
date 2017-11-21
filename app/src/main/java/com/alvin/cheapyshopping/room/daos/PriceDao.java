package com.alvin.cheapyshopping.room.daos;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.alvin.cheapyshopping.room.entities.Price;

import java.util.List;

/**
 * Created by Alvin on 20/11/2017.
 */

@Dao
public interface PriceDao {

    @Query("SELECT * FROM Price")
    List<Price> getAllNow();

    @Query("SELECT * FROM Price WHERE foreign_product_id = :productId")
    LiveData<List<Price>> findByProductId(long productId);

    @Query("SELECT * FROM Price WHERE foreign_store_id = :storeId")
    LiveData<List<Price>> findByStoreId(long storeId);

    @Query("SELECT * FROM Price WHERE foreign_product_id = :productId AND foreign_store_id IN (:storeIds)")
    LiveData<List<Price>> findByProductIdAndStoreIds(long productId, List<Long> storeIds);



    String SQL_CALC_PRICE_OF_QUANTITY =
            " CASE type"
                + " WHEN " + Price.TYPE_SINGLE + " THEN"
                    + " total * :quantity"
                + " WHEN " + Price.TYPE_MULTIPLE + " THEN"
                    + " CASE quantity"
                        + " WHEN :quantity THEN"
                            + " total"
                    + " END"
                + " WHEN " + Price.TYPE_DISCOUNT_FOR_X + " THEN"
                    + " CASE quantity"
                        + " WHEN :quantity THEN"
                            + " total * quantity"
                    + " END"
                + " WHEN " + Price.TYPE_BUY_X_GET_Y_FREE + " THEN"
                    + " CASE quantity + free_quantity"
                        + " WHEN :quantity THEN"
                            + " total"
                    + " END"
            + " END";

    @Query("SELECT * FROM PRICE"
            + " WHERE (" + SQL_CALC_PRICE_OF_QUANTITY + ") = ("
                +"SELECT MIN(price) AS best_price FROM ("
                    + " SELECT" + SQL_CALC_PRICE_OF_QUANTITY + " AS price FROM Price"
                    + " WHERE foreign_product_id = :productId AND foreign_store_id IN (:storeIds)"
                + ") WHERE price IS NOT NULL"
            + ") AND foreign_product_id = :productId AND foreign_store_id IN (:storeIds)")
    List<Price> findBestPriceOfProductNow(long productId, List<Long> storeIds, int quantity);
    // TODO where clause can add time constraint, e.g. within 30 days

    @Insert
    long[] insert(Price... prices);

    @Update
    int update(Price... prices);

    @Delete
    int delete(Price... prices);

}
