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
    LiveData<List<ShoppingListProductRelation>> findShoppingListProductRelations(long shoppingListId);


    /*
    ************************************************************************************************
    * Query, Sync
    ************************************************************************************************
     */

    @Query("SELECT * FROM ShoppingListProductRelation"
            + " WHERE ShoppingListProductRelation.foreign_shopping_list_id = :shoppingListId")
    List<ShoppingListProductRelation> findShoppingListProductRelationsNow(long shoppingListId);

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
