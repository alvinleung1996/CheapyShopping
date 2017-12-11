package com.alvin.cheapyshopping.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.util.ArrayMap;

import com.alvin.cheapyshopping.db.entities.pseudo.ProductPrice;
import com.alvin.cheapyshopping.repositories.ProductPriceRepository;

import java.util.List;
import java.util.Map;

/**
 * Created by cheng on 11/27/2017.
 */


public class StoreProductPricesFragmentViewModel extends AndroidViewModel {

    public StoreProductPricesFragmentViewModel(Application application) {
        super(application);
    }


    /*
    ************************************************************************************************
    * Repository
    ************************************************************************************************
     */

    private ProductPriceRepository mProductPriceRepository;
    private ProductPriceRepository getProductPriceRepository() {
        if (this.mProductPriceRepository == null) {
            this.mProductPriceRepository = ProductPriceRepository.getInstance(this.getApplication());
        }
        return this.mProductPriceRepository;
    }



    /*
    ************************************************************************************************
    * Product Store Prices
    ************************************************************************************************
     */

    private Map<String, LiveData<List<ProductPrice>>> mStoreProductPricesCache;
    public LiveData<List<ProductPrice>> findStoreProductPrices(String storeId) {
        if (this.mStoreProductPricesCache == null) {
            this.mStoreProductPricesCache = new ArrayMap<>();
        }
        if (!this.mStoreProductPricesCache.containsKey(storeId)) {
            this.mStoreProductPricesCache.put(storeId, this.getProductPriceRepository()
                    .findStoreProductPrices(storeId));
        }
        return this.mStoreProductPricesCache.get(storeId);
    }



}
