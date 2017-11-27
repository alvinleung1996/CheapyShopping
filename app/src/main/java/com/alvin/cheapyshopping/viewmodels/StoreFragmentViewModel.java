package com.alvin.cheapyshopping.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.alvin.cheapyshopping.db.entities.Store;
import com.alvin.cheapyshopping.db.entities.pseudo.ProductPrice;
import com.alvin.cheapyshopping.repositories.ProductPriceRepository;
import com.alvin.cheapyshopping.repositories.ProductRepository;
import com.alvin.cheapyshopping.repositories.ShoppingListRepository;
import com.alvin.cheapyshopping.repositories.StoreRepository;

import java.util.List;

/**
 * Created by cheng on 11/26/2017.
 */

public class StoreFragmentViewModel extends AndroidViewModel {

    public StoreFragmentViewModel(Application application) {
        super(application);
    }

    /*
    ************************************************************************************************
    * Repository
    ************************************************************************************************
    */


    private StoreRepository mStoreRepository;
    private ProductPriceRepository mProductPriceRepository;


    private StoreRepository getStoreRepository() {
        if (this.mStoreRepository == null) {
            this.mStoreRepository = StoreRepository.getInstance(this.getApplication());
        }
        return mStoreRepository;
    }

    private ProductPriceRepository getProductPriceRepository() {
        if (this.mProductPriceRepository == null) {
            this.mProductPriceRepository = ProductPriceRepository.getInstance(this.getApplication());
        }
        return mProductPriceRepository;
    }


    /*
    ************************************************************************************************
    * get Store
    ************************************************************************************************
     */

    private LiveData<Store> mStore;

    public LiveData<Store> getStore(String storeId) {

        if (this.mStore == null) {
            this.mStore = this.getStoreRepository().findStoreByStoreId(storeId);
        }
        return this.mStore;
    }

    /*
    ************************************************************************************************
    * get Store Product Price list
    ************************************************************************************************
     */

    private LiveData<List<ProductPrice>> mProductPriceList;

    public LiveData<List<ProductPrice>> getProductPriceList(String storeId){
        if (this.mProductPriceList == null){
            this.mProductPriceList = this.getProductPriceRepository().findProductStorePrices(storeId);
        }
        return this.mProductPriceList;
    }



}
