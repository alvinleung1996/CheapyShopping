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

public class AddShoppingListProductRelationActivityViewModel extends AndroidViewModel {

    public AddShoppingListProductRelationActivityViewModel(Application application) {
        super(application);
    }




    public void addShoppingListProduct(String shoppingListId, List<String> productIds, int quantity) {
        new ShoppingListProductAdder(this.getApplication(), shoppingListId, productIds, quantity).execute();
    }

    private static class ShoppingListProductAdder extends AsyncTask<Void, Void, long[]> {

        @SuppressLint("StaticFieldLeak")
        private final Context mContext;
        private final String mShoppingListId;
        private final List<String> mProductIds;
        private final int mQuantity;

        private ShoppingListProductAdder(Context context, String shoppingListId, List<String> productIds, int quantity) {
            this.mContext = context.getApplicationContext();
            this.mShoppingListId = shoppingListId;
            this.mProductIds = productIds;
            this.mQuantity = quantity;
        }

        @Override
        protected long[] doInBackground(Void... voids) {
            List<ShoppingListProductRelation> listProducts = new ArrayList<>(this.mProductIds.size());
            for (String productId : mProductIds) {
                ShoppingListProductRelation relation = new ShoppingListProductRelation();
                relation.setForeignShoppingListId(mShoppingListId);
                relation.setForeignProductId(productId);
                relation.setQuantity(this.mQuantity);
                listProducts.add(relation);
            }
            return ShoppingListProductRelationRepository.getInstance(this.mContext)
                    .insertShoppingListProductRelation(listProducts
                            .toArray(new ShoppingListProductRelation[listProducts.size()]));
        }
    }

}
