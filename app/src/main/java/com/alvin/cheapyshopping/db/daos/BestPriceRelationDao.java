package com.alvin.cheapyshopping.db.daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
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
    * Bulk delete shopping list product best price
    ************************************************************************************************
     */

    @Query("DELETE FROM BestPriceRelation"
            + " WHERE foreign_shopping_list_id = :shoppingListId"
                + " AND foreign_product_id = :productId")
    int deleteShoppingListProductBestPrice(long shoppingListId, long productId);


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
