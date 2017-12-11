package com.alvin.cheapyshopping.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.util.ArrayMap;

import com.alvin.cheapyshopping.db.entities.pseudo.StorePrice;
import com.alvin.cheapyshopping.repositories.StorePriceRepository;

import java.util.List;
import java.util.Map;

/**
 * Created by Alvin on 27/11/2017.
 */

public class ProductStorePricesFragmentViewModel extends AndroidViewModel {

    public ProductStorePricesFragmentViewModel(Application application) {
        super(application);
    }


    /*
    ************************************************************************************************
    * Repository
    ************************************************************************************************
     */

    private StorePriceRepository mStorePriceRepository;
    private StorePriceRepository getStorePriceRepository() {
        if (this.mStorePriceRepository == null) {
            this.mStorePriceRepository = StorePriceRepository.getInstance(this.getApplication());
        }
        return this.mStorePriceRepository;
    }



    /*
    ************************************************************************************************
    * Product Store Prices
    ************************************************************************************************
     */

    private Map<String, LiveData<List<StorePrice>>> mProductStorePricesCache;
    public LiveData<List<StorePrice>> findProductStorePrices(String productId) {
        if (this.mProductStorePricesCache == null) {
            this.mProductStorePricesCache = new ArrayMap<>();
        }
        if (!this.mProductStorePricesCache.containsKey(productId)) {
            this.mProductStorePricesCache.put(productId, this.getStorePriceRepository()
                    .findProductStorePrices(productId));
        }
        return this.mProductStorePricesCache.get(productId);
    }

}
