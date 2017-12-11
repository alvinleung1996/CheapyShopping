package com.alvin.cheapyshopping.db.daos;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.alvin.cheapyshopping.db.entities.Rank;

import java.util.List;

/**
 * Created by cheng on 12/1/2017.
 */
@Dao
public interface RankDao {
    /*
    ************************************************************************************************
    * Query, Async
    ************************************************************************************************
     */

    @Query("SELECT * From Rank R WHERE :score >= R.min_score ORDER BY R.min_score DESC")
    LiveData<List<Rank>> getRanksByScore(int score);



    @Query("SELECT R.* FROM Rank R , Account A " +
            "WHERE A.account_score >= R.min_score " +
            "AND A.account_id = (SELECT account_id FROM Account LIMIT 1) " +
            "ORDER BY R.min_score DESC")
    LiveData<List<Rank>> getCurrentAccountRanks();



    /*
    ************************************************************************************************
    * Query, Sync
    ************************************************************************************************
     */

    @Query("SELECT * From Rank R WHERE :score > R.min_score ORDER BY R.min_score DESC ")
    List<Rank> getRanksByScoreNow(int score);

    @Query("SELECT R.* FROM Rank R , Account A " +
            "WHERE A.account_score >= R.min_score " +
            "AND A.account_id = (SELECT account_id FROM Account LIMIT 1) " +
            "ORDER BY R.min_score DESC")
    List<Rank> getCurrentAccountRanksNow();

    /*
    ************************************************************************************************
    * Other
    ************************************************************************************************
     */

    @Insert
    long[] insertRank(Rank... ranks);

    @Update
    int updateRank(Rank... ranks);

    @Delete
    int deleteRank(Rank... ranks);

}
