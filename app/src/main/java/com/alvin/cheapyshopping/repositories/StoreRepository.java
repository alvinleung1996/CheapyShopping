package com.alvin.cheapyshopping.repositories;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.content.Context;
import android.util.ArrayMap;

import com.alvin.cheapyshopping.db.AppDatabase;
import com.alvin.cheapyshopping.db.daos.StoreDao;
import com.alvin.cheapyshopping.db.entities.Store;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by Alvin on 19/11/2017.
 */

public class StoreRepository {

    @SuppressLint("StaticFieldLeak")
    private static StoreRepository sInstance;

    public static StoreRepository getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new StoreRepository(context);
        }
        return sInstance;
    }

    private Context mContext;

    private StoreRepository(Context context) {
        this.mContext = context.getApplicationContext();
    }

    /*
    ************************************************************************************************
    * Dao
    ************************************************************************************************
     */

    private StoreDao mStoreDao;
    private StoreDao getStoreDao() {
        if (this.mStoreDao == null) {
            this.mStoreDao = AppDatabase.getInstance(this.mContext).getStoreDao();
        }
        return mStoreDao;
    }


    /*
    ************************************************************************************************
    * Query, Async
    ************************************************************************************************
     */

    private LiveData<List<Store>> mAllStores;
    public LiveData<List<Store>> getAllStores() {
        if (this.mAllStores == null) {
            this.mAllStores = this.getStoreDao().getAllStores();
        }
        return this.mAllStores;
    }


    private Map<Long, LiveData<Store>> mStoreCache;
    public LiveData<Store> findStoreByStoreId(long storeId) {
        if (this.mStoreCache == null) {
            this.mStoreCache = new ArrayMap<>();
        }
        if (!this.mStoreCache.containsKey(storeId)) {
            this.mStoreCache.put(storeId, this.getStoreDao().findStoreByStoreId(storeId));
        }
        return this.mStoreCache.get(storeId);
    }


    private LiveData<List<Store>> mNearbyStores;
    public LiveData<List<Store>> findNearbyStores() {
        if (this.mNearbyStores == null) {
            this.mNearbyStores = new NearbyStoresComputer();
        }
        return this.mNearbyStores;
    }

    private class NearbyStoresComputer extends MediatorLiveData<List<Store>> {

        private ExecutorService mExecutor;
        private Future<?> mExecutingJob;

        private void compute() {
            if (this.mExecutingJob != null) {
                this.mExecutingJob.cancel(true);
                this.mExecutingJob = null;
            }
            if (this.mExecutor == null) {
                this.mExecutor = Executors.newCachedThreadPool();
            }
            this.mExecutingJob = this.mExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    List<Store> stores = StoreRepository.this.findNearbyStoresNow();
                    if (!Thread.interrupted()) NearbyStoresComputer.this.postValue(stores);
                }
            });
        }

    }


    /*
    ************************************************************************************************
    * Query, Sync
    ************************************************************************************************
     */

    public List<Store> getAllStoresNow() {
        return this.getStoreDao().getAllStoresNow();
    }

    public Store findStoreByIdNow(long storeId) {
        return this.getStoreDao().findStoreByStoreIdNow(storeId);
    }

    public List<Store> findNearbyStoresNow() {
        // TODO use GPS to filter all nearby stores
        return this.getAllStoresNow();
    }


    /*
    ************************************************************************************************
    * Other
    ************************************************************************************************
     */

    public long[] insertStore(Store... stores) {
        return this.getStoreDao().insertStore(stores);
    }

    public int updateStore(Store... stores) {
        return this.getStoreDao().updateStore(stores);
    }

    public int deleteStore(Store... stores) {
        return this.getStoreDao().deleteStore(stores);
    }

}
