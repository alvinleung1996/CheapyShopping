package com.alvin.cheapyshopping.repositories;

import android.annotation.SuppressLint;
import android.content.Context;

import com.alvin.cheapyshopping.db.AppDatabase;
import com.alvin.cheapyshopping.db.daos.BestPriceRelationDao;
import com.alvin.cheapyshopping.db.entities.BestPriceRelation;

/**
 * Created by Alvin on 21/11/2017.
 */

public class BestPriceRelationRepository {

    @SuppressLint("StaticFieldLeak")
    private static BestPriceRelationRepository sInstance;

    public static BestPriceRelationRepository getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new BestPriceRelationRepository(context);
        }
        return sInstance;
    }


    private final Context mContext;

    private BestPriceRelationRepository(Context context) {
        this.mContext = context.getApplicationContext();
    }


    /*
    ************************************************************************************************
    * Dao
    ************************************************************************************************
     */

    private BestPriceRelationDao mBestPriceRelationDao;
    private BestPriceRelationDao getBestPriceRelationDao() {
        if (this.mBestPriceRelationDao == null) {
            this.mBestPriceRelationDao = AppDatabase.getInstance(this.mContext).getBestPriceRelationDao();
        }
        return this.mBestPriceRelationDao;
    }

    /*
    ************************************************************************************************
    * Query, Async
    ************************************************************************************************
     */



    /*
    ************************************************************************************************
    * Query, Sync
    ************************************************************************************************
     */




    /*
    ************************************************************************************************
    * Other
    ************************************************************************************************
     */

    public long[] insertBestPrice(BestPriceRelation... bestPriceRelations) {
        return this.getBestPriceRelationDao().insertBestPrice(bestPriceRelations);
    }

    public int udpateBestPrice(BestPriceRelation... bestPriceRelations) {
        return this.getBestPriceRelationDao().updateBestPrice(bestPriceRelations);
    }

    public int deleteBestPrice(BestPriceRelation... bestPriceRelations) {
        return this.getBestPriceRelationDao().deleteBestPrice(bestPriceRelations);
    }

}
