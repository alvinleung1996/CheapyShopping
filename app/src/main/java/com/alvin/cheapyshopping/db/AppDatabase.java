package com.alvin.cheapyshopping.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.alvin.cheapyshopping.db.daos.AccountDao;
import com.alvin.cheapyshopping.db.daos.BestPriceRelationDao;
import com.alvin.cheapyshopping.db.daos.PriceDao;
import com.alvin.cheapyshopping.db.daos.ProductDao;
import com.alvin.cheapyshopping.db.daos.ProductPriceDao;
import com.alvin.cheapyshopping.db.daos.RankDao;
import com.alvin.cheapyshopping.db.daos.ShoppingListDao;
import com.alvin.cheapyshopping.db.daos.ShoppingListProductDao;
import com.alvin.cheapyshopping.db.daos.ShoppingListProductRelationDao;
import com.alvin.cheapyshopping.db.daos.StoreDao;
import com.alvin.cheapyshopping.db.daos.StorePriceDao;
import com.alvin.cheapyshopping.db.entities.Account;
import com.alvin.cheapyshopping.db.entities.BestPriceRelation;
import com.alvin.cheapyshopping.db.entities.Price;
import com.alvin.cheapyshopping.db.entities.Product;
import com.alvin.cheapyshopping.db.entities.Rank;
import com.alvin.cheapyshopping.db.entities.ShoppingList;
import com.alvin.cheapyshopping.db.entities.ShoppingListProductRelation;
import com.alvin.cheapyshopping.db.entities.Store;

/**
 * Created by Alvin on 19/11/2017.
 */

@Database(
    entities = {
        ShoppingList.class,
        Store.class,
        Product.class,
        ShoppingListProductRelation.class,
        Price.class,
        BestPriceRelation.class,
        Account.class,
        Rank.class
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

    public abstract ShoppingListProductRelationDao getShoppingListProductRelationDao();

    public abstract PriceDao getPriceDao();

    public abstract BestPriceRelationDao getBestPriceRelationDao();

    public abstract AccountDao getAccountDao();

    public abstract ShoppingListProductDao getShoppingListProductDao();

    public abstract StorePriceDao getStorePriceDao();

    public abstract ProductPriceDao getProductPriceDao();

    public abstract RankDao getRankDao();

}
