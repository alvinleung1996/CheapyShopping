package com.alvin.cheapyshopping.olddb.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.alvin.cheapyshopping.olddb.AbstractModel;

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

abstract class AbstractProductModel<M extends AbstractProductModel<M>> extends AbstractModel<M> {

    public static final String COLUMN_PRODUCT_ID = "product_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_POPULAR = "popular";
    public static final String COLUMN_DESCRIPTION = "description";

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
                            COLUMN_NAME + " TEXT NOT NULL," +
                            COLUMN_POPULAR + " INTEGER DEFAULT 1," +
                            COLUMN_DESCRIPTION + " TEXT NOT NULL DEFAULT 'NO PRODUCT DESCRIPTION AVAILABLE.'" +
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
                    COLUMN_NAME,
                    COLUMN_POPULAR,
                    COLUMN_DESCRIPTION
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

    public AbstractProductModel(Context context, AbstractManager<M> manager) {
        super(context, manager);
        this.productId = -1;
        this.popular = 0;
        this.description = "NO PRODUCT DESCRIPTION AVAILABLE.";
    }

    public long productId;
    public String name;
    public int popular;
    public String description;


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
        values.put(COLUMN_POPULAR, this.popular);
        values.put(COLUMN_DESCRIPTION, this.description);
    }

    @Override
    public void readFromCursor(Cursor cursor) {
        super.readFromCursor(cursor);
        this.name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
        this.popular = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_POPULAR));
        this.description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
    }

}
