package com.alvin.cheapyshopping.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.alvin.cheapyshopping.db.models.PriceModel;
import com.alvin.cheapyshopping.db.models.ProductModel;
import com.alvin.cheapyshopping.db.models.ShoppingListModel;
import com.alvin.cheapyshopping.db.models.StoreModel;

/**
 * Created by Alvin on 5/11/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "database.db";
    private static final int DATABASE_VERSION = 1;

    private static DatabaseHelper instance;
    private static SQLiteDatabase dbInstance;

    public static DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context);
        }
        return instance;
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
        if (dbInstance == null) {
            dbInstance = this.getWritableDatabase();
        }
        return dbInstance;
        /* Leave the db open and android will automatically close/free it when application close */
    }

    public boolean deleteDatabase() {
        this.close();
        dbInstance = null;
        return context.deleteDatabase(DATABASE_NAME);
    }

}
