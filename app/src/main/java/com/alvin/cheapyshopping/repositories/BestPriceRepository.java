package com.alvin.cheapyshopping.repositories;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.util.ArrayMap;
import android.util.Pair;

import com.alvin.cheapyshopping.room.AppDatabase;
import com.alvin.cheapyshopping.room.daos.BestPriceDao;
import com.alvin.cheapyshopping.room.entities.Price;

import java.util.List;
import java.util.Map;

/**
 * Created by Alvin on 21/11/2017.
 */

public class BestPriceRepository {

    private static BestPriceRepository sInstance;

    public static BestPriceRepository getInstance(Context context) {
        if (sInstance == null) {
            AppDatabase db = AppDatabase.getInstance(context);
            sInstance = new BestPriceRepository(db.getBestPriceDao());
        }
        return sInstance;
    }


    private BestPriceDao mDao;

    private Map<Pair<Long, Long>, LiveData<List<Price>>> mCache;

    private BestPriceRepository(BestPriceDao dao) {
        this.mDao = dao;
        this.mCache = new ArrayMap<>();
    }


    public LiveData<List<Price>> findBestPricesOfShoppingListProduct(long shoppingListId, long productId) {
        Pair<Long, Long> key = new Pair<>(shoppingListId, productId);
        if (!this.mCache.containsKey(key)) {
            this.mCache.put(key, this.mDao.findPriceOfShoppingListProduct(shoppingListId, productId));
        }
        return this.mCache.get(key);
    }

}
