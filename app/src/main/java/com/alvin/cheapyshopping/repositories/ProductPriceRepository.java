package com.alvin.cheapyshopping.repositories;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.util.ArrayMap;

import com.alvin.cheapyshopping.db.AppDatabase;
import com.alvin.cheapyshopping.db.daos.ProductPriceDao;
import com.alvin.cheapyshopping.db.entities.pseudo.ProductPrice;

import java.util.List;
import java.util.Map;

/**
 * Created by cheng on 11/26/2017.
 */

public class ProductPriceRepository {

    @SuppressLint("StaticFieldLeak")
    private static ProductPriceRepository sInstance;

    public static ProductPriceRepository getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new ProductPriceRepository(context);
        }
        return sInstance;
    }


    private final Context mContext;

    private ProductPriceRepository(Context context) {
        this.mContext = context.getApplicationContext();
    }


    /*
    ************************************************************************************************
    * Dao
    ************************************************************************************************
     */

    private ProductPriceDao mProductPriceDao;
    private ProductPriceDao getProductPriceDao() {
        if (this.mProductPriceDao == null) {
            this.mProductPriceDao = AppDatabase.getInstance(this.mContext).getProductPriceDao();
        }
        return this.mProductPriceDao;
    }


    /*
    ************************************************************************************************
    * Query, Async
    ************************************************************************************************
     */

    private Map<String, LiveData<List<ProductPrice>>> mStoreProductPricesCache;
    public LiveData<List<ProductPrice>> findProductStorePrices(String storeId) {
        if (this.mStoreProductPricesCache == null) {
            this.mStoreProductPricesCache = new ArrayMap<>();
        }
        if (!this.mStoreProductPricesCache.containsKey(storeId)) {
            this.mStoreProductPricesCache.put(storeId, this.getProductPriceDao()
                    .findStoreProductPrices(storeId));
        }
        return this.mStoreProductPricesCache.get(storeId);
    }


    /*
    ************************************************************************************************
    * Query, Sync
    ************************************************************************************************
     */

    public List<ProductPrice> findStoreProductPricesNow(String storeId) {
        return this.getProductPriceDao().findProductStorePricesNow(storeId);
    }

}
