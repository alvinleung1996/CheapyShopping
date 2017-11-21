package com.alvin.cheapyshopping.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.alvin.cheapyshopping.room.AppDatabase;
import com.alvin.cheapyshopping.room.daos.ProductDao;
import com.alvin.cheapyshopping.room.entities.Product;

import java.util.List;

/**
 * Created by Alvin on 21/11/2017.
 */

public class SelectProductFragmentViewModel extends AndroidViewModel {


    private final ProductDao mProductDao;


    public SelectProductFragmentViewModel(Application application) {
        super(application);
        this.mProductDao = AppDatabase.getInstance(application).getProductDao();
    }


    public LiveData<List<Product>> getProducts() {
        // Cached by the repository
        return this.mProductDao.getAll();
    }

    public LiveData<List<Product>> getProductsNotInShoppingList(long shoppingListId) {
        // Cached by the repository
        return this.mProductDao.getAllNotInShoppingList(shoppingListId);
    }

}
