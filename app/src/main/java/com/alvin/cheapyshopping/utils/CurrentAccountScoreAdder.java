package com.alvin.cheapyshopping.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import com.alvin.cheapyshopping.repositories.AccountRepository;

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

    public void addScore(int score){
        new AddScoreToCurrentAccountTask(mContext.getApplicationContext(), score).execute();
    }

    private static class AddScoreToCurrentAccountTask extends AsyncTask<Void, Void, Integer> {
        @SuppressLint("StaticFieldLeak")
        private Context mContext;
        private int mScore;

        private AddScoreToCurrentAccountTask (Context context, int score){
            mContext = context;
            mScore = score;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            AccountRepository.getInstance(mContext).addScoreToCurrentAccountNow(mScore);
            return 0;
        }
    }


}
