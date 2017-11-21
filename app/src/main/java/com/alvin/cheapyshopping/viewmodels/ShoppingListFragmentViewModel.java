package com.alvin.cheapyshopping.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.alvin.cheapyshopping.repositories.ShoppingListRepository;
import com.alvin.cheapyshopping.room.daos.ShoppingListProductDao.ShoppingListProductDetail;
import com.alvin.cheapyshopping.room.entities.ShoppingList;
import com.alvin.cheapyshopping.room.entities.Store;

import java.util.List;
import java.util.Map;

/**
 * Created by Alvin on 20/11/2017.
 */

public class ShoppingListFragmentViewModel extends AndroidViewModel {

    private final ShoppingListRepository mShoppingListRepository;

    private Long mShoppingListId;


    public ShoppingListFragmentViewModel(Application application) {
        super(application);
        this.mShoppingListRepository = ShoppingListRepository.getInstance(application);
    }

    public void setShoppingListId(long shoppingListId) {
        this.mShoppingListId = shoppingListId;
    }

    public LiveData<List<ShoppingList>> getShoppingLists() {
        return this.mShoppingListRepository.getAll();
    }

    public LiveData<Map<Store, List<ShoppingListProductDetail>>> getResult() {
        return this.mShoppingListRepository.getCachedResultOfLatest();
    }

}
