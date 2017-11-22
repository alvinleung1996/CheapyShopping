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


    @Query("SELECT DISTINCT Product.* FROM Product LEFT OUTER JOIN ShoppingListProduct"
            + "Relation ON Product.product_id = ShoppingListProductRelation.foreign_product_id"
            + " WHERE ShoppingListProductRelation.foreign_shopping_list_id != :shoppingListId")
    LiveData<List<Product>> findAllProductsNotInShoppingList(long shoppingListId);


    @Query("SELECT * FROM Product WHERE product_id = :productId")
    LiveData<Product> findProductByProductId(long productId);


    /*
    ************************************************************************************************
    * Query, Sync
    ************************************************************************************************
     */

    @Query("SELECT * FROM Product")
    List<Product> getAllProductsNow();


    @Query("SELECT DISTINCT Product.* FROM Product LEFT OUTER JOIN ShoppingListProduct"
            + "Relation ON Product.product_id = ShoppingListProductRelation.foreign_product_id"
            + " WHERE ShoppingListProductRelation.foreign_shopping_list_id != :shoppingListId")
    List<Product> findAllProductsNotInShoppingListNow(long shoppingListId);


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
