package com.alvin.cheapyshopping.room.daos;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.alvin.cheapyshopping.room.entities.Store;

import java.util.List;

/**
 * Created by Alvin on 19/11/2017.
 */

@Dao
public interface StoreDao {

    @Query("SELECT * FROM Store")
    LiveData<List<Store>> getAll();

    @Query("SELECT * FROM Store")
    List<Store> getAllNow();

    @Query("SELECT * FROM Store WHERE store_id = :id")
    LiveData<Store> findById(long id);

    @Query("SELECT * FROM Store WHERE store_id = :id")
    Store findByIdNow(long id);

    @Insert
    long[] insert(Store... stores);

    @Update
    int update(Store... stores);

    @Delete
    int delete(Store... stores);

}
