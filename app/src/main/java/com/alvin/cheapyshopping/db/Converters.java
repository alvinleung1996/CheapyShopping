package com.alvin.cheapyshopping.db;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

/**
 * Created by Alvin on 20/11/2017.
 */

public class Converters {

    @TypeConverter
    public static Date longToDate(Long value) {
        return value != null ? new Date(value) : null;
    }

    @TypeConverter
    public static Long dateToLong(Date date) {
        return date != null ? date.getTime() : null;
    }

}
