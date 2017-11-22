package com.alvin.cheapyshopping.repositories;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.content.Context;
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

    private Map<Long, LiveData<List<ShoppingListProductRelation>>> mRelationCache;
    public LiveData<List<ShoppingListProductRelation>> findShoppingListProductRelations(long shoppingListId) {
        if (this.mRelationCache == null) {
            this.mRelationCache = new ArrayMap<>();
        }
        if (!this.mRelationCache.containsKey(shoppingListId)) {
            this.mRelationCache.put(shoppingListId,
                    this.getShoppingListProductRelationDao().findShoppingListProductRelations(shoppingListId));
        }
        return this.mRelationCache.get(shoppingListId);
    }


    /*
    ************************************************************************************************
    * Query, Sync
    ************************************************************************************************
     */

    public List<ShoppingListProductRelation> findShoppingListProductRelationsNow(long shoppingListId) {
        return this.getShoppingListProductRelationDao().findShoppingListProductRelationsNow(shoppingListId);
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

}
