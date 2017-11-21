package com.alvin.cheapyshopping.repositories;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.util.ArrayMap;

import com.alvin.cheapyshopping.room.AppDatabase;
import com.alvin.cheapyshopping.room.daos.PriceDao;
import com.alvin.cheapyshopping.room.entities.Price;

import java.util.List;
import java.util.Map;

/**
 * Created by Alvin on 20/11/2017.
 */

public class PriceRepository {

    private static PriceRepository sInstance;

    public static PriceRepository getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new PriceRepository(AppDatabase.getInstance(context).getPriceDao());
        }
        return sInstance;
    }


    private final PriceDao mDao;

    private final Map<Long, LiveData<List<Price>>> mCacheByProductId;
    private final Map<Long, LiveData<List<Price>>> mCacheByStoreId;


    private PriceRepository(PriceDao dao) {
        this.mDao = dao;
        this.mCacheByProductId = new ArrayMap<>();
        this.mCacheByStoreId = new ArrayMap<>();
    }



    public LiveData<List<Price>> findByProductId(long productId) {
        if (!this.mCacheByProductId.containsKey(productId)) {
            this.mCacheByProductId.put(productId, this.mDao.findByProductId(productId));
        }
        return this.mCacheByProductId.get(productId);
    }

    public LiveData<List<Price>> findByStoreId(long storeId) {
        if (!this.mCacheByStoreId.containsKey(storeId)) {
            this.mCacheByStoreId.put(storeId, this.mDao.findByStoreId(storeId));
        }
        return this.mCacheByStoreId.get(storeId);
    }

    public LiveData<List<Price>> findByProductIdAndStoreIds(long productId, List<Long> storeIds) {
        // TODO cache or not?
        return this.mDao.findByProductIdAndStoreIds(productId, storeIds);
    }

    public List<Price> findBestPriceOfProductNow(long productId, List<Long> storeIds, int quantity) {
        // TODO cache or not?
        return this.mDao.findBestPriceOfProductNow(productId, storeIds, quantity);
    }



    public long[] insert(Price... prices) {
        return this.mDao.insert(prices);
    }

    public int update(Price... prices) {
        return this.mDao.update(prices);
    }

    public int delete(Price... prices) {
        return this.mDao.delete(prices);
    }

}
