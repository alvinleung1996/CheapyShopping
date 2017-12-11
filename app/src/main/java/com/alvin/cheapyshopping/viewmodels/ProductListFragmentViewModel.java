package com.alvin.cheapyshopping.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.alvin.cheapyshopping.repositories.ProductRepository;
import com.alvin.cheapyshopping.db.entities.Product;

import java.util.List;

/**
 * Created by Alvin on 19/11/2017.
 */

public class ProductListFragmentViewModel extends AndroidViewModel {

    public ProductListFragmentViewModel(Application application) {
        super(application);
    }


    /*
    ************************************************************************************************
    * Repository
    ************************************************************************************************
     */

    private ProductRepository mProductRepository;
    private ProductRepository getProductRepository() {
        if (this.mProductRepository == null) {
            this.mProductRepository = ProductRepository.getInstance(this.getApplication());
        }
        return this.mProductRepository;
    }


    /*
    ************************************************************************************************
    * get all products
    ************************************************************************************************
     */

    private LiveData<List<Product>> mAllProductsCache;
    public LiveData<List<Product>> getAllProducts() {
        if (this.mAllProductsCache == null) {
            this.mAllProductsCache = this.getProductRepository().getAllProducts();
        }
        return this.mAllProductsCache;
    }

}
