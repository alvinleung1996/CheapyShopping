package com.alvin.cheapyshopping.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.alvin.cheapyshopping.db.entities.Store;
import com.alvin.cheapyshopping.repositories.StoreRepository;

import java.util.List;

/**
 * Created by Alvin on 25/11/2017.
 */

public class SelectStoreFragmentViewModel extends AndroidViewModel {

    public SelectStoreFragmentViewModel(Application application) {
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
    * Repository
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
