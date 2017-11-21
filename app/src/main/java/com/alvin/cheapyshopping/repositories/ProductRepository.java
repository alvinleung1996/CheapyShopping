package com.alvin.cheapyshopping.repositories;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.util.ArrayMap;

import com.alvin.cheapyshopping.room.AppDatabase;
import com.alvin.cheapyshopping.room.daos.ProductDao;
import com.alvin.cheapyshopping.room.entities.Product;

import java.util.List;
import java.util.Map;

/**
 * Created by Alvin on 19/11/2017.
 */

public class ProductRepository {

    private static ProductRepository sInstance;

    public static ProductRepository getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new ProductRepository(AppDatabase.getInstance(context).getProductDao());
        }
        return sInstance;
    }


    private final ProductDao mDao;

    private LiveData<List<Product>> mAll;
    private Map<Long, LiveData<List<Product>>> mAllNotOfShoppingListCache;
    private final Map<Long, LiveData<Product>> mCache;


    private ProductRepository(ProductDao dao) {
        this.mDao = dao;
        this.mAllNotOfShoppingListCache = new ArrayMap<>();
        this.mCache = new ArrayMap<>();
    }

    public LiveData<List<Product>> getAll() {
        if (this.mAll == null) {
            this.mAll = this.mDao.getAll();
        }
        return this.mAll;
    }

    public LiveData<List<Product>> getAllNotInShoppingList(long shoppingListId) {
        if (!this.mAllNotOfShoppingListCache.containsKey(shoppingListId)) {
            this.mAllNotOfShoppingListCache.put(shoppingListId, this.mDao.getAllNotInShoppingList(shoppingListId));
        }
        return this.mAllNotOfShoppingListCache.get(shoppingListId);
    }

    public LiveData<Product> findById(long id) {
        if (!this.mCache.containsKey(id)) {
            this.mCache.put(id, this.mDao.findById(id));
        }
        return this.mCache.get(id);
    }

    public long[] insert(Product... products) {
        return this.mDao.insert(products);
    }
    
    public int update(Product... products) {
        return this.mDao.update(products);
    }

    public int delete(Product... products) {
        return this.mDao.delete(products);
    }

}
