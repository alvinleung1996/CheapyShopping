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

    @Query("SELECT * FROM ShoppingList WHERE shopping_list_id = :shoppingListId")
    LiveData<ShoppingList> findShoppingList(String shoppingListId);

    @Query("SELECT * FROM ShoppingList WHERE foreign_account_id = :accountId")
    LiveData<List<ShoppingList>> findAccountShoppingLists(String accountId);

//    @Query("SELECT * FROM Product P WHERE ("
//            + "SELECT COUNT(*) FROM Product P0 INNER JOIN ShoppingListProductRelation R"
//            + " ON P0.product_id = R.foreign_product_id"
//            + " WHERE P.product_id = P0.product_id AND R.foreign_shopping_list_id = :shoppingListId"
//            + ") = 0")
//    LiveData<List<Product>> findProductsNotInShoppingList(String shoppingListId);


    @Query("SELECT * FROM ShoppingList S WHERE (" +
            "SELECT COUNT(*) FROM ShoppingListProductRelation R " +
            "WHERE R.foreign_shopping_list_id = S.shopping_list_id " +
            "AND R.foreign_product_id = :productId) = 0")

    LiveData<List<ShoppingList>> findShoppingListsNotContainProduct(String productId);

    /*
    ************************************************************************************************
    * Query, Sync
    ************************************************************************************************
     */

    @Query("SELECT * FROM ShoppingList WHERE shopping_list_id = :shoppingListId")
    ShoppingList findShoppingListNow(String shoppingListId);

    @Query("SELECT * FROM ShoppingList WHERE foreign_account_id = :accountId")
    List<ShoppingList> findAccountShoppingListsNow(String accountId);

    @Query("SELECT * FROM ShoppingList S WHERE (" +
            "SELECT COUNT(*) FROM ShoppingListProductRelation R " +
            "WHERE R.foreign_shopping_list_id = S.shopping_list_id " +
            "AND R.foreign_product_id = :productId) = 0")

    List<ShoppingList> findShoppingListsNotContainProductNow(String productId);


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
