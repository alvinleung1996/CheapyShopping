package com.alvin.cheapyshopping.repositories;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.util.ArrayMap;

import com.alvin.cheapyshopping.db.AppDatabase;
import com.alvin.cheapyshopping.db.daos.ShoppingListDao;
import com.alvin.cheapyshopping.db.entities.ShoppingList;

import java.util.List;
import java.util.Map;

/**
 * Created by Alvin on 20/11/2017.
 */

public class ShoppingListRepository {

    @SuppressLint("StaticFieldLeak")
    private static ShoppingListRepository sInstance;

    public static ShoppingListRepository getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new ShoppingListRepository(context);
        }
        return sInstance;
    }


    private final Context mContext;

    private ShoppingListRepository(Context context) {
        this.mContext = context.getApplicationContext();
    }


    /*
    ************************************************************************************************
    * Dao
    ************************************************************************************************
     */

    private ShoppingListDao mShoppingListDao;
    private ShoppingListDao getShoppingListDao() {
        if (this.mShoppingListDao == null) {
            this.mShoppingListDao = AppDatabase.getInstance(this.mContext).getShoppingListDao();
        }
        return this.mShoppingListDao;
    }


    /*
    ************************************************************************************************
    * Query, Async
    ************************************************************************************************
     */

    private Map<String, LiveData<ShoppingList>> mShoppingListCache;
    public LiveData<ShoppingList> findShoppingList(String shoppingListId) {
        if (this.mShoppingListCache == null) {
            this.mShoppingListCache = new ArrayMap<>();
        }
        if (!this.mShoppingListCache.containsKey(shoppingListId)) {
            this.mShoppingListCache.put(shoppingListId, this.getShoppingListDao()
                    .findShoppingList(shoppingListId));
        }
        return this.mShoppingListCache.get(shoppingListId);
    }


    private Map<String, LiveData<List<ShoppingList>>> mAccountShoppingListsCache;
    public LiveData<List<ShoppingList>> findAccountShoppingLists(String accountId) {
        if (this.mAccountShoppingListsCache == null) {
            this.mAccountShoppingListsCache = new ArrayMap<>();
        }
        if (!this.mAccountShoppingListsCache.containsKey(accountId)) {
            this.mAccountShoppingListsCache.put(accountId, this.getShoppingListDao()
                    .findAccountShoppingLists(accountId));
        }
        return this.mAccountShoppingListsCache.get(accountId);
    }


    private Map<String, LiveData<List<ShoppingList>>> mShoppingListsNotContainProductCache;
    public LiveData<List<ShoppingList>> findShoppingListsNotContainProduct(String productId) {
        if (this.mShoppingListsNotContainProductCache == null) {
            this.mShoppingListsNotContainProductCache = new ArrayMap<>();
        }
        if (!this.mShoppingListsNotContainProductCache.containsKey(productId)) {
            this.mShoppingListsNotContainProductCache
                    .put(productId, this.getShoppingListDao()
                            .findShoppingListsNotContainProduct(productId));
        }
        return this.mShoppingListsNotContainProductCache.get(productId);
    }


    /*
    ************************************************************************************************
    * Query, Sync
    ************************************************************************************************
     */

    public ShoppingList findShoppingListNow(String shoppingListId) {
        return this.getShoppingListDao().findShoppingListNow(shoppingListId);
    }

    public List<ShoppingList> findAccountShoppingListsNow(String accountId) {
        return this.getShoppingListDao().findAccountShoppingListsNow(accountId);
    }


    private Map<String, List<ShoppingList>> mShoppingListsNotContainProductCacheNow;
    public List<ShoppingList> findShoppingListsNotContainProductNow(String productId) {
        if (this.mShoppingListsNotContainProductCacheNow == null) {
            this.mShoppingListsNotContainProductCacheNow = new ArrayMap<>();
        }
        if (!this.mShoppingListsNotContainProductCacheNow.containsKey(productId)) {
            this.mShoppingListsNotContainProductCacheNow
                    .put(productId, this.getShoppingListDao()
                            .findShoppingListsNotContainProductNow(productId));
        }
        return this.mShoppingListsNotContainProductCacheNow.get(productId);
    }


    /*
    ************************************************************************************************
    * Other
    ************************************************************************************************
     */

    public long[] insertShoppingList(ShoppingList... shoppingLists) {
        return this.getShoppingListDao().insertShoppingList(shoppingLists);
    }

    public int updateShoppingList(ShoppingList... shoppingLists) {
        return this.getShoppingListDao().updateShoppingList(shoppingLists);
    }

    public int deleteShoppingList(ShoppingList... shoppingLists) {
        return this.getShoppingListDao().deleteShoppingList(shoppingLists);
    }

}
