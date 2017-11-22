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

    private Map<Long, LiveData<List<Price>>> mProductPriceCache;
    public LiveData<List<Price>> findProductPrices(long productId) {
        if (this.mProductPriceCache == null) {
            this.mProductPriceCache = new ArrayMap<>();
        }
        if (!this.mProductPriceCache.containsKey(productId)) {
            this.mProductPriceCache.put(productId, this.getPriceDao().findProductPrices(productId));
        }
        return this.mProductPriceCache.get(productId);
    }

    private Map<Pair<Long, Long>, LiveData<List<Price>>> mShoppingListProductBestPriceCache;
    public LiveData<List<Price>> findShoppingListProductBestPrices(long shoppingListId, long productId) {
        if (this.mShoppingListProductBestPriceCache == null) {
            this.mShoppingListProductBestPriceCache = new ArrayMap<>();
        }
        Pair<Long, Long> key = new Pair<>(shoppingListId, productId);
        if (!this.mShoppingListProductBestPriceCache.containsKey(key)) {
            this.mShoppingListProductBestPriceCache
                    .put(key, this.getPriceDao()
                            .findShoppingListProductBestPrice(shoppingListId, productId));
        }
        return this.mShoppingListProductBestPriceCache.get(key);
    }


    public LiveData<List<Price>> computeProductBestPrices(long productId, List<Long> storeIds, int quantity) {
        // TODO cache or not?
        return this.getPriceDao().computeProductBestPrices(productId, storeIds, quantity);
    }


    /*
    ************************************************************************************************
    * Query, Sync
    ************************************************************************************************
     */

    public List<Price> findProductPricesNow(long productId) {
        return this.getPriceDao().findProductPricesNow(productId);
    }

    public List<Price> findShoppingListProductBestPricesNow(long shoppingListId, long productId) {
        return this.getPriceDao()
                .findShoppingListProductBestPriceNow(shoppingListId, productId);
    }

    public List<Price> findProductBestPricesNow(long productId, List<Long> storeIds, int quantity) {
        // TODO cache or not?
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
