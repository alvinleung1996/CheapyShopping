package com.alvin.cheapyshopping.repositories;

import android.arch.lifecycle.LiveData;
import android.content.Context;

import com.alvin.cheapyshopping.room.AppDatabase;
import com.alvin.cheapyshopping.room.daos.StoreDao;
import com.alvin.cheapyshopping.room.entities.Store;

import java.util.List;

/**
 * Created by Alvin on 19/11/2017.
 */

public class StoreRepository {

    private static StoreRepository sInstance;

    public static StoreRepository getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new StoreRepository(AppDatabase.getInstance(context).getStoreDao());
        }
        return sInstance;
    }


    private final StoreDao mStoreDao;

    private LiveData<List<Store>> mStores;



    private StoreRepository(StoreDao storeDao) {
        this.mStoreDao = storeDao;
    }

    public LiveData<List<Store>> getStores() {
        if (this.mStores == null) {
            this.mStores = this.mStoreDao.getAll();
        }
        return this.mStores;
    }

    public long[] insertAll(Store... stores) {
        return this.mStoreDao.insertAll(stores);
    }

}
