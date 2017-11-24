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

    private Map<Long, LiveData<ShoppingList>> mShoppingListCache;
    public LiveData<ShoppingList> findShoppingList(long shoppingListId) {
        if (this.mShoppingListCache == null) {
            this.mShoppingListCache = new ArrayMap<>();
        }
        if (!this.mShoppingListCache.containsKey(shoppingListId)) {
            this.mShoppingListCache.put(shoppingListId, this.getShoppingListDao()
                    .findShoppingList(shoppingListId));
        }
        return this.mShoppingListCache.get(shoppingListId);
    }


    private Map<Long, LiveData<List<ShoppingList>>> mAccountShoppingListsCache;
    public LiveData<List<ShoppingList>> findAccountShoppingLists(long accountId) {
        if (this.mAccountShoppingListsCache == null) {
            this.mAccountShoppingListsCache = new ArrayMap<>();
        }
        if (!this.mAccountShoppingListsCache.containsKey(accountId)) {
            this.mAccountShoppingListsCache.put(accountId, this.getShoppingListDao()
                    .findAccountShoppingLists(accountId));
        }
        return this.mAccountShoppingListsCache.get(accountId);
    }


    /*
    ************************************************************************************************
    * Query, Sync
    ************************************************************************************************
     */

    public ShoppingList findShoppingListNow(long shoppingListId) {
        return this.getShoppingListDao().findShoppingListNow(shoppingListId);
    }

    public List<ShoppingList> findAccountShoppingListsNow(long accountId) {
        return this.getShoppingListDao().findAccountShoppingListsNow(accountId);
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
