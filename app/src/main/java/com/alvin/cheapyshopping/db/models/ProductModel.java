package com.alvin.cheapyshopping.db.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.alvin.cheapyshopping.db.AbstractModel;

/**
 * Created by Alvin on 5/11/2017.
 */

public class ProductModel extends AbstractProductModel<ProductModel> {

    public static final AbstractManager<ProductModel> manager = new AbstractManager<ProductModel>() {
        @Override
        public ProductModel newModelInstance(Context context) {
            return new ProductModel(context);
        }
    };

    public ProductModel(Context context) {
        super(context, manager);
    }

}

abstract class AbstractProductModel<SELF extends AbstractProductModel<SELF>> extends AbstractModel<SELF> {

    public static final String COLUMN_PRODUCT_ID = "product_id";
    public static final String COLUMN_NAME = "name";

    public static abstract class AbstractManager<M extends AbstractProductModel<M>> extends AbstractModel.AbstractManager<M> {

        @Override
        public String getTableName() {
            return "Product";
        }

        @Override
        public String getIdColumnName() {
            return COLUMN_PRODUCT_ID;
        }

        @Override
        public void createTable(SQLiteDatabase db) {
            db.execSQL(
                    "CREATE TABLE " + this.getTableName() + " (" +
                            COLUMN_PRODUCT_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE," +
                            COLUMN_NAME + " TEXT NOT NULL" +
                    ")"
            );
        }

        @Override
        public void dropTable(SQLiteDatabase db) {
            db.execSQL("DROP TABLE IF EXISTS " + this.getTableName());
        }

        public String[] getColumnNames() {
            return new String[] {
                    COLUMN_PRODUCT_ID,
                    COLUMN_NAME
            };
        }

        public String[] getPrefixedColumnNames() {
            String[] columns = this.getColumnNames();
            for (int i = 0; i < columns.length; ++i) {
                columns[i] = this.getTableName() + "." + columns[i];
            }
            return columns;
        }

    };

    public AbstractProductModel(Context context, AbstractManager<SELF> manager) {
        super(context, manager);
        this.productId = -1;
    }

    public long productId;
    public String name;


    @Override
    protected long getIdValue() {
        return this.productId;
    }

    @Override
    protected void setIdValue(long idValue) {
        this.productId = idValue;
    }


    @Override
    protected void onSave(ContentValues values) {
        super.onSave(values);
        values.put(COLUMN_NAME, this.name);
    }

    @Override
    public void readFromCursor(Cursor cursor) {
        super.readFromCursor(cursor);
        this.name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
    }

}
