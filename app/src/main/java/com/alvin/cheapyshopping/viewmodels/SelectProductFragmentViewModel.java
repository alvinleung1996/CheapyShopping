package com.alvin.cheapyshopping.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.alvin.cheapyshopping.repositories.ProductRepository;
import com.alvin.cheapyshopping.db.entities.Product;

import java.util.List;

/**
 * Created by Alvin on 21/11/2017.
 */

public class SelectProductFragmentViewModel extends AndroidViewModel {

    public SelectProductFragmentViewModel(Application application) {
        super(application);
    }

    private ProductRepository mProductRepository;
    private ProductRepository getProductRepository() {
        if (this.mProductRepository == null) {
            this.mProductRepository = ProductRepository.getInstance(this.getApplication());
        }
        return this.mProductRepository;
    }


    public LiveData<List<Product>> getProducts() {
        // Cached by the repository
        return this.getProductRepository().getAllProducts();
    }



    public LiveData<List<Product>> getProductsNotInShoppingList(long shoppingListId) {
        // Cached by the repository
        return this.getProductRepository().findAllProductsNotInShoppingList(shoppingListId);
    }

}
