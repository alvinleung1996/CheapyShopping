package com.alvin.cheapyshopping.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;

import com.alvin.cheapyshopping.repositories.ShoppingListProductRepository;
import com.alvin.cheapyshopping.room.entities.ShoppingListProduct;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alvin on 21/11/2017.
 */

public class AddShoppingListProductActivityViewModel extends AndroidViewModel {

    public AddShoppingListProductActivityViewModel(Application application) {
        super(application);
    }

    private ShoppingListProductRepository mListProductRepository;
    private ShoppingListProductRepository getListProductRepository() {
        if (this.mListProductRepository == null) {
            this.mListProductRepository = ShoppingListProductRepository.getInstance(this.getApplication());
        }
        return this.mListProductRepository;
    }

    public long[] addShoppingListProduct(long shoppingListId, List<Long> productIds) {
        List<ShoppingListProduct> listProducts = new ArrayList<>(productIds.size());
        for (Long productId : productIds) {
            ShoppingListProduct relation = new ShoppingListProduct();
            relation.setForeignShoppingListId(shoppingListId);
            relation.setForeignProductId(productId);
            listProducts.add(relation);
        }
        return this.getListProductRepository().insert(listProducts.toArray(new ShoppingListProduct[listProducts.size()]));
    }

}
