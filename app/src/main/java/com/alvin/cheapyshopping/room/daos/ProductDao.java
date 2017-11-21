package com.alvin.cheapyshopping.room.daos;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.alvin.cheapyshopping.room.entities.Product;

import java.util.List;

/**
 * Created by Alvin on 20/11/2017.
 */

@Dao
public interface ProductDao {

    @Query("SELECT * FROM Product")
    LiveData<List<Product>> getAll();

    @Query("SELECT * FROM Product")
    List<Product> getAllNow();


    @Query("SELECT DISTINCT Product.* FROM Product LEFT OUTER JOIN ShoppingListProduct"
            + " ON Product.product_id = ShoppingListProduct.foreign_product_id"
            + " WHERE ShoppingListProduct.foreign_shopping_list_id != :shoppingListId")
    LiveData<List<Product>> getAllNotInShoppingList(long shoppingListId);

    @Query("SELECT * FROM Product WHERE product_id = :id")
    LiveData<Product> findById(long id);

    @Insert
    long[] insert(Product... products);

    @Update
    int update(Product... products);

    @Delete
    int delete(Product... products);

}
