package com.alvin.cheapyshopping.olddb.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.alvin.cheapyshopping.olddb.AbstractModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Alvin on 7/11/2017.
 */

public class ShoppingListModel extends AbstractShoppingListModel<ShoppingListModel> {

    public static final AbstractManager<ShoppingListModel> manager = new AbstractManager<ShoppingListModel>() {
        @Override
        public ShoppingListModel newModelInstance(Context context) {
            return new ShoppingListModel(context);
        }
    };

    public ShoppingListModel(Context context) {
        super(context, manager);
    }

}

abstract class AbstractShoppingListModel<M extends AbstractShoppingListModel<M>> extends AbstractModel<M> {

    public static final String COLUMN_SHOPPING_LIST_ID = "shopping_list_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_TIME = "time";

    public static abstract class AbstractManager<M extends AbstractShoppingListModel<M>> extends AbstractModel.AbstractManager<M> {

        @Override
        public String getTableName() {
            return "ShoppingList";
        }

        @Override
        public String getIdColumnName() {
            return COLUMN_SHOPPING_LIST_ID;
        }

        @Override
        public void createTable(SQLiteDatabase db) {
            db.execSQL(
                    "CREATE TABLE " + this.getTableName() + " (" +
                            COLUMN_SHOPPING_LIST_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE," +
                            COLUMN_NAME + " TEXT NOT NULL," +
                            COLUMN_TIME + " INTEGER NOT NULL" +
                    ")"
            );
            ShoppingListProductRelationModel.manager.createTable(db);
        }

        @Override
        public void dropTable(SQLiteDatabase db) {
            ShoppingListProductRelationModel.manager.dropTable(db);
            db.execSQL("DROP TABLE IF EXISTS " + this.getTableName());
        }

        public M getLatestShoppingList(Context context) {
            String orderBy = COLUMN_TIME + " DESC";
            String limit = "1";
            SQLiteDatabase db = getDatabase(context);
            Cursor cursor = db.query(
                    this.getTableName(),
                    null, /* read all data */
                    null,
                    null,
                    null,
                    null,
                    orderBy,
                    limit
            );
            M model = null;
            if (cursor.moveToFirst()) {
                model = this.get(context, cursor);
            }
            cursor.close();
            return model;
        }
    }

    public AbstractShoppingListModel(Context context, AbstractManager<M> manager) {
        super(context, manager);
        this.shoppingListId = -1;
        this.name = "Unnamed Shopping List";
        this.time = new Date();
    }

    public long shoppingListId;
    public String name;
    public Date time;


    public boolean addProduct(ProductModel product) {
        ShoppingListProductRelationModel model = new ShoppingListProductRelationModel(this.mContext);
        model.foreignShoppingListId = this.shoppingListId;
        model.foreignProductId = product.productId;
        return model.save();
    }


    @Override
    protected long getIdValue() {
        return this.shoppingListId;
    }

    @Override
    protected void setIdValue(long idValue) {
        this.shoppingListId = idValue;
    }

    public List<ProductModel> getProducts() {
        if (this.shoppingListId < 0) {
            return new ArrayList<>();
        }

        String[] selectionArgs = { Long.toString(this.shoppingListId) };
        Cursor cursor = this.getDatabase().rawQuery(
                "SELECT " + TextUtils.join(", ", ProductModel.manager.getPrefixedColumnNames()) +
                " FROM " + this.mManager.getTableName() + ", " + ShoppingListProductRelationModel.manager.getTableName() + ", " + ProductModel.manager.getTableName() +
                " WHERE " + this.mManager.getTableName() + "." + COLUMN_SHOPPING_LIST_ID + " = ?" +
                        " AND " + this.mManager.getTableName() + "." + COLUMN_SHOPPING_LIST_ID + " = " + ShoppingListProductRelationModel.manager.getTableName() + "." + ShoppingListProductRelationModel.COLUMN_FOREIGN_SHOPPING_LIST_ID +
                        " AND " + ShoppingListProductRelationModel.manager.getTableName() + "." + ShoppingListProductRelationModel.COLUMN_FOREIGN_PRODUCT_ID + " = " + ProductModel.manager.getTableName() + "." + ProductModel.COLUMN_PRODUCT_ID,
                selectionArgs
        );

        List<ProductModel> products = new ArrayList<>();
        while (cursor.moveToNext()) {
            ProductModel product = ProductModel.manager.get(this.mContext, cursor);
            products.add(product);
        }
        cursor.close();
        return products;
    }

