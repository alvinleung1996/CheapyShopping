package com.alvin.cheapyshopping.viewmodels;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.util.ArrayMap;

import com.alvin.cheapyshopping.db.entities.ShoppingList;
import com.alvin.cheapyshopping.db.entities.ShoppingListProductRelation;
import com.alvin.cheapyshopping.repositories.ShoppingListProductRelationRepository;
import com.alvin.cheapyshopping.repositories.ShoppingListRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by cheng on 11/30/2017.
 */

public class ProductActivityViewModel extends AndroidViewModel{

    public ProductActivityViewModel (Application application){
        super(application);
    }

    /*
    ************************************************************************************************
    * Repository
    ************************************************************************************************
    */

    private ShoppingListRepository mShoppingListRepository;

    private  ShoppingListRepository getShoppingListRepository(){
        if (this.mShoppingListRepository == null){
            this.mShoppingListRepository = ShoppingListRepository.getInstance(this.getApplication());
        }
        return  mShoppingListRepository;
    }

    /*
    ************************************************************************************************
    * get Shopping Lists without Products
    ************************************************************************************************
     */


    private Map<String, LiveData<List<ShoppingList>>> mFilteredShoppingListsCache;

    public LiveData<List<ShoppingList>> findShoppingListsNotContainProduct(String productId) {
        if (this.mFilteredShoppingListsCache == null) {
            this.mFilteredShoppingListsCache = new ArrayMap<>();
        }
        if (!this.mFilteredShoppingListsCache.containsKey(productId)) {
            this.mFilteredShoppingListsCache.put(productId, this.getShoppingListRepository()
                    .findShoppingListsNotContainProduct(productId));
        }
        return this.mFilteredShoppingListsCache.get(productId);
    }





    public void findShoppingListsNotContainProductNow(String productId, Function<List<ShoppingList>, Void> callback){
        new findShoppingListsNotContainProductNowTask(this.getApplication(), productId, callback).execute();
    }

    private static class findShoppingListsNotContainProductNowTask extends AsyncTask<Void, Void, List<ShoppingList>>{
        @SuppressLint("StaticFieldLeak")
        private final Context mContext;
        private final String mProductIds;
        private final Function<List<ShoppingList>, Void> mCallback;

        private findShoppingListsNotContainProductNowTask(Context context,String productId, Function<List<ShoppingList>, Void> callback){
            mContext = context;
            mProductIds = productId;
            mCallback = callback;
        }

        @Override
        protected List<ShoppingList> doInBackground(Void... voids) {
            return ShoppingListRepository.getInstance(mContext).findShoppingListsNotContainProductNow(mProductIds);
        }

        @Override
        protected void onPostExecute(List<ShoppingList> shoppingLists) {
            super.onPostExecute(shoppingLists);
            if(mCallback != null){
                mCallback.apply(shoppingLists);
            }
        }
    }

    /*
    ************************************************************************************************
    * Add product to shopping list
    ************************************************************************************************
     */

    public void addProductToShoppingLists(String productId, List<String> shoppingListIds, int quantity) {
        new ProductToShoppingListAdder(this.getApplication(), productId, shoppingListIds, quantity).execute();
    }

    private static class ProductToShoppingListAdder extends AsyncTask<Void, Void, long[]> {

        @SuppressLint("StaticFieldLeak")
        private final Context mContext;
        private final String mProductIds;
        private final List<String> mShoppingListIds;
        private final int mQuantity;

        private ProductToShoppingListAdder(Context context, String productId, List<String> shoppingListIds, int quantity) {
            this.mContext = context.getApplicationContext();
            this.mShoppingListIds = shoppingListIds;
            this.mProductIds = productId;
            this.mQuantity = quantity;
        }

        @Override
        protected long[] doInBackground(Void... voids) {
            List<ShoppingListProductRelation> listShoppingLists = new ArrayList<>(this.mShoppingListIds.size());
            for (String shoppingListId : mShoppingListIds){
                ShoppingListProductRelation relation = new ShoppingListProductRelation();
                relation.setForeignProductId(mProductIds);
                relation.setForeignShoppingListId(shoppingListId);
                relation.setQuantity(this.mQuantity);
                listShoppingLists.add(relation);
            }
            return ShoppingListProductRelationRepository.getInstance(this.mContext)
                    .insertShoppingListProductRelation(listShoppingLists
                            .toArray(new ShoppingListProductRelation[listShoppingLists.size()]));

        }
    }

}
