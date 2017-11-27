package com.alvin.cheapyshopping.viewmodels;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;

import com.alvin.cheapyshopping.db.entities.Account;
import com.alvin.cheapyshopping.db.entities.Product;
import com.alvin.cheapyshopping.db.entities.ShoppingList;
import com.alvin.cheapyshopping.db.entities.pseudo.StorePrice;
import com.alvin.cheapyshopping.repositories.AccountRepository;
import com.alvin.cheapyshopping.repositories.PriceRepository;
import com.alvin.cheapyshopping.repositories.ProductRepository;
import com.alvin.cheapyshopping.repositories.ShoppingListRepository;
import com.alvin.cheapyshopping.repositories.StorePriceRepository;
import com.alvin.cheapyshopping.repositories.StoreRepository;

import java.util.List;

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

    private StoreRepository getStoreRepository() {
        if (this.mStoreRepository == null) {
            this.mStoreRepository = StoreRepository.getInstance(this.getApplication());
        }
        return mStoreRepository;
    }

    private PriceRepository getPriceRepository() {
        if (this.mPriceRepository == null) {
            this.mPriceRepository = PriceRepository.getInstance(this.getApplication());
        }
        return mPriceRepository;
    }

    private StorePriceRepository getStorePriceRepository() {
        if (this.mStorePriceRepository == null) {
            this.mStorePriceRepository = StorePriceRepository.getInstance(this.getApplication());
        }
        return mStorePriceRepository;
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

    private LiveData<Product> mProduct;

    public LiveData<Product> getProduct(String productId) {

        if (this.mProduct == null) {
            this.mProduct = this.getProductRepository().findProductByProductId(productId);
        }
        return this.mProduct;
    }

    /*
    ************************************************************************************************
    * get storePrice list
    ************************************************************************************************
     */

    private LiveData<List<StorePrice>> mStorePricesList;

    public LiveData<List<StorePrice>> getStorePrices(String productId){
        if (this.mStorePricesList == null){
            this.mStorePricesList = this.getStorePriceRepository().findProductStorePrices(productId);
        }
        return this.mStorePricesList;
    }

    /*
    ************************************************************************************************
    * get best storePrice
    ************************************************************************************************
     */

    private LiveData<StorePrice> mBestStorePrice;

    public LiveData<StorePrice> getBestStorePrice(String productId){
        if (this.mBestStorePrice == null){
            this.mBestStorePrice = this.getStorePriceRepository().findBestProductStorePrice(productId);
        }
        return this.mBestStorePrice;
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


    private Account mCurrentAccountNow;

    public Account getCurrentAccountNow () {
        if (this.mCurrentAccountNow == null) {
            this.mCurrentAccountNow = this.getAccountRepository().getCurrentAccountNow();
        }
        return this.mCurrentAccountNow;
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



    private List<ShoppingList> mCurrentAccountShoppingListsNow;

    public List<ShoppingList> getCurrentAccountShoppingListsNow() {
        if (this.mCurrentAccountShoppingListsNow == null) {
            mCurrentAccountShoppingListsNow = this.getShoppingListRepository().
                    findAccountShoppingListsNow(this.getCurrentAccountNow().getAccountId());
        }
        return this.mCurrentAccountShoppingListsNow;
    }


}
