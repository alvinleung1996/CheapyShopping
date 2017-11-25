package com.alvin.cheapyshopping.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.alvin.cheapyshopping.db.entities.Product;
import com.alvin.cheapyshopping.db.entities.Store;
import com.alvin.cheapyshopping.db.entities.pseudo.StorePrice;
import com.alvin.cheapyshopping.repositories.PriceRepository;
import com.alvin.cheapyshopping.repositories.ProductRepository;
import com.alvin.cheapyshopping.repositories.StorePriceRepository;
import com.alvin.cheapyshopping.repositories.StoreRepository;

import java.util.List;
import java.util.Map;

/**
 * Created by cheng on 11/25/2017.
 */

public class ProductFragmentViewModel extends AndroidViewModel {

    public ProductFragmentViewModel(Application application) {
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

    /*
    ************************************************************************************************
    * get product
    ************************************************************************************************
     */

    private LiveData<Product> mProduct;

    public LiveData<Product> getProduct(long productId) {

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

    public LiveData<List<StorePrice>> getStorePrices(long productId){
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

    public LiveData<StorePrice> getBestStorePrice(long productId){
        if (this.mBestStorePrice == null){
            this.mBestStorePrice = this.getStorePriceRepository().findBestProductStorePrice(productId);
        }
        return this.mBestStorePrice;
    }
}
