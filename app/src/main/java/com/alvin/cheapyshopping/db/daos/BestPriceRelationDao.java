package com.alvin.cheapyshopping.db.daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Update;

import com.alvin.cheapyshopping.db.entities.BestPriceRelation;

/**
 * Created by Alvin on 21/11/2017.
 */

@Dao
public interface BestPriceRelationDao {

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

    @Insert
    long[] insertBestPrice(BestPriceRelation... bestPriceRelations);

    @Update
    int updateBestPrice(BestPriceRelation... bestPriceRelations);

    @Delete
    int deleteBestPrice(BestPriceRelation... bestPriceRelations);
}
