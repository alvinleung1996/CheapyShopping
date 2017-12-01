package com.alvin.cheapyshopping.repositories;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.content.Context;

import com.alvin.cheapyshopping.db.AppDatabase;
import com.alvin.cheapyshopping.db.daos.RankDao;
import com.alvin.cheapyshopping.db.entities.Rank;

import java.util.List;

/**
 * Created by cheng on 12/1/2017.
 */

public class RankRepository {

    @SuppressLint("StaticFieldLeak")
    private static RankRepository sInstance;

    public static RankRepository getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new RankRepository(context);
        }
        return sInstance;
    }

    private final Context mContext;


    private RankRepository(Context context) {
        this.mContext = context.getApplicationContext();
    }

    /*
    ************************************************************************************************
    * Dao
    ************************************************************************************************
     */

    private RankDao mRankDao;
    private RankDao getRankDao() {
        if (this.mRankDao == null) {
            this.mRankDao = AppDatabase.getInstance(this.mContext).getRankDao();
        }
        return this.mRankDao;
    }

    /*
    ************************************************************************************************
    * Query, Async
    ************************************************************************************************
     */

    private LiveData<List<Rank>> mRanks;

    public LiveData<List<Rank>> getRanksByScore (int score){
        if(mRanks == null){
            mRanks = this.getRankDao().getRanksByScore(score);
        }
        return mRanks;
    }

    private LiveData<List<Rank>> mCurrentAccountRanks;

    public LiveData<List<Rank>> getCurrentAccountRanks(){
        if (mCurrentAccountRanks == null){
            mCurrentAccountRanks = this.getRankDao().getCurrentAccountRanks();
        }
        return mCurrentAccountRanks;
    }


    /*
    ************************************************************************************************
    * Query, Sync
    ************************************************************************************************
     */

    private List<Rank> mRanksNow;

    public List<Rank> getRanksByScoreNow (int score){
        if(mRanksNow == null){
           mRanksNow = this.getRankDao().getRanksByScoreNow(score);
        }
        return mRanksNow;
    }



}
