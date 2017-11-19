package com.alvin.cheapyshopping.olddb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.alvin.cheapyshopping.olddb.models.PriceModel;
import com.alvin.cheapyshopping.olddb.models.ProductModel;
import com.alvin.cheapyshopping.olddb.models.ShoppingListModel;
import com.alvin.cheapyshopping.olddb.models.StoreModel;

/**
 * Created by Alvin on 5/11/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "database.db";
    private static final int DATABASE_VERSION = 1;

    private static DatabaseHelper sInstance;
    private static SQLiteDatabase sDbInstance;

    public static DatabaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DatabaseHelper(context);
        }
        return sInstance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    private final Context context;

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        ShoppingListModel.manager.createTable(db);
        ProductModel.manager.createTable(db);
        StoreModel.manager.createTable(db);
        PriceModel.manager.createTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        PriceModel.manager.dropTable(db);
        StoreModel.manager.dropTable(db);
        ProductModel.manager.dropTable(db);
        ShoppingListModel.manager.dropTable(db);
        this.onCreate(db);
    }

    public SQLiteDatabase getDatabase() {
        if (sDbInstance == null) {
            sDbInstance = this.getWritableDatabase();
        }
        return sDbInstance;
        /* Leave the db open and android will automatically close/free it when application close */
    }

    public boolean deleteDatabase() {
        this.close();
        sDbInstance = null;
        return context.deleteDatabase(DATABASE_NAME);
    }

}
