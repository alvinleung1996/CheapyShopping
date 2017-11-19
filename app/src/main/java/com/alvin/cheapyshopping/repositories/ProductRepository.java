package com.alvin.cheapyshopping.repositories;

import android.arch.lifecycle.LiveData;
import android.content.Context;

import com.alvin.cheapyshopping.room.AppDatabase;
import com.alvin.cheapyshopping.room.daos.ProductDao;
import com.alvin.cheapyshopping.room.entities.Product;

import java.util.List;

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


    private final ProductDao mProductDao;

    private LiveData<List<Product>> mProducts;



    private ProductRepository(ProductDao storeDao) {
        this.mProductDao = storeDao;
    }

    public LiveData<List<Product>> getProducts() {
        if (this.mProducts == null) {
            this.mProducts = this.mProductDao.getAll();
        }
        return this.mProducts;
    }

    public long[] insertAll(Product... stores) {
        return this.mProductDao.insertAll(stores);
    }

}
