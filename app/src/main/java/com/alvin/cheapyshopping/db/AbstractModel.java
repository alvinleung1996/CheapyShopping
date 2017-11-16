package com.alvin.cheapyshopping.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
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

public abstract class AbstractModel<M extends AbstractModel<M>> {

    public abstract static class AbstractManager<M extends AbstractModel<M>> {

        /**
         * Store and cache all model instance for this model.
         * Only model which has its id value (saved to the database)
         * will be stored here.
         */
        public Map<Long, M> modelCache = new TreeMap<>();

        /**
         * Get the database instance.
         *
         * @param context The context object used by the database
         * @return the database instance
         */
        public SQLiteDatabase getDatabase(Context context) {
            return DatabaseHelper.getInstance(context).getDatabase();
        }

        /**
         * Get the table name for this model
         * The table name will be used by other part of the model
         * @return the table name for this model
         */
        public abstract String getTableName();

        /**
         * Get the column name for the id column.
         * For simplicity, it only support single id column (single primary key)
         * @return the column name for the id column
         */
        public abstract String getIdColumnName();

        /**
         * Create the table in the given database.
         * It is used to create the table for this model,
         * or any other related table e.g. relation tables
         * in the database.
         * @param db the database to create the table
         */
        public void createTable(SQLiteDatabase db) {}

        /**
         * Drop the table in the given database.
         * It is used to drop the table for this model,
         * or any other related table e.g. relation tables
         * in the database.
         * @param db the database to drop the table
         */
        public void dropTable(SQLiteDatabase db) {}

        /**
         * Create a new instance of the model.
         * This method should be overridden by the concrete class
         * i.e. non-abstract class to return an instance of the concrete model class.
         * @param context The application context, which may be used during database operation
         * @return An instance of the concrete class
         */
        public abstract M newModelInstance(Context context);

