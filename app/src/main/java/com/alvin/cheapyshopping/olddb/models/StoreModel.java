package com.alvin.cheapyshopping.olddb.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.alvin.cheapyshopping.olddb.AbstractModel;

// TODO: import java.math.BigDecimal;

/**
 * Created by Alvin on 5/11/2017.
 */

public class StoreModel extends AbstractStoreModel<StoreModel> {

    public static final AbstractManager<StoreModel> manager = new AbstractManager<StoreModel>() {
        @Override
        public StoreModel newModelInstance(Context context) {
            return new StoreModel(context);
        }
    };

    public StoreModel(Context context) {
        super(context, manager);
    }

}

class AbstractStoreModel<M extends AbstractStoreModel<M>> extends AbstractModel<M> {

    public static final String COLUMN_STORE_ID = "store_id";
    public static final String COLUMN_LOCATION = "location";
    public static final String COLUMN_NAME = "name";


    public static abstract class AbstractManager<M extends AbstractStoreModel<M>> extends AbstractModel.AbstractManager<M> {

        @Override
        public String getTableName() {
            return "Store";
        }

        @Override
        public String getIdColumnName() {
            return COLUMN_STORE_ID;
        }

        @Override
        public void createTable(SQLiteDatabase db) {
            db.execSQL(
                    "CREATE TABLE " + this.getTableName() + " (" +
                            COLUMN_STORE_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE," +
                            COLUMN_LOCATION + " TEXT NOT NULL," +
                            COLUMN_NAME + " TEXT NOT NULL" +
                    ")"
            );
        }

        @Override
        public void dropTable(SQLiteDatabase db) {
            db.execSQL("DROP TABLE IF EXISTS " + this.getTableName());
        }

    };


    public AbstractStoreModel(Context context, AbstractManager<M> manager) {
        super(context, manager);
        this.storeId = -1;
    }

    public long storeId;
    public String location;
    public String name;


    @Override
    protected long getIdValue() {
        return this.storeId;
    }

    @Override
    protected void setIdValue(long idValue) {
        this.storeId = idValue;
    }


    @Override
    protected void onSave(ContentValues values) {
        super.onSave(values);
        values.put(COLUMN_LOCATION, this.location);
        values.put(COLUMN_NAME, this.name);
    }

    @Override
    public void readFromCursor(Cursor cursor) {
        super.readFromCursor(cursor);
        this.location = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION));
        this.name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
    }

}