    @Override
    protected void onSave(ContentValues values) {
        super.onSave(values);
        values.put(COLUMN_NAME, this.name);
        values.put(COLUMN_TIME, this.time.getTime());
    }

    @Override
    public void readFromCursor(Cursor cursor) {
        super.readFromCursor(cursor);
        this.name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
        this.time = new Date(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TIME)));
    }

}



class ShoppingListProductRelationModel extends AbstractShoppingListProductRelation<ShoppingListProductRelationModel> {

    public static final AbstractManager<ShoppingListProductRelationModel> manager = new AbstractManager<ShoppingListProductRelationModel>() {
        @Override
        public ShoppingListProductRelationModel newModelInstance(Context context) {
            return new ShoppingListProductRelationModel(context);
        }
    };

    public ShoppingListProductRelationModel(Context context) {
        super(context, manager);
    }

}

abstract class AbstractShoppingListProductRelation<M extends AbstractShoppingListProductRelation<M>> extends AbstractModel<M> {

    public static final String COLUMN_RELATION_ID = "relation_id";
    public static final String COLUMN_FOREIGN_SHOPPING_LIST_ID = "shopping_list_id";
    public static final String COLUMN_FOREIGN_PRODUCT_ID = "product_id";

    public static abstract class AbstractManager<M extends AbstractShoppingListProductRelation<M>> extends AbstractModel.AbstractManager<M> {

        @Override
        public String getTableName() {
            return "ShoppingListProductRelation";
        }

        @Override
        public String getIdColumnName() {
            return COLUMN_RELATION_ID;
        }

        @Override
        public void createTable(SQLiteDatabase db) {
            db.execSQL(
                    "CREATE TABLE " + this.getTableName() + " (" +
                            COLUMN_RELATION_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE," +

                            COLUMN_FOREIGN_SHOPPING_LIST_ID + " INTEGER NOT NULL," +
                            COLUMN_FOREIGN_PRODUCT_ID + " INTEGER NOT NULL," +

                            "FOREIGN KEY(" + COLUMN_FOREIGN_SHOPPING_LIST_ID + ") REFERENCES " +
                                    ShoppingListModel.manager.getTableName() + "(" + ShoppingListModel.COLUMN_SHOPPING_LIST_ID + ")" +
                                    " ON DELETE CASCADE," +
                            "FOREIGN KEY(" + COLUMN_FOREIGN_PRODUCT_ID + ") REFERENCES " +
                                    ProductModel.manager.getTableName() + "(" + ProductModel.COLUMN_PRODUCT_ID + ")" +
                                    " ON DELETE CASCADE," +

                            "UNIQUE(" + COLUMN_FOREIGN_SHOPPING_LIST_ID + ", " + COLUMN_FOREIGN_PRODUCT_ID + ")" +
                     ")"
            );
        }

        @Override
        public void dropTable(SQLiteDatabase db) {
            db.execSQL("DROP TABLE IF EXISTS " + this.getTableName());
        }
    }

    public AbstractShoppingListProductRelation(Context context, AbstractManager<M> manager) {
        super(context, manager);
        this.relationId = -1;
        this.foreignShoppingListId = -1;
        this.foreignProductId = -1;
    }


    public long relationId;
    public long foreignShoppingListId;
    public long foreignProductId;


    public ShoppingListModel getShoppingList() {
        String tableName = ShoppingListModel.manager.getTableName();
        String selection = ShoppingListModel.COLUMN_SHOPPING_LIST_ID + " = ?";
        String[] selectionArgs = { Long.toString(this.foreignShoppingListId) };
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
        ShoppingListModel model = null;
        if (cursor.moveToFirst()) {
            model = ShoppingListModel.manager.get(this.mContext, cursor);
        }
        cursor.close();
        return model;
    }

    public ProductModel getProduct() {
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


    @Override
    protected long getIdValue() {
        return this.relationId;
    }

    @Override
    protected void setIdValue(long idValue) {
        this.relationId = idValue;
    }

    @Override
    protected void onSave(ContentValues values) {
        super.onSave(values);
        values.put(COLUMN_FOREIGN_SHOPPING_LIST_ID, this.foreignShoppingListId);
        values.put(COLUMN_FOREIGN_PRODUCT_ID, this.foreignProductId);
    }

    @Override
    public void readFromCursor(Cursor cursor) {
        super.readFromCursor(cursor);
        this.foreignShoppingListId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_FOREIGN_SHOPPING_LIST_ID));
        this.foreignProductId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_FOREIGN_PRODUCT_ID));
    }

}
