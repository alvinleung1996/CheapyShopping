package com.alvin.cheapyshopping.repositories;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.content.Context;
import android.util.ArrayMap;

import com.alvin.cheapyshopping.room.AppDatabase;
import com.alvin.cheapyshopping.room.daos.StoreDao;
import com.alvin.cheapyshopping.room.entities.Store;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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


    private final StoreDao mDao;

    private LiveData<List<Store>> mAll;
    private Map<Long, LiveData<Store>> mCache;

    private LiveData<List<Store>> mNearbyStores;

    private ExecutorService mExecutor;
    private Future<?> mExecutingJob;


    private StoreRepository(StoreDao dao) {
        this.mDao = dao;
        this.mCache = new ArrayMap<>();
    }


    public LiveData<List<Store>> getAll() {
        if (this.mAll == null) {
            this.mAll = this.mDao.getAll();
        }
        return this.mAll;
    }

    public LiveData<Store> findById(long id) {
        if (!this.mCache.containsKey(id)) {
            this.mCache.put(id, this.mDao.findById(id));
        }
        return this.mCache.get(id);
    }

    public Store findByIdNow(long id) {
        return this.mDao.findByIdNow(id);
    }

    public LiveData<List<Store>> findNearbyStores() {
        if (this.mNearbyStores == null) {
            this.mNearbyStores = Transformations.switchMap(this.getAll(), new Function<List<Store>, LiveData<List<Store>>>() {
                @Override
                public LiveData<List<Store>> apply(List<Store> input) {
                    MutableLiveData<List<Store>> result = new MutableLiveData<>();
                    StoreRepository.this.computeNearbyStore(input, result);
                    return result;
                }
            });
        }
        return this.mNearbyStores;
    }

    public long[] insert(Store... stores) {
        return this.mDao.insert(stores);
    }

    public int update(Store... stores) {
        return this.mDao.update(stores);
    }

    public int delete(Store... stores) {
        return this.mDao.delete(stores);
    }



    private ExecutorService getExecutor() {
        if (this.mExecutor == null) {
            this.mExecutor = Executors.newSingleThreadExecutor();
        }
        return mExecutor;
    }

    private void computeNearbyStore(final List<Store> stores, final MutableLiveData<List<Store>> result) {
        if (this.mExecutingJob != null) {
            this.mExecutingJob.cancel(true);
            this.mExecutingJob = null;
        }

        if (stores == null) {
            result.setValue(null);
        } else {
            this.mExecutingJob = this.mExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    // TODO use GPS to filter all nearby stores
                    if (!Thread.interrupted()) result.postValue(stores);
                }
            });
        }
    }

}
