package com.alvin.cheapyshopping.db.daos;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.alvin.cheapyshopping.db.entities.Product;

import java.util.List;

/**
 * Created by Alvin on 20/11/2017.
 */

@Dao
public interface ProductDao {

    /*
    ************************************************************************************************
    * Query, Async
    ************************************************************************************************
     */

    @Query("SELECT * FROM Product")
    LiveData<List<Product>> getAllProducts();


    @Query("SELECT DISTINCT P.* FROM Product P LEFT OUTER JOIN ShoppingListProductRelation R"
            + " ON P.product_id = R.foreign_product_id"
            + " WHERE R.foreign_shopping_list_id IS NULL OR R.foreign_shopping_list_id != :shoppingListId")
    LiveData<List<Product>> findProductsNotInShoppingList(long shoppingListId);


    @Query("SELECT * FROM Product WHERE product_id = :productId")
    LiveData<Product> findProductByProductId(long productId);


    /*
    ************************************************************************************************
    * Query, Sync
    ************************************************************************************************
     */

    @Query("SELECT * FROM Product")
    List<Product> getAllProductsNow();


    @Query("SELECT DISTINCT P.* FROM Product P LEFT OUTER JOIN ShoppingListProductRelation R"
            + " ON P.product_id = R.foreign_product_id"
            + " WHERE R.foreign_shopping_list_id IS NULL OR R.foreign_shopping_list_id != :shoppingListId")
    List<Product> findProductsNotInShoppingListNow(long shoppingListId);


    @Query("SELECT * FROM Product WHERE product_id = :productId")
    Product findProductByProductIdNow(long productId);


    /*
    ************************************************************************************************
    * Other
    ************************************************************************************************
     */

    @Insert
    long[] insertProduct(Product... products);

    @Update
    int updateProduct(Product... products);

    @Delete
    int deleteProduct(Product... products);

}
