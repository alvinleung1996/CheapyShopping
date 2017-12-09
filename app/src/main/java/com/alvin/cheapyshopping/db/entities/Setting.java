package com.alvin.cheapyshopping.db.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by cheng on 12/9/2017.
 */

@Entity
public class Setting {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "setting_id")
    private String mSettingId;

    @ColumnInfo(name = "setting_boolean")
    private Boolean mSettingBoolean = false;

    @NonNull
    public String getSettingId() {
        return mSettingId;
    }

    public void setSettingId(@NonNull String settingId) {
        mSettingId = settingId;
    }

    public Boolean getSettingBoolean() {
        return mSettingBoolean;
    }

    public void setSettingBoolean(Boolean settingBoolean) {
        mSettingBoolean = settingBoolean;
    }
}
