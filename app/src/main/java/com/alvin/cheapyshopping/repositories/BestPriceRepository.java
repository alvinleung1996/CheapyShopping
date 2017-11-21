package com.alvin.cheapyshopping.repositories;

import android.content.Context;

import com.alvin.cheapyshopping.room.AppDatabase;
import com.alvin.cheapyshopping.room.daos.BestPriceDao;
import com.alvin.cheapyshopping.room.entities.Price;

import java.util.List;

/**
 * Created by Alvin on 21/11/2017.
 */

public class BestPriceRepository {

    private static BestPriceRepository sInstance;

    public static BestPriceRepository getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new BestPriceRepository(context);
        }
        return sInstance;
    }


    private final Context mContext;

    private BestPriceDao mBestPriceDao;



    private BestPriceRepository(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public BestPriceDao getBestPriceDao() {
        if (this.mBestPriceDao == null) {
            this.mBestPriceDao = AppDatabase.getInstance(this.mContext).getBestPriceDao();
        }
        return this.mBestPriceDao;
    }

//    private Map<Pair<Long, Long>, LiveData<List<Price>>> mCache;
//
//    public LiveData<List<Price>> findShoppingListProductBestPrices(long shoppingListId, long productId) {
//        Pair<Long, Long> key = new Pair<>(shoppingListId, productId);
//        if (!this.mCache.containsKey(key)) {
//            this.mCache.put(key, this.mBestPriceDao.findPriceOfShoppingListProduct(shoppingListId, productId));
//        }
//        return this.mCache.get(key);
//    }

    public List<Price> findShoppingListProductBestPricesNow(long shoppingListId, long productId) {
        return this.getBestPriceDao().findPriceOfShoppingListProductNow(shoppingListId, productId);
    }

}
