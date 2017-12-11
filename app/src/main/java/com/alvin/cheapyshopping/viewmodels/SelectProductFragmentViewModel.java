package com.alvin.cheapyshopping.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.util.ArrayMap;

import com.alvin.cheapyshopping.repositories.ProductRepository;
import com.alvin.cheapyshopping.db.entities.Product;

import java.util.List;
import java.util.Map;

/**
 * Created by Alvin on 21/11/2017.
 */

public class SelectProductFragmentViewModel extends AndroidViewModel {

    public SelectProductFragmentViewModel(Application application) {
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
    * get products
    ************************************************************************************************
     */

    private LiveData<List<Product>> mAllProductsCache;
    public LiveData<List<Product>> getProducts() {
        if (this.mAllProductsCache == null) {
            this.mAllProductsCache = this.getProductRepository().getAllProducts();
        }
        return this.mAllProductsCache;
    }


    private Map<String, LiveData<List<Product>>> mFilteredProductsCache;
    public LiveData<List<Product>> findProductsNotInShoppingList(String shoppingListId) {
        if (this.mFilteredProductsCache == null) {
            this.mFilteredProductsCache = new ArrayMap<>();
        }
        if (!this.mFilteredProductsCache.containsKey(shoppingListId)) {
            this.mFilteredProductsCache.put(shoppingListId, this.getProductRepository()
                    .findProductsNotInShoppingList(shoppingListId));
        }
        return this.mFilteredProductsCache.get(shoppingListId);
        // Cached by the repository;
    }

}
