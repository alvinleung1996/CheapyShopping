package com.alvin.cheapyshopping.db.daos;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.alvin.cheapyshopping.db.entities.pseudo.ProductPrice;
import com.alvin.cheapyshopping.db.entities.pseudo.StorePrice;

import java.util.List;

/**
 * Created by cheng on 11/26/2017.
 */

@Dao
public interface ProductPriceDao {

    /*
    ************************************************************************************************
    * Query, Async
    ************************************************************************************************
     */

    @Query("SELECT * FROM Price INNER JOIN Product ON Price.foreign_product_id = Product.product_id WHERE Price.foreign_store_id = :storeId")
    LiveData<List<ProductPrice>> findStoreProductPrices(String storeId);


    /*
    ************************************************************************************************
    * Query, Sync
    ************************************************************************************************
     */

    @Query("SELECT * FROM Price INNER JOIN Product ON Price.foreign_product_id = Product.product_id WHERE Price.foreign_store_id = :storeId")
    List<ProductPrice> findProductStorePricesNow(String storeId);


}

