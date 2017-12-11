package com.alvin.cheapyshopping.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.alvin.cheapyshopping.repositories.StoreRepository;
import com.alvin.cheapyshopping.db.entities.Store;

import java.util.List;

/**
 * Created by Alvin on 19/11/2017.
 */

public class StoreListFragmentViewModel extends AndroidViewModel {

    public StoreListFragmentViewModel(Application application) {
        super(application);
    }


    /*
    ************************************************************************************************
    * Repository
    ************************************************************************************************
     */

    private StoreRepository mStoreRepository;
    private StoreRepository getStoreRepository() {
        if (this.mStoreRepository == null) {
            this.mStoreRepository = StoreRepository.getInstance(this.getApplication());
        }
        return this.mStoreRepository;
    }


    /*
    ************************************************************************************************
    * get all stores
    ************************************************************************************************
     */

    private LiveData<List<Store>> mAllStoresCache;
    public LiveData<List<Store>> getAllStores() {
        if (this.mAllStoresCache == null) {
            this.mAllStoresCache = this.getStoreRepository().getAllStores();
        }
        return this.mAllStoresCache;
    }

}
