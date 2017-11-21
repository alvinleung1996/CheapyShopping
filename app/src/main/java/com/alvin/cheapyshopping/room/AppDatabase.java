package com.alvin.cheapyshopping.room;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.support.annotation.NonNull;

import com.alvin.cheapyshopping.room.daos.AccountDao;
import com.alvin.cheapyshopping.room.daos.BestPriceDao;
import com.alvin.cheapyshopping.room.daos.PriceDao;
import com.alvin.cheapyshopping.room.daos.ProductDao;
import com.alvin.cheapyshopping.room.daos.ShoppingListDao;
import com.alvin.cheapyshopping.room.daos.ShoppingListProductDao;
import com.alvin.cheapyshopping.room.daos.StoreDao;
import com.alvin.cheapyshopping.room.entities.Account;
import com.alvin.cheapyshopping.room.entities.BestPrice;
import com.alvin.cheapyshopping.room.entities.Price;
import com.alvin.cheapyshopping.room.entities.Product;
import com.alvin.cheapyshopping.room.entities.ShoppingList;
import com.alvin.cheapyshopping.room.entities.ShoppingListProduct;
import com.alvin.cheapyshopping.room.entities.Store;

/**
 * Created by Alvin on 19/11/2017.
 */

@Database(
    entities = {
        ShoppingList.class,
        Store.class,
        Product.class,
        ShoppingListProduct.class,
        Price.class,
        BestPrice.class,
        Account.class
    },
    version = 1
)
@TypeConverters({Converters.class})
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
            sInstance = Room.databaseBuilder(context, AppDatabase.class, "app_database.db")
                    .addCallback(new AppDatabaseCallback(context))
                    .build();
            // For debug use: the database will be destroyed after the process is killed
//            sInstance = Room.inMemoryDatabaseBuilder(context.getApplicationContext(), AppDatabase.class)
//                    .addCallback(new AppDatabaseCallback(context))
//                    .build();
        }
        return sInstance;
    }


    public abstract ShoppingListDao getShoppingListDao();

    public abstract StoreDao getStoreDao();

    public abstract ProductDao getProductDao();

    public abstract ShoppingListProductDao getShoppingListProductDao();

    public abstract PriceDao getPriceDao();

    public abstract BestPriceDao getBestPriceDao();

    public abstract AccountDao getAccountDao();

}
