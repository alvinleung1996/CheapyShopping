package com.alvin.cheapyshopping.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;

import com.alvin.cheapyshopping.repositories.ProductRepository;
import com.alvin.cheapyshopping.room.entities.Product;

/**
 * Created by Alvin on 19/11/2017.
 */

public class AddProductFragmentViewModel extends AndroidViewModel {

    private final ProductRepository mProductRepository;

    public AddProductFragmentViewModel(Application application) {
        super(application);
        this.mProductRepository = ProductRepository.getInstance(application);
    }

    public long addProduct(String name, String description) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        return this.mProductRepository.insertAll(product)[0];
    }

}
