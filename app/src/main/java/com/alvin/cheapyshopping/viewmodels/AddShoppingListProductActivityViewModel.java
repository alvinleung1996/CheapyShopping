package com.alvin.cheapyshopping.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;

import com.alvin.cheapyshopping.repositories.ShoppingListProductRelationRepository;
import com.alvin.cheapyshopping.db.entities.ShoppingListProductRelation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alvin on 21/11/2017.
 */

public class AddShoppingListProductActivityViewModel extends AndroidViewModel {

    public AddShoppingListProductActivityViewModel(Application application) {
        super(application);
    }

    private ShoppingListProductRelationRepository mListProductRepository;
    private ShoppingListProductRelationRepository getListProductRepository() {
        if (this.mListProductRepository == null) {
            this.mListProductRepository = ShoppingListProductRelationRepository.getInstance(this.getApplication());
        }
        return this.mListProductRepository;
    }

    public long[] addShoppingListProduct(long shoppingListId, List<Long> productIds) {
        List<ShoppingListProductRelation> listProducts = new ArrayList<>(productIds.size());
        for (Long productId : productIds) {
            ShoppingListProductRelation relation = new ShoppingListProductRelation();
            relation.setForeignShoppingListId(shoppingListId);
            relation.setForeignProductId(productId);
            listProducts.add(relation);
        }
        return this.getListProductRepository().insertShoppingListProductRelation(listProducts.toArray(new ShoppingListProductRelation[listProducts.size()]));
    }

}
