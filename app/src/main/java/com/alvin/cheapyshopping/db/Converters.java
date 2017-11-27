package com.alvin.cheapyshopping.db;

import android.arch.persistence.room.TypeConverter;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Alvin on 20/11/2017.
 */

public class Converters {

    @TypeConverter
    public static Calendar longToCalendar(Long value) {
        if (value != null) {
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(value);
            return c;
        } else {
            return null;
        }
    }

    @TypeConverter
    public static Long calendarToLong(Calendar c) {
        return c != null ? c.getTimeInMillis() : null;
    }

}
