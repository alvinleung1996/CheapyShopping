package com.alvin.cheapyshopping.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Alvin on 5/11/2017.
 *
 * Why is it so complicated? See the following articles:
 * https://www.sitepoint.com/self-types-with-javas-generics/
 * https://stackoverflow.com/questions/211143/java-enum-definition
 *
 */

public abstract class AbstractModel<SELF extends AbstractModel<SELF>> {

    public abstract static class AbstractManager<M extends AbstractModel<M>> {

        public Map<Long, M> modelMap = new TreeMap<>();

        public abstract String getTableName();

        public abstract String getIdColumnName();

        public void createTable(SQLiteDatabase db) {}

        public void dropTable(SQLiteDatabase db) {}

        public abstract M newModelInstance(Context context);

        public M getByCursor(Context context, Cursor cursor) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(this.getIdColumnName()));
            M model;
            if (this.modelMap.containsKey(id)) {
                model = this.modelMap.get(id);
            } else {
                model = this.newModelInstance(context);
            }
            model.readFromCursor(cursor);
            this.modelMap.put(id, model);
            return model;
        }

        public M getById(Context context, long id) {
            return this.modelMap.get(id);
        }

        public List<M> getAll(Context context) {
            Cursor cursor = getDatabase(context).query(
                    this.getTableName(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );
            List<M> products = new ArrayList<>(cursor.getCount());
            while (cursor.moveToNext()) {
                M model = this.newModelInstance(context);
                model.readFromCursor(cursor);
                products.add(model);
            }
            return products;
        }

    }

    protected static SQLiteDatabase getDatabase(Context context) {
        return DatabaseHelper.getInstance(context).getDatabase();
    }


    protected AbstractModel(Context context, AbstractManager<SELF> manager) {
        this.mContext = context;
        this.mManager = manager;
    }


    protected final Context mContext;
    protected final AbstractManager<SELF> mManager;


    protected SQLiteDatabase getDatabase() {
        return getDatabase(this.mContext);
    }


    protected abstract long getIdValue();
    protected abstract void setIdValue(long idValue);


    public boolean save() {
        boolean sucess = false;

        ContentValues values = new ContentValues();
        this.onSave(values);

        SQLiteDatabase db = this.getDatabase();
        if (this.getIdValue() >= 0) {
            // Existing record
            String selection = this.mManager.getIdColumnName() + " = ?";
            String[] selectionArgs = { Long.toString(this.getIdValue()) };
            int affected = db.update(
                    this.mManager.getTableName(),
                    values,
                    selection,
                    selectionArgs
            );
            sucess = affected > 0;
        } else {
            // New record
            values.remove(this.mManager.getIdColumnName());
            try {
                long rowId = db.insertOrThrow(this.mManager.getTableName(), null, values);
                if (rowId >= 0) {
                    this.setIdValue(rowId);
                    // TODO: safe?
                    this.mManager.modelMap.put(rowId, (SELF) this);
                    sucess = true;
                }
            } catch (SQLException e) {
                Log.e("sqlite", "Shopping List Product Relation save fail", e);
                sucess = false;
            }
        }
        return sucess;
    }

    public void saveOrThrow() {
        if (!this.save()) {
            throw new RuntimeException("Save fail!");
        }
    }

    public boolean refresh() {
        if (this.getIdValue() < 0) {
            return false;
        }

        String selection = this.mManager.getIdColumnName() + " = ?";
        String[] selectionArgs = { Long.toString(this.getIdValue()) };
        String limit = "1";
        SQLiteDatabase db = getDatabase(mContext);
        Cursor cursor = db.query(
                this.mManager.getTableName(),
                null, /* read all data */
                selection,
                selectionArgs,
                null,
                null,
                null,
                limit
        );
        boolean success = false;
        if (cursor.moveToFirst()) {
            this.readFromCursor(cursor);
            success = true;
        }
        cursor.close();
        return success;
    }

    public boolean delete() {
        String selection = this.mManager.getIdColumnName() + " = ?";
        String[] selectionArgs = { Long.toString(this.getIdValue()) };
        SQLiteDatabase db = this.getDatabase();
        int affected = db.delete(
                this.mManager.getTableName(),
                selection,
                selectionArgs
        );
        boolean success = affected > 0;
        if (success) {
            this.mManager.modelMap.remove(this.getIdValue());
            this.setIdValue(-1);
        }
        return success;
    }


    protected void onSave(ContentValues values) {
        values.put(this.mManager.getIdColumnName(), this.getIdValue());
    }

    public void readFromCursor(Cursor cursor) {
        this.setIdValue(cursor.getLong(cursor.getColumnIndexOrThrow(this.mManager.getIdColumnName())));
    }

}
