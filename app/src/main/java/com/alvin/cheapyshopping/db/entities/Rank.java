package com.alvin.cheapyshopping.db.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by cheng on 12/1/2017.
 */

@Entity
public class Rank {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "rank_id")
    private String mRankId;

    @ColumnInfo(name = "rank")
    private String mRank;

    @ColumnInfo(name = "min_score")
    private Integer mMinScore;

    @ColumnInfo(name = "max_score")
    private Integer mMaxScore;

    @NonNull
    public String getRankId() {
        return mRankId;
    }

    public void setRankId(@NonNull String rankId) {
        mRankId = rankId;
    }

    public String getRank() {
        return mRank;
    }

    public void setRank(String rank) {
        mRank = rank;
    }

    public Integer getMinScore() {
        return mMinScore;
    }

    public void setMinScore(Integer minScore) {
        mMinScore = minScore;
    }

    public Integer getMaxScore() {
        return mMaxScore;
    }

    public void setMaxScore(Integer maxScore) {
        mMaxScore = maxScore;
    }
}
