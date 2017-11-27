package com.alvin.cheapyshopping.repositories;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.util.ArrayMap;
import android.util.Pair;

import com.alvin.cheapyshopping.db.AppDatabase;
import com.alvin.cheapyshopping.db.daos.PriceDao;
import com.alvin.cheapyshopping.db.entities.Price;

import java.util.List;
import java.util.Map;

/**
 * Created by Alvin on 20/11/2017.
 */

public class PriceRepository {

    @SuppressLint("StaticFieldLeak")
    private static PriceRepository sInstance;

    public static PriceRepository getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new PriceRepository(context);
        }
        return sInstance;
    }


    private final Context mContext;

    private PriceRepository(Context context) {
        this.mContext = context.getApplicationContext();
    }


    /*
    ************************************************************************************************
    * Dao
    ************************************************************************************************
     */

    private PriceDao mPriceDao;
    private PriceDao getPriceDao() {
        if (this.mPriceDao == null) {
            this.mPriceDao = AppDatabase.getInstance(this.mContext).getPriceDao();
        }
        return this.mPriceDao;
    }


    /*
    ************************************************************************************************
    * Query, Async
    ************************************************************************************************
     */

    private Map<String, LiveData<List<Price>>> mProductPriceCache;
    public LiveData<List<Price>> findProductPrices(String productId) {
        if (this.mProductPriceCache == null) {
            this.mProductPriceCache = new ArrayMap<>();
        }
        if (!this.mProductPriceCache.containsKey(productId)) {
            this.mProductPriceCache.put(productId, this.getPriceDao().findProductPrices(productId));
        }
        return this.mProductPriceCache.get(productId);
    }


    /*
    ************************************************************************************************
    * Query, Sync
    ************************************************************************************************
     */

    public List<Price> findProductPricesNow(String productId) {
        return this.getPriceDao().findProductPricesNow(productId);
    }


    public List<Price> computeProductBestPriceNow(String productId, List<String> storeIds, int quantity) {
        return this.getPriceDao().computeProductBestPricesNow(productId, storeIds, quantity);
    }


    /*
    ************************************************************************************************
    * Other
    ************************************************************************************************
     */

    public long[] insertPrice(Price... prices) {
        return this.getPriceDao().insertPrice(prices);
    }

    public int updatePrice(Price... prices) {
        return this.getPriceDao().updatePrice(prices);
    }

    public int deletePrice(Price... prices) {
        return this.getPriceDao().deletePrice(prices);
    }

}
