package com.alvin.cheapyshopping.repositories;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.util.ArrayMap;
import android.util.Pair;

import com.alvin.cheapyshopping.db.AppDatabase;
import com.alvin.cheapyshopping.db.daos.StorePriceDao;
import com.alvin.cheapyshopping.db.entities.Price;
import com.alvin.cheapyshopping.db.entities.pseudo.StorePrice;

import java.util.List;
import java.util.Map;

/**
 * Created by Alvin on 22/11/2017.
 */

public class StorePriceRepository {

    @SuppressLint("StaticFieldLeak")
    private static StorePriceRepository sInstance;

    public static StorePriceRepository getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new StorePriceRepository(context);
        }
        return sInstance;
    }


    private final Context mContext;

    private StorePriceRepository(Context context) {
        this.mContext = context.getApplicationContext();
    }


    /*
    ************************************************************************************************
    * Dao
    ************************************************************************************************
     */

    private StorePriceDao mStorePriceDao;
    private StorePriceDao getStorePriceDao() {
        if (this.mStorePriceDao == null) {
            this.mStorePriceDao = AppDatabase.getInstance(this.mContext).getStorePriceDao();
        }
        return this.mStorePriceDao;
    }


    /*
    ************************************************************************************************
    * Query, Async
    ************************************************************************************************
     */

    private Map<Long, LiveData<List<StorePrice>>> mProductStorePricesCache;
    public LiveData<List<StorePrice>> findProductStorePrices(long productId) {
        if (this.mProductStorePricesCache == null) {
            this.mProductStorePricesCache = new ArrayMap<>();
        }
        if (!this.mProductStorePricesCache.containsKey(productId)) {
            this.mProductStorePricesCache.put(productId, this.getStorePriceDao()
                    .findProductStorePrices(productId));
        }
        return this.mProductStorePricesCache.get(productId);
    }

    private Map<Pair<Long, Long>, LiveData<List<StorePrice>>> mShoppingListProductBestStorePricesCache;
    public LiveData<List<StorePrice>> findShoppingListProductBestStorePrices(long shoppingListId, long productId) {
        if (this.mShoppingListProductBestStorePricesCache == null) {
            this.mShoppingListProductBestStorePricesCache = new ArrayMap<>();
        }
        Pair<Long, Long> key = new Pair<>(shoppingListId, productId);
        if (!this.mShoppingListProductBestStorePricesCache.containsKey(key)) {
            this.mShoppingListProductBestStorePricesCache.put(key, this.getStorePriceDao()
                    .findShoppingListProductBestStorePrices(shoppingListId, productId));
        }
        return this.mShoppingListProductBestStorePricesCache.get(key);
    }


    /*
    ************************************************************************************************
    * Query, Sync
    ************************************************************************************************
     */

    public List<StorePrice> findProductStorePricesNow(long productId) {
        return this.getStorePriceDao().findProductStorePricesNow(productId);
    }

    public List<StorePrice> findShoppingListProductBestStorePriceNow(long shoppingListId, long productId) {
        return this.getStorePriceDao().findShoppingListProductBestStorePricesNow(shoppingListId, productId);
    }

}
