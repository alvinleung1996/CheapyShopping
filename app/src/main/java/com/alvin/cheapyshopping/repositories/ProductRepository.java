package com.alvin.cheapyshopping.repositories;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.util.ArrayMap;

import com.alvin.cheapyshopping.db.AppDatabase;
import com.alvin.cheapyshopping.db.daos.ProductDao;
import com.alvin.cheapyshopping.db.entities.Product;

import java.util.List;
import java.util.Map;

/**
 * Created by Alvin on 19/11/2017.
 */

public class ProductRepository {

    @SuppressLint("StaticFieldLeak")
    private static ProductRepository sInstance;

    public static ProductRepository getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new ProductRepository(context);
        }
        return sInstance;
    }

    private final Context mContext;


    private ProductRepository(Context context) {
        this.mContext = context.getApplicationContext();
    }

    /*
    ************************************************************************************************
    * Dao
    ************************************************************************************************
     */

    private ProductDao mProductDao;
    private ProductDao getProductDao() {
        if (this.mProductDao == null) {
            this.mProductDao = AppDatabase.getInstance(this.mContext).getProductDao();
        }
        return this.mProductDao;
    }


    /*
    ************************************************************************************************
    * Query, Async
    ************************************************************************************************
     */

    private LiveData<List<Product>> mAllProductsCache;
    public LiveData<List<Product>> getAllProducts() {
        if (this.mAllProductsCache == null) {
            this.mAllProductsCache = this.getProductDao().getAllProducts();
        }
        return this.mAllProductsCache;
    }


    private Map<Long, LiveData<List<Product>>> mAllProductsNotInShoppingListCache;
    public LiveData<List<Product>> findAllProductsNotInShoppingList(long shoppingListId) {
        if (this.mAllProductsNotInShoppingListCache == null) {
            this.mAllProductsNotInShoppingListCache = new ArrayMap<>();
        }
        if (!this.mAllProductsNotInShoppingListCache.containsKey(shoppingListId)) {
            this.mAllProductsNotInShoppingListCache
                    .put(shoppingListId, this.getProductDao()
                            .findAllProductsNotInShoppingList(shoppingListId));
        }
        return this.mAllProductsNotInShoppingListCache.get(shoppingListId);
    }

    private Map<Long, LiveData<Product>> mProductCache;
    public LiveData<Product> findProductByProductId(long productId) {
        if (this.mProductCache == null) {
            this.mProductCache = new ArrayMap<>();
        }
        if (!this.mProductCache.containsKey(productId)) {
            this.mProductCache.put(productId, this.getProductDao()
                    .findProductByProductId(productId));
        }
        return this.mProductCache.get(productId);
    }

    /*
    ************************************************************************************************
    * Query, Sync
    ************************************************************************************************
     */

    public List<Product> getAllProductsNow() {
        return this.getProductDao().getAllProductsNow();
    }

    public List<Product> findAllProductsNotInShoppingListNow(long shoppingListId) {
        return this.getProductDao().findAllProductsNotInShoppingListNow(shoppingListId);
    }

    public Product findProductByProductIdNow(long productId) {
        return this.getProductDao().findProductByProductIdNow(productId);
    }


    /*
    ************************************************************************************************
    * Other
    ************************************************************************************************
     */

    public long[] insertProduct(Product... products) {
        return this.getProductDao().insertProduct(products);
    }
    
    public int updateProduct(Product... products) {
        return this.getProductDao().updateProduct(products);
    }

    public int deleteProduct(Product... products) {
        return this.getProductDao().deleteProduct(products);
    }

}
