package com.alvin.cheapyshopping.repositories;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.support.v4.util.Pair;
import android.util.ArrayMap;

import com.alvin.cheapyshopping.db.AppDatabase;
import com.alvin.cheapyshopping.db.daos.ShoppingListProductRelationDao;
import com.alvin.cheapyshopping.db.entities.ShoppingListProductRelation;

import java.util.List;
import java.util.Map;

/**
 * Created by Alvin on 20/11/2017.
 */

public class ShoppingListProductRelationRepository {

    @SuppressLint("StaticFieldLeak")
    private static ShoppingListProductRelationRepository sInstance;

    public static ShoppingListProductRelationRepository getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new ShoppingListProductRelationRepository(context);
        }
        return sInstance;
    }


    private final Context mContext;

    private ShoppingListProductRelationRepository(Context context) {
        this.mContext = context.getApplicationContext();
    }


    /*
    ************************************************************************************************
    * Dao
    ************************************************************************************************
     */

    private ShoppingListProductRelationDao mShoppingListProductRelationDao;
    private ShoppingListProductRelationDao getShoppingListProductRelationDao() {
        if (this.mShoppingListProductRelationDao == null) {
            this.mShoppingListProductRelationDao = AppDatabase.getInstance(this.mContext).getShoppingListProductRelationDao();
        }
        return this.mShoppingListProductRelationDao;
    }


    /*
    ************************************************************************************************
    * Query, Async
    ************************************************************************************************
     */

    private Map<String, LiveData<List<ShoppingListProductRelation>>> mRelationsCache;
    public LiveData<List<ShoppingListProductRelation>> findShoppingListProductRelations(String shoppingListId) {
        if (this.mRelationsCache == null) {
            this.mRelationsCache = new ArrayMap<>();
        }
        if (!this.mRelationsCache.containsKey(shoppingListId)) {
            this.mRelationsCache.put(shoppingListId,
                    this.getShoppingListProductRelationDao().findShoppingListProductRelations(shoppingListId));
        }
        return this.mRelationsCache.get(shoppingListId);
    }


    private Map<Pair<String, String>, LiveData<ShoppingListProductRelation>> mRelationCache;
    public LiveData<ShoppingListProductRelation> getShoppingListProductRelation(String shoppingListId, String productId) {
        if (this.mRelationCache == null) {
            this.mRelationCache = new ArrayMap<>();
        }
        Pair<String, String> key = new Pair<>(shoppingListId, productId);
        if (!this.mRelationCache.containsKey(key)) {
            this.mRelationCache.put(key, this.getShoppingListProductRelationDao()
                    .getShoppingListProductRelation(shoppingListId, productId));
        }
        return this.mRelationCache.get(key);
    }


    /*
    ************************************************************************************************
    * Query, Sync
    ************************************************************************************************
     */

    public List<ShoppingListProductRelation> findShoppingListProductRelationsNow(String shoppingListId) {
        return this.getShoppingListProductRelationDao().findShoppingListProductRelationsNow(shoppingListId);
    }


    public ShoppingListProductRelation getShoppingListProductRelationNow(String shoppingListId, String productId) {
        return this.getShoppingListProductRelationDao()
                .getShoppingListProductRelationNow(shoppingListId, productId);
    }



    /*
    ************************************************************************************************
    * Other
    ************************************************************************************************
     */

    public long[] insertShoppingListProductRelation(ShoppingListProductRelation... relations) {
        return this.getShoppingListProductRelationDao()
                .insertShoppingListProductRelation(relations);
    }

    public int updateShoppingListProductRelation(ShoppingListProductRelation... relations) {
        return this.getShoppingListProductRelationDao()
                .updateShoppingListProductRelation(relations);
    }

    public int deleteShoppingListProductRelation(ShoppingListProductRelation... relations) {
        return this.getShoppingListProductRelationDao()
                .deleteShoppingListProductRelation(relations);
    }

    public int deleteShoppingListProductRelation(String shoppingListId, String productId) {
        return this.getShoppingListProductRelationDao()
                .deleteShoppingListProductRelation(shoppingListId, productId);
    }

}