        /**
         * Get an instance of the model which matched the id value stored in the cursor.<br>
         * <br>
         * If the model instance with the given id has already been cached in <code>mdoelCache</code>,
         * the cached instance will be returned with its data (fields) updated with the value in the cursor.<br>
         * <br>
         * If the model instance with the given id has not been cached,
         * a new model instance will be created, store in <code>modelCache</code>,
         * and fill with data provided by the cursor.<br>
         * <br>
         * Note: The cursor must contain all model data,
         * partial data will result in a partial initialized model instance.
         * The column name must in the cursor must match the field name provided by the model class,
         * otherwise exception will be thrown.
         *
         * @param context the context object required by the model class
         * @param cursor the cursor object containing data for the model
         * @return the model instance updated with the cursor data
         */
        public M get(Context context, Cursor cursor) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(this.getIdColumnName()));
            M model;
            if (this.modelCache.containsKey(id)) {
                model = this.modelCache.get(id);
            } else {
                model = this.newModelInstance(context);
            }
            model.readFromCursor(cursor);
            this.modelCache.put(id, model);
            return model;
        }

        /**
         * Get an instance of the model which matched the given id value.<br>
         *
         * @param context the context object required by the model class
         * @param id the id of the model record
         * @return the model instance with the given id
         */
        public M get(Context context, long id) {
            if (this.modelCache.containsKey(id)) {
                return this.modelCache.get(id);
            }

            String selection = this.getIdColumnName() + " = ?";
            String[] selectionArgs = { Long.toString(id) };
            Cursor cursor = this.getDatabase(context).query(
                    this.getTableName(),
                    null,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null,
                    "1"
            );
            if (cursor.moveToFirst()) {
                M model = this.get(context, cursor);
                cursor.close();
                return model;

            } else {
                throw new RuntimeException("id {" + Long.toString(id) + "} does not exist!");
            }
        }

        /**
         * Get all record of the model as list of model instance.
         *
         * @param context the context object required by the model class
         * @return a list containing all model instances
         */
        public List<M> getAll(Context context) {
            Cursor cursor = this.getDatabase(context).query(
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
                M model = this.get(context, cursor);
                products.add(model);
            }
            cursor.close();
            return products;
        }

    }


    protected AbstractModel(Context context, AbstractManager<M> manager) {
        this.mContext = context;
        this.mManager = manager;
    }


    protected final Context mContext;
    protected final AbstractManager<M> mManager;


    /**
     * A proxy method which invoke <code>this.mManager.getDatabase(Context)</code>.
     * This method make it convenience to get the database instance which the need to pass the context object.
     *
     * @return the databse instance
     */
    protected SQLiteDatabase getDatabase() {
        return this.mManager.getDatabase(this.mContext);
    }

    /**
     * Get the id value of this model.
     * The id value will be used by other method to use to identify it from the database.
     *
     * @return the value of the id of this model
     */
    protected abstract long getIdValue();

    /**
     * Set the id value of this model.
     * Other method will use this method to update the model id value
     * e.g. Creating from cursor instance
     *
     * @param idValue the value of the id
     */
    protected abstract void setIdValue(long idValue);

    /**
     * Save the model data to the database.
     *
     * @return true if success, false otherwise
     */
    public boolean save() {
        try {
            this.saveOrThrow();
            return true;
        } catch (RuntimeException e) {
            Log.e("sqlite", "save exception", e);
            return false;
        }
    }

    /**
     * Save the model data to the database.
     * If the save failed, a runtime exception will be thrown.
     */
    public void saveOrThrow() {
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
            throw new RuntimeException("Cannot upadte database record");

        } else {
            // New record
            values.remove(this.mManager.getIdColumnName());
            long rowId = db.insertOrThrow(this.mManager.getTableName(), null, values);
            if (rowId >= 0) {
                this.setIdValue(rowId);
                // TODO: cast safe?
                this.mManager.modelCache.put(rowId, (M) this);
            }
        }
    }



    /**
     * Refresh the model data from the database.
     *
     * @return true if the refresh success, false otherwise
     */
    public boolean refresh() {
        try {
            this.refreshOrThrow();
            return true;
        } catch (RuntimeException e) {
            Log.e("sqlite", "refresh exception", e);
            return false;
        }
    }

    /**
     * Refresh the model data from the database.
     * If the refresh failed, a runtime exception will be thrown.
     */
    public void refreshOrThrow() {
        // The model has not been saved to the database, and nothing can be done
        if (this.getIdValue() < 0) {
            throw new RuntimeException("Cannot refresh from database due to invalid id " + this.getIdValue());
        }

        String selection = this.mManager.getIdColumnName() + " = ?";
        String[] selectionArgs = { Long.toString(this.getIdValue()) };
        String limit = "1";
        Cursor cursor = this.getDatabase().query(
                this.mManager.getTableName(),
                null, /* read all data */
                selection,
                selectionArgs,
                null,
                null,
                null,
                limit
        );
        try {
            if (cursor.moveToFirst()) {
                this.readFromCursor(cursor);
            } else {
                throw new RuntimeException("Cannot find any record of id" + this.getIdValue());
            }
        } finally {
            cursor.close();
        }
    }


    /**
     * Delete the database record from the database
     *
     * @return true if the deletion success, false otherwise
     */
    public boolean delete() {
        try {
            this.deleteOrThrow();
            return true;
        } catch (RuntimeException e) {
            Log.e("sqlite", "delete exception", e);
            return false;
        }
    }

    /**
     * Delete the database record from the database
     * If the delete failed, a runtime exception will be thrown.
     */
    public void deleteOrThrow() {
        // The model has not been saved to the database, and nothing can be done
        if (this.getIdValue() < 0) {
            throw new RuntimeException("Cannot delete from database due to invalid id " + this.getIdValue());
        }

        String selection = this.mManager.getIdColumnName() + " = ?";
        String[] selectionArgs = { Long.toString(this.getIdValue()) };
        int affected = this.getDatabase().delete(
                this.mManager.getTableName(),
                selection,
                selectionArgs
        );
        if (affected > 0) {
            this.mManager.modelCache.remove(this.getIdValue());
            this.setIdValue(-1);
        } else {
            throw new RuntimeException("Unable to delete from the database, maybe there is no record matching the model id " + this.getIdValue());
        }
    }


    /**
     * This method will be called when the model is saving to the database.
     * The default action is to just save the id value.
     * Subclasses can override this method in order to save more fields to the database.<br>
     * <br>
     * Note: Subclasses may omit to call <code>super.onSave(values)</code>,
     * but then it is the responsibility of the subclasses to put the id into the given <code>values</code>.
     * Failing to do so may yield undefined behavior.
     *
     * @param values the values to be saved to the database
     */
    protected void onSave(ContentValues values) {
        values.put(this.mManager.getIdColumnName(), this.getIdValue());
    }

    /**
     * Update the model data with the value in the cursor.
     * The default action is to just retrieve the id value.
     * Subclasses can override this method in order to retrieve more fields from the cursor.<br>
     * <br>
     * Note: The cursor must contain all model data,
     * partial data will result in a partial initialized model instance.
     * The column name must in the cursor must match the field name provided by the model class,
     * otherwise exception will be thrown.<br>
     * <br>
     * Note: Subclasses may omit to call <code>super.readFromCursor(cursor)</code>,
     * but then it is the responsibility of the subclasses to retrieve the id from the given <code>cursor</code>.
     * Failing to do so may yield undefined behavior.
     *
     * @param cursor the cursor data.
     */
    public void readFromCursor(Cursor cursor) {
        this.setIdValue(cursor.getLong(cursor.getColumnIndexOrThrow(this.mManager.getIdColumnName())));
    }

}
