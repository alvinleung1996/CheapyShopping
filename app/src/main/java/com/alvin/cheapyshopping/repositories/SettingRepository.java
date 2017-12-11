package com.alvin.cheapyshopping.repositories;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.content.Context;

import com.alvin.cheapyshopping.db.AppDatabase;
import com.alvin.cheapyshopping.db.daos.SettingDao;
import com.alvin.cheapyshopping.db.entities.Setting;

import java.util.List;

/**
 * Created by cheng on 12/9/2017.
 */

public class SettingRepository {

    @SuppressLint("StaticFieldLeak")
    private static SettingRepository sInstance;

    public static SettingRepository getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new SettingRepository(context);
        }
        return sInstance;
    }

    private final Context mContext;


    private SettingRepository(Context context) {
        this.mContext = context.getApplicationContext();
    }

    /*
    ************************************************************************************************
    * Dao
    ************************************************************************************************
     */

    private SettingDao mSettingDao;
    private SettingDao getSettingDao() {
        if (this.mSettingDao == null) {
            this.mSettingDao = AppDatabase.getInstance(this.mContext).getSettingDao();
        }
        return this.mSettingDao;
    }

    /*
    ************************************************************************************************
    * Query, Async
    ************************************************************************************************
     */

    private LiveData<List<Setting>> mSettings;

    public LiveData<List<Setting>> getAllSettings (){
        if(mSettings == null){
            mSettings = this.getSettingDao().getAllSettings();
        }
        return mSettings;
    }


    private LiveData<Setting> mSetting;
    public LiveData<Setting> getSettingById(String settingId){
        if(mSetting == null){
            mSetting = this.getSettingDao().getSettingById(settingId);
        }
        return mSetting;
    }

    /*
    ************************************************************************************************
    * Query, Sync
    ************************************************************************************************
     */

    private Setting mSettingNow;
    public Setting getSettingByIdNow(String settingId){
        if(mSettingNow == null){
            mSettingNow = this.getSettingDao().getSettingByIdNow(settingId);
        }
        return mSettingNow;
    }

    /*
    ************************************************************************************************
    * Other
    ************************************************************************************************
     */

    public long[] insertSetting(Setting... settings) {
        return this.getSettingDao().insertSetting(settings);
    }

    public int updateSetting(Setting... settings) {
        return this.getSettingDao().updateSetting(settings);
    }

    public int deleteSetting(Setting... settings) {
        return this.getSettingDao().deleteSetting(settings);
    }

}
