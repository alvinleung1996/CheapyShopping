package com.alvin.cheapyshopping.room.daos;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.alvin.cheapyshopping.room.entities.Product;

import java.util.List;

/**
 * Created by Alvin on 20/11/2017.
 */

@Dao
public interface ProductDao {

    @Query("SELECT * FROM Product")
    LiveData<List<Product>> getAll();

    @Query("SELECT * FROM Product WHERE product_id = :id")
    LiveData<Product> findById(long id);

    @Insert
    long[] insertAll(Product... products);

    @Delete
    int deleteAll(Product... products);

}
