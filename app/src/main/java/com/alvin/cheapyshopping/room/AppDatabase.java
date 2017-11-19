package com.alvin.cheapyshopping.room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.alvin.cheapyshopping.room.daos.ProductDao;
import com.alvin.cheapyshopping.room.daos.StoreDao;
import com.alvin.cheapyshopping.room.entities.Product;
import com.alvin.cheapyshopping.room.entities.Store;

/**
 * Created by Alvin on 19/11/2017.
 */

@Database(entities = {
        Store.class,
        Product.class
}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase sInstance;

    /**
     * The context parameter should be an application context instead of activity or fragment,
     * otherwise these activity or fragment cannot be garbage collected
     * and cause memory leak.
     *
     * @param context
     * @return
     */
    public static AppDatabase getInstance(Context context) {
        if (sInstance == null) {
//            sInstance = Room.databaseBuilder(context, AppDatabase.class, "database.db").build();
            // For debug use: the database will be destroyed after the process is killed
            sInstance = Room.inMemoryDatabaseBuilder(context.getApplicationContext(), AppDatabase.class).allowMainThreadQueries().build();
        }
        return sInstance;
    }

    public abstract StoreDao getStoreDao();

    public abstract ProductDao getProductDao();

}
