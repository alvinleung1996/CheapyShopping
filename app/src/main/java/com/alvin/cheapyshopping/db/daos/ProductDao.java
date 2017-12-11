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


    @Query("SELECT * FROM Product P WHERE ("
                + "SELECT COUNT(*) FROM Product P0 INNER JOIN ShoppingListProductRelation R"
                + " ON P0.product_id = R.foreign_product_id"
                + " WHERE P.product_id = P0.product_id AND R.foreign_shopping_list_id = :shoppingListId"
            + ") = 0")
    LiveData<List<Product>> findProductsNotInShoppingList(String shoppingListId);


    @Query("SELECT P.* FROM Product P INNER JOIN ShoppingListProductRelation R"
            + " ON P.product_id = R.foreign_product_id"
            + " WHERE R.foreign_shopping_list_id = :shoppingListId")
    LiveData<List<Product>> findShoppingListProducts(String shoppingListId);


    @Query("SELECT * FROM Product WHERE product_id = :productId")
    LiveData<Product> findProductByProductId(String productId);


    /*
    ************************************************************************************************
    * Query, Sync
    ************************************************************************************************
     */

    @Query("SELECT * FROM Product")
    List<Product> getAllProductsNow();


    @Query("SELECT * FROM Product P WHERE ("
                + "SELECT COUNT(*) FROM Product P0 INNER JOIN ShoppingListProductRelation R"
                + " ON P0.product_id = R.foreign_product_id"
                + " WHERE P.product_id = P0.product_id AND R.foreign_shopping_list_id = :shoppingListId"
            + ") = 0")
    List<Product> findProductsNotInShoppingListNow(String shoppingListId);


    @Query("SELECT P.* FROM Product P INNER JOIN ShoppingListProductRelation R"
            + " ON P.product_id = R.foreign_product_id"
            + " WHERE R.foreign_shopping_list_id = :shoppingListId")
    List<Product> findShoppingListProductsNow(String shoppingListId);


    @Query("SELECT * FROM Product WHERE product_id = :productId")
    Product findProductByProductIdNow(String productId);


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
