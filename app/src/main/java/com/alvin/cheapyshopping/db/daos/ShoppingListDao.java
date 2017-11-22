package com.alvin.cheapyshopping.db.daos;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.alvin.cheapyshopping.db.entities.ShoppingList;

import java.util.List;

/**
 * Created by Alvin on 20/11/2017.
 */

@Dao
public interface ShoppingListDao {

    /*
    ************************************************************************************************
    * Query, Async
    ************************************************************************************************
     */

    @Query("SELECT * FROM ShoppingList WHERE foreign_account_id = :accountId")
    LiveData<List<ShoppingList>> findAccountShoppingLists(long accountId);


    /*
    ************************************************************************************************
    * Query, Sync
    ************************************************************************************************
     */

    @Query("SELECT * FROM ShoppingList WHERE foreign_account_id = :accountId")
    List<ShoppingList> findAccountShoppingListsNow(long accountId);


    /*
    ************************************************************************************************
    * Other
    ************************************************************************************************
     */

    @Insert
    long[] insertShoppingList(ShoppingList... shoppingLists);

    @Update
    int updateShoppingList(ShoppingList... shoppingLists);

    @Delete
    int deleteShoppingList(ShoppingList... shoppingLists);

}
