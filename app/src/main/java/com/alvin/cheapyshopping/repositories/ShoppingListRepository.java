package com.alvin.cheapyshopping.repositories;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.ArrayMap;

import com.alvin.cheapyshopping.room.AppDatabase;
import com.alvin.cheapyshopping.room.daos.ShoppingListDao;
import com.alvin.cheapyshopping.room.daos.ShoppingListProductDao.ShoppingListProductDetail;
import com.alvin.cheapyshopping.room.entities.Price;
import com.alvin.cheapyshopping.room.entities.Product;
import com.alvin.cheapyshopping.room.entities.ShoppingList;
import com.alvin.cheapyshopping.room.entities.ShoppingListProduct;
import com.alvin.cheapyshopping.room.entities.Store;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by Alvin on 20/11/2017.
 */

public class ShoppingListRepository {

    private static ShoppingListRepository sInstance;

    public static ShoppingListRepository getInstance(Context context) {
        if (sInstance == null) {
            AppDatabase db = AppDatabase.getInstance(context);
            sInstance = new ShoppingListRepository(
                    db.getShoppingListDao(),
                    ShoppingListProductRepository.getInstance(context),
                    ProductRepository.getInstance(context),
                    PriceRepository.getInstance(context),
                    StoreRepository.getInstance(context)
            );
        }
        return sInstance;
    }


    private final ShoppingListDao mDao;

    private final ShoppingListProductRepository mListProductRepository;
    private final ProductRepository mProductRepository;
    private final PriceRepository mPriceRepository;
    private final StoreRepository mStoreRepository;

    private final Map<Long, LiveData<Map<Store, List<ShoppingListProductDetail>>>> mResultCache;
    private LiveData<Map<Store, List<ShoppingListProductDetail>>> mResultCacheOfLatest;

    private LiveData<ShoppingList> mLatest;
    private LiveData<List<ShoppingListProduct>> mLatestProducts;
    private LiveData<Map<Store, List<Product>>> mComputedResult;


    private ShoppingListRepository(ShoppingListDao dao,
                                   ShoppingListProductRepository listProductRepository,
                                   ProductRepository productRepository,
                                   PriceRepository priceRepository,
                                   StoreRepository storeRepository) {
        this.mDao = dao;
        this.mListProductRepository = listProductRepository;
        this.mProductRepository = productRepository;
        this.mPriceRepository = priceRepository;
        this.mStoreRepository = storeRepository;

        this.mResultCache = new ArrayMap<>();
    }

    /*
    ************************************************************************************************
    * get ShoppingList
    ************************************************************************************************
     */



    private Map<Long, LiveData<List<ShoppingList>>> mAccountShoppingListCache;

    public LiveData<List<ShoppingList>> getAccountShoppingList(long accountId) {
        if (this.mAccountShoppingListCache == null) {
            this.mAccountShoppingListCache = new ArrayMap<>();
        }
        if (!this.mAccountShoppingListCache.containsKey(accountId)) {
            this.mAccountShoppingListCache.put(accountId, this.mDao.getShoppingListsOfAccount(accountId));
        }
        return this.mAccountShoppingListCache.get(accountId);
    }


    /*
    ************************************************************************************************
    * Other
    ************************************************************************************************
     */

    public long[] insert(ShoppingList... stores) {
        return this.mDao.insert(stores);
    }

    public int update(ShoppingList... stores) {
        return this.mDao.update(stores);
    }

    public int delete(ShoppingList... stores) {
        return this.mDao.delete(stores);
    }

}
