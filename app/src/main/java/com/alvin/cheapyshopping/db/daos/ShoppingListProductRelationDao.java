package com.alvin.cheapyshopping.db.daos;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.alvin.cheapyshopping.db.entities.ShoppingListProductRelation;

import java.util.List;

/**
 * Created by Alvin on 20/11/2017.
 */

@Dao
public interface ShoppingListProductRelationDao {


    /*
    ************************************************************************************************
    * Query, Async
    ************************************************************************************************
     */


    @Query("SELECT * FROM ShoppingListProductRelation"
            + " WHERE ShoppingListProductRelation.foreign_shopping_list_id = :shoppingListId")
    LiveData<List<ShoppingListProductRelation>> findShoppingListProductRelations(String shoppingListId);


    @Query("SELECT * FROM ShoppingListProductRelation R"
            + " WHERE R.foreign_shopping_list_id = :shoppingListId"
            + " AND R.foreign_product_id = :productId")
    LiveData<ShoppingListProductRelation> getShoppingListProductRelation(String shoppingListId, String productId);


    /*
    ************************************************************************************************
    * Query, Sync
    ************************************************************************************************
     */

    @Query("SELECT * FROM ShoppingListProductRelation"
            + " WHERE ShoppingListProductRelation.foreign_shopping_list_id = :shoppingListId")
    List<ShoppingListProductRelation> findShoppingListProductRelationsNow(String shoppingListId);


    @Query("SELECT * FROM ShoppingListProductRelation R"
            + " WHERE R.foreign_shopping_list_id = :shoppingListId"
            + " AND R.foreign_product_id = :productId")
    ShoppingListProductRelation getShoppingListProductRelationNow(String shoppingListId, String productId);

    /*
    ************************************************************************************************
    * Other
    ************************************************************************************************
     */

    @Insert
    long[] insertShoppingListProductRelation(ShoppingListProductRelation... relations);

    @Update
    int updateShoppingListProductRelation(ShoppingListProductRelation... relations);

    @Delete
    int deleteShoppingListProductRelation(ShoppingListProductRelation... relations);

}
