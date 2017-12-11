package com.alvin.cheapyshopping.utils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alvin.cheapyshopping.R;
import com.alvin.cheapyshopping.db.AppDatabaseCallback;
import com.alvin.cheapyshopping.db.entities.Rank;
import com.alvin.cheapyshopping.db.entities.Setting;
import com.alvin.cheapyshopping.repositories.AccountRepository;
import com.alvin.cheapyshopping.repositories.RankRepository;
import com.alvin.cheapyshopping.repositories.SettingRepository;

import java.util.List;
import java.util.Set;

/**
 * Created by cheng on 12/1/2017.
 */

public class CurrentAccountScoreAdder {

    @SuppressLint("StaticFieldLeak")
    private static CurrentAccountScoreAdder sInstance;

    public static CurrentAccountScoreAdder getsInstance(Context context) {
        if (sInstance == null) {
            sInstance = new CurrentAccountScoreAdder(context);
        }
        return sInstance;
    }

    private Context mContext;

    private CurrentAccountScoreAdder(Context context){
        mContext = context.getApplicationContext();
    }

    /*
    ************************************************************************************************
    * Add Score to account
    ************************************************************************************************
     */

    public void addScore(int scoreToAdd){
        new AddScoreToCurrentAccountTask(mContext, scoreToAdd).execute();
    }


    private static class AddScoreToCurrentAccountTask extends AsyncTask<Void, Void, Integer> {
        @SuppressLint("StaticFieldLeak")
        private Context mContext;
        private int mScoreToAdd;
        private int mOriginalScore;
        private List<Rank> mUpdatedRanks;

        private AddScoreToCurrentAccountTask (Context context, int scoreToAdd){
            mContext = context;
            mScoreToAdd = scoreToAdd;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            mOriginalScore = AccountRepository.getInstance(mContext).getAllAccountsNow().get(0).getAccountScore();
            AccountRepository.getInstance(mContext).addScoreToCurrentAccountNow(mScoreToAdd);
            mUpdatedRanks = RankRepository.getInstance(mContext).getCurrentAccountRanksNow();


            return 0;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if(mUpdatedRanks.get(0).getMinScore() > mOriginalScore){
                int newRank = mUpdatedRanks.size() - 1;
                Log.d("Debug", "New rank achieved: " + newRank);
                Toast.makeText(mContext,"Gained new Rank: Rank " + newRank + " !",Toast.LENGTH_LONG).show();
            }
    }
    }


}
