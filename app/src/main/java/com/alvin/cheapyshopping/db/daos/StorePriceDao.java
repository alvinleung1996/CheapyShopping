package com.alvin.cheapyshopping.db.daos;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.alvin.cheapyshopping.db.entities.pseudo.StorePrice;

import java.util.List;

/**
 * Created by Alvin on 22/11/2017.
 */

@Dao
public interface StorePriceDao {

    /*
    ************************************************************************************************
    * Query, Async
    ************************************************************************************************
     */

    @Query("SELECT * FROM Price P INNER JOIN STORE S ON P.foreign_store_id = S.store_id WHERE P.foreign_product_id = :productId")
    LiveData<List<StorePrice>> findProductStorePrices(long productId);


    @Query("SELECT P.*, S.* FROM BestPriceRelation B, Price P, STORE S"
            + " WHERE B.foreign_shopping_list_id = :shoppingListId"
                + " AND B.foreign_product_id = :productId"
                + " AND B.foreign_price_id = P.price_id"
                + " AND P.foreign_store_id = S.store_id")
    LiveData<List<StorePrice>> findShoppingListProductBestStorePrices(long shoppingListId, long productId);


    // Get the best StorePrice with min unit price (QTY: 1) and latest creation time
    @Query("SELECT * " +
            "FROM Price P INNER JOIN STORE S ON P.foreign_store_id = S.store_id " +
            "WHERE P.foreign_product_id = :productId " +
            "ORDER BY P.total ASC, P.creation_time DESC " +
            "LIMIT 1")
    LiveData<StorePrice> findBestProductStorePrice(long productId);

    /*
    ************************************************************************************************
    * Query, Sync
    ************************************************************************************************
     */

    @Query("SELECT * FROM Price P INNER JOIN STORE S ON P.foreign_store_id = S.store_id WHERE P.foreign_product_id = :productId")
    List<StorePrice> findProductStorePricesNow(long productId);


    @Query("SELECT P.*, S.* FROM BestPriceRelation B, Price P, STORE S"
            + " WHERE B.foreign_shopping_list_id = :shoppingListId"
                + " AND B.foreign_product_id = :productId"
                + " AND B.foreign_price_id = P.price_id"
                + " AND P.foreign_store_id = S.store_id")
    List<StorePrice> findShoppingListProductBestStorePricesNow(long shoppingListId, long productId);

}
