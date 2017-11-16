package com.alvin.cheapyshopping.db.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.alvin.cheapyshopping.db.AbstractModel;

// TODO: import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Alvin on 5/11/2017.
 */

public class PriceModel extends AbstractPriceModel<PriceModel> {

    public static final AbstractManager<PriceModel> manager = new AbstractManager<PriceModel>() {
        @Override
        public PriceModel newModelInstance(Context context) {
            return new PriceModel(context);
        }
    };

    public PriceModel(Context context) {
        super(context, manager);
    }

}

abstract class AbstractPriceModel<M extends AbstractPriceModel<M>> extends AbstractModel<M> {

    public static final String COLUMN_PRICE_ID = "price_id";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_PRICE_DATA_0 = "price_data_0";
    public static final String COLUMN_PRICE_DATA_1 = "price_data_1";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_FOREIGN_PRODUCT_ID = "foreign_product_id";
    public static final String COLUMN_FOREIGN_STORE_ID = "foreign_store_id";

    public static final String TYPE_SINGLE = "SI";
    public static final String TYPE_MULTIPLE = "MU";
    public static final String TYPE_BUY_X_GET_DISCOUNT = "DI";
    public static final String TYPE_BUY_X_GET_Y_FREE = "FE";


    public static abstract class AbstractManager<M extends AbstractPriceModel<M>> extends AbstractModel.AbstractManager<M> {
        @Override
        public String getTableName() {
            return "Price";
        }

        @Override
        public String getIdColumnName() {
            return COLUMN_PRICE_ID;
        }

        @Override
        public void createTable(SQLiteDatabase db) {
            db.execSQL(
                    "CREATE TABLE " + this.getTableName() + " (" +
                            COLUMN_PRICE_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE," +
                            COLUMN_PRICE + " REAL NOT NULL," +
                            COLUMN_TYPE + " TEXT NOT NULL," +
                            COLUMN_PRICE_DATA_0 + " REAL NOT NULL DEFAULT -1," +
                            COLUMN_PRICE_DATA_1 + " REAL NOT NULL DEFAULT -1," +
                            COLUMN_TIME + " INTEGER NOT NULL," +

                            COLUMN_FOREIGN_PRODUCT_ID + " INTEGER NOT NULL," +
                            COLUMN_FOREIGN_STORE_ID + " INTEGER NOT NULL," +

                            "FOREIGN KEY(" + COLUMN_FOREIGN_PRODUCT_ID + ") REFERENCES " +
                                    ProductModel.manager.getTableName() + "(" + ProductModel.COLUMN_PRODUCT_ID + ")" +
                                    " ON DELETE CASCADE," +
                            "FOREIGN KEY(" + COLUMN_FOREIGN_STORE_ID + ") REFERENCES " +
                                    StoreModel.manager.getTableName() + "(" + StoreModel.COLUMN_STORE_ID + ")" +
                                    " ON DELETE CASCADE" +
                            ")"
            );
        }

        @Override
        public void dropTable(SQLiteDatabase db) {
            db.execSQL("DROP TABLE IF EXISTS " + this.getTableName());
        }

    }


    AbstractPriceModel(Context context, AbstractManager<M> manager) {
        super(context, manager);
        this.priceId = -1;
        this.time = new Date();
        this.foreignStoreId = -1;
        this.foreignStoreId = -1;
    }

    public long priceId;
    public double price;
    public String type;
    public double priceData0;
    public double priceData1;
    public Date time;
    public long foreignProductId;
    public long foreignStoreId;


    public ProductModel getProduct() {
        if (this.foreignProductId < 0) {
            return null;
        }

        String tableName = ProductModel.manager.getTableName();
        String selection = ProductModel.COLUMN_PRODUCT_ID + " = ?";
        String[] selectionArgs = { Long.toString(this.foreignProductId) };
        String limit = "1";
        Cursor cursor = this.getDatabase().query(
                tableName,
                null, /* read all data */
                selection,
                selectionArgs,
                null,
                null,
                null,
                limit
        );
        ProductModel model = null;
        if (cursor.moveToFirst()) {
            model = ProductModel.manager.get(this.mContext, cursor);
        }
        cursor.close();
        return model;
    }


    public StoreModel getStore() {
        if (this.foreignStoreId < 0) {
            return null;
        }

        String tableName = StoreModel.manager.getTableName();
        String selection = StoreModel.COLUMN_STORE_ID + " = ?";
        String[] selectionArgs = { Long.toString(this.foreignStoreId) };
        String limit = "1";
        Cursor cursor = this.getDatabase().query(
                tableName,
                null, /* read all data */
                selection,
                selectionArgs,
                null,
                null,
                null,
                limit
        );
        StoreModel model = null;
        if (cursor.moveToFirst()) {
            model = StoreModel.manager.get(this.mContext, cursor);
        }
        cursor.close();
        return model;
    }


    @Override
    protected long getIdValue() {
        return this.priceId;
    }

    @Override
    protected void setIdValue(long idValue) {
        this.priceId = idValue;
    }

    @Override
    protected void onSave(ContentValues values) {
        super.onSave(values);
        values.put(COLUMN_TYPE, this.type);
        values.put(COLUMN_PRICE, this.price);
        values.put(COLUMN_PRICE_DATA_0, this.priceData0);
        values.put(COLUMN_PRICE_DATA_1, this.priceData1);
        values.put(COLUMN_TIME, this.time.getTime());
        values.put(COLUMN_FOREIGN_PRODUCT_ID, this.foreignProductId);
        values.put(COLUMN_FOREIGN_STORE_ID, this.foreignStoreId);
    }

    @Override
    public void readFromCursor(Cursor cursor) {
        super.readFromCursor(cursor);
        this.price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE));
        this.type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE));
        this.priceData0 = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE_DATA_0));
        this.priceData1 = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE_DATA_1));
        this.time = new Date(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TIME)));
        this.foreignProductId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_FOREIGN_PRODUCT_ID));
        this.foreignStoreId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_FOREIGN_STORE_ID));
    }

}
