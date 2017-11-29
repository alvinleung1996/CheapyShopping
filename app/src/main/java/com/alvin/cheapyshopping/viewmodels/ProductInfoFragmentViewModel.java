package com.alvin.cheapyshopping.viewmodels;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.util.ArrayMap;

import com.alvin.cheapyshopping.db.entities.Account;
import com.alvin.cheapyshopping.db.entities.Product;
import com.alvin.cheapyshopping.db.entities.ShoppingList;
import com.alvin.cheapyshopping.db.entities.ShoppingListProductRelation;
import com.alvin.cheapyshopping.db.entities.pseudo.StorePrice;
import com.alvin.cheapyshopping.repositories.AccountRepository;
import com.alvin.cheapyshopping.repositories.PriceRepository;
import com.alvin.cheapyshopping.repositories.ProductRepository;
import com.alvin.cheapyshopping.repositories.ShoppingListProductRelationRepository;
import com.alvin.cheapyshopping.repositories.ShoppingListRepository;
import com.alvin.cheapyshopping.repositories.StorePriceRepository;
import com.alvin.cheapyshopping.repositories.StoreRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by cheng on 11/25/2017.
 */

public class ProductInfoFragmentViewModel extends AndroidViewModel {

    public ProductInfoFragmentViewModel(Application application) {
        super(application);
    }

    /*
    ************************************************************************************************
    * Repository
    ************************************************************************************************
    */


    private ProductRepository mProductRepository;
    private StoreRepository mStoreRepository;
    private PriceRepository mPriceRepository;
    private StorePriceRepository mStorePriceRepository;
    private AccountRepository mAccountRepository;
    private ShoppingListRepository mShoppingListRepository;

    private ProductRepository getProductRepository() {
        if (this.mProductRepository == null) {
            this.mProductRepository = ProductRepository.getInstance(this.getApplication());
        }
        return mProductRepository;
    }

    private  AccountRepository getAccountRepository(){
        if (this.mAccountRepository == null){
            this.mAccountRepository = AccountRepository.getInstance(this.getApplication());
        }
        return mAccountRepository;
    }

    private  ShoppingListRepository getShoppingListRepository(){
        if (this.mShoppingListRepository == null){
            this.mShoppingListRepository = ShoppingListRepository.getInstance(this.getApplication());
        }
        return  mShoppingListRepository;
    }

    /*
    ************************************************************************************************
    * get product
    ************************************************************************************************
     */

    // Assume the fragment will not change the product id throughout its life time
    private LiveData<Product> mProduct;
    public LiveData<Product> getProduct(String productId) {
        if (this.mProduct == null) {
            this.mProduct = this.getProductRepository().findProductByProductId(productId);
        }
        return this.mProduct;
    }

    /*
    ************************************************************************************************
    * remove / add custom product image
    ************************************************************************************************
     */

    public void removeCustomProductImage(Product product){
        product.setImageExist(false);
        new UpdateProductImageTask(this.getApplication(), product).execute();
    }

    public void addCustomProductImage(Product product){
        product.setImageExist(true);
        new UpdateProductImageTask(this.getApplication(), product).execute();
    }

    private static class UpdateProductImageTask extends AsyncTask<Void, Void, Void> {

        @SuppressLint("StaticFieldLeak")
        private Context mContext;
        private Product mProduct;

        private UpdateProductImageTask(Context context, Product product) {
            this.mContext = context.getApplicationContext();
            this.mProduct = product;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ProductRepository.getInstance(this.mContext).updateProduct(mProduct);
            return null;
        }
    }

    /*
    ************************************************************************************************
    * current account information
    ************************************************************************************************
     */

    private LiveData<Account> mCurrentAccount;
    public LiveData<Account> findCurrentAccount() {
        if (this.mCurrentAccount == null) {
            this.mCurrentAccount = this.getAccountRepository().getCurrentAccount();
        }
        return this.mCurrentAccount;
    }


    /*
    ************************************************************************************************
    * get Shopping Lists: depend on current account
    ************************************************************************************************
     */

    private LiveData<List<ShoppingList>> mCurrentAccountShoppingLists;
    public LiveData<List<ShoppingList>> findCurrentAccountShoppingLists() {
        if (this.mCurrentAccountShoppingLists == null) {
            this.mCurrentAccountShoppingLists = Transformations.switchMap(
                    this.getAccountRepository().getCurrentAccount(),
                    new Function<Account, LiveData<List<ShoppingList>>>() {
                        @Override
                        public LiveData<List<ShoppingList>> apply(Account input) {
                            return input == null ? null : ProductInfoFragmentViewModel.this.getShoppingListRepository()
                                    .findAccountShoppingLists(input.getAccountId());
                        }
                    }
            );
        }
        return this.mCurrentAccountShoppingLists;
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
