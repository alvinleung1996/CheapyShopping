package com.alvin.cheapyshopping.db.daos;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.alvin.cheapyshopping.db.entities.Setting;

import java.util.List;


/**
 * Created by cheng on 12/9/2017.
 */

@Dao
public interface SettingDao {
    /*
    ************************************************************************************************
    * Query, Async
    ************************************************************************************************
     */
    @Query("SELECT * From Setting")
    LiveData<List<Setting>> getAllSettings();

    @Query("SELECT * FROM Setting WHERE Setting.setting_id = :Id")
    LiveData<Setting> getSettingById(String Id);


    /*
    ************************************************************************************************
    * Query, sync
    ************************************************************************************************
     */
    @Query("SELECT * From Setting")
    List<Setting> getAllSettingsNow();

    @Query("SELECT * FROM Setting WHERE Setting.setting_id = :Id")
    Setting getSettingByIdNow(String Id);

    /*
    ************************************************************************************************
    * Other
    ************************************************************************************************
     */

    @Insert
    long[] insertSetting(Setting... settings);

    @Update
    int updateSetting(Setting... settings);

    @Delete
    int deleteSetting(Setting... settings);
}
