package com.alvin.cheapyshopping.room.daos;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.alvin.cheapyshopping.room.entities.ShoppingList;

import java.util.List;

/**
 * Created by Alvin on 20/11/2017.
 */

@Dao
public interface ShoppingListDao {

    @Query("SELECT * FROM ShoppingList")
    LiveData<List<ShoppingList>> getAll();

    @Query("SELECT * FROM ShoppingList")
    List<ShoppingList> getAllNow();


    @Query("SELECT * FROM ShoppingList WHERE foreign_account_id = :accountId")
    LiveData<List<ShoppingList>> getShoppingListsOfAccount(long accountId);


    @Query("SELECT * FROM ShoppingList ORDER BY creation_time DESC LIMIT 1")
    LiveData<ShoppingList> findLatest();

    @Query("SELECT * FROM ShoppingList WHERE shopping_list_id = :id")
    LiveData<ShoppingList> findById(long id);

    @Insert
    long[] insert(ShoppingList... shoppingLists);

    @Update
    int update(ShoppingList... shoppingLists);

    @Delete
    int delete(ShoppingList... shoppingLists);

}
