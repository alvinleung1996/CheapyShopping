package com.alvin.cheapyshopping.viewmodels;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.content.Context;
import android.os.AsyncTask;

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


    /*
    ************************************************************************************************
    * Repository
    ************************************************************************************************
     */

    private ShoppingListProductRelationRepository mListProductRepository;
    private ShoppingListProductRelationRepository getListProductRepository() {
        if (this.mListProductRepository == null) {
            this.mListProductRepository = ShoppingListProductRelationRepository.getInstance(this.getApplication());
        }
        return this.mListProductRepository;
    }

    public void addShoppingListProduct(long shoppingListId, List<Long> productIds) {
        new ShoppingListProductAdder(this.getApplication(), shoppingListId, productIds).execute();
    }

    private static class ShoppingListProductAdder extends AsyncTask<Void, Void, long[]> {

        @SuppressLint("StaticFieldLeak")
        private final Context mContext;
        private final long mShoppingListId;
        private final List<Long> mProductIds;

        private ShoppingListProductAdder(Context context, long shoppingListId, List<Long> productIds) {
            this.mContext = context.getApplicationContext();
            this.mShoppingListId = shoppingListId;
            this.mProductIds = productIds;
        }

        @Override
        protected long[] doInBackground(Void... voids) {
            List<ShoppingListProductRelation> listProducts = new ArrayList<>(this.mProductIds.size());
            for (long productId : mProductIds) {
                ShoppingListProductRelation relation = new ShoppingListProductRelation();
                relation.setForeignShoppingListId(mShoppingListId);
                relation.setForeignProductId(productId);
                listProducts.add(relation);
            }
            return ShoppingListProductRelationRepository.getInstance(this.mContext)
                    .insertShoppingListProductRelation(listProducts
                            .toArray(new ShoppingListProductRelation[listProducts.size()]));
        }
    }

}
