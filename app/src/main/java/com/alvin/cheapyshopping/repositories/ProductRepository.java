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


    private Map<String, LiveData<List<Product>>> mProductsNotInShoppingListCache;
    public LiveData<List<Product>> findProductsNotInShoppingList(String shoppingListId) {
        if (this.mProductsNotInShoppingListCache == null) {
            this.mProductsNotInShoppingListCache = new ArrayMap<>();
        }
        if (!this.mProductsNotInShoppingListCache.containsKey(shoppingListId)) {
            this.mProductsNotInShoppingListCache
                    .put(shoppingListId, this.getProductDao()
                            .findProductsNotInShoppingList(shoppingListId));
        }
        return this.mProductsNotInShoppingListCache.get(shoppingListId);
    }


    private Map<String, LiveData<List<Product>>> mShoppingListProductsCache;
    LiveData<List<Product>> findShoppingListProducts(String shoppingListId) {
        if (this.mShoppingListProductsCache == null) {
            this.mShoppingListProductsCache = new ArrayMap<>();
        }
        if (!this.mShoppingListProductsCache.containsKey(shoppingListId)) {
            this.mShoppingListProductsCache.put(shoppingListId, this.getProductDao()
                    .findShoppingListProducts(shoppingListId));
        }
        return this.mShoppingListProductsCache.get(shoppingListId);
    }


    private Map<String, LiveData<Product>> mProductCache;
    public LiveData<Product> findProductByProductId(String productId) {
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


    public List<Product> findProductsNotInShoppingListNow(String shoppingListId) {
        return this.getProductDao().findProductsNotInShoppingListNow(shoppingListId);
    }


    List<Product> findShoppingListProductsNow(String shoppingListId) {
        return this.getProductDao().findShoppingListProductsNow(shoppingListId);
    }


    public Product findProductByProductIdNow(String productId) {
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
