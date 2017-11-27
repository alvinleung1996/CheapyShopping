package com.alvin.cheapyshopping.db.daos;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.alvin.cheapyshopping.db.entities.pseudo.ShoppingListProduct;

import java.util.List;

/**
 * Created by Alvin on 22/11/2017.
 */

@Dao
public interface ShoppingListProductDao {

    /*
    ************************************************************************************************
    * Query, Async
    ************************************************************************************************
     */



    @Query("SELECT R.foreign_shopping_list_id, R.quantity, P.*"
            + "FROM ShoppingListProductRelation R INNER JOIN Product P ON R.foreign_product_id = P.product_id"
            + " WHERE R.foreign_shopping_list_id = :shoppingListId")
    LiveData<List<ShoppingListProduct>> findPartialShoppingListProducts(String shoppingListId);


    /*
    ************************************************************************************************
    * Query, Sync
    ************************************************************************************************
     */

    @Query("SELECT R.foreign_shopping_list_id, R.quantity, P.*"
            + "FROM ShoppingListProductRelation R INNER JOIN Product P ON R.foreign_product_id = P.product_id"
            + " WHERE R.foreign_shopping_list_id = :shoppingListId")
    List<ShoppingListProduct> findPartialShoppingListProductsNow(String shoppingListId);



}
