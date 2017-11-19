package com.alvin.cheapyshopping.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.alvin.cheapyshopping.repositories.ProductRepository;
import com.alvin.cheapyshopping.room.entities.Product;

import java.util.List;

/**
 * Created by Alvin on 19/11/2017.
 */

public class ProductListFragmentViewModel extends AndroidViewModel {

    private final ProductRepository mProductRepository;

    private LiveData<List<Product>> mLiveProducts;

    public ProductListFragmentViewModel(Application application) {
        super(application);
        this.mProductRepository = ProductRepository.getInstance(application);
    }

    public LiveData<List<Product>> getLiveProducts() {
        if (this.mLiveProducts == null) {
            this.mLiveProducts = this.mProductRepository.getProducts();
        }
        return this.mLiveProducts;
    }

}
