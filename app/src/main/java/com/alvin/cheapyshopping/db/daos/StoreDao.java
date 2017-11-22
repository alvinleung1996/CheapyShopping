package com.alvin.cheapyshopping.db.daos;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.alvin.cheapyshopping.db.entities.Store;

import java.util.List;

/**
 * Created by Alvin on 19/11/2017.
 */

@Dao
public interface StoreDao {

    /*
    ************************************************************************************************
    * Query, Async
    ************************************************************************************************
     */

    @Query("SELECT * FROM Store")
    LiveData<List<Store>> getAllStores();


    @Query("SELECT * FROM Store WHERE store_id = :id")
    LiveData<Store> findStoreByStoreId(long id);


    /*
    ************************************************************************************************
    * Query, Sync
    ************************************************************************************************
     */

    @Query("SELECT * FROM Store")
    List<Store> getAllStoresNow();

    @Query("SELECT * FROM Store WHERE store_id = :id")
    Store findStoreByStoreIdNow(long id);


    /*
    ************************************************************************************************
    * Other
    ************************************************************************************************
     */

    @Insert
    long[] insertStore(Store... stores);

    @Update
    int updateStore(Store... stores);

    @Delete
    int deleteStore(Store... stores);

}
