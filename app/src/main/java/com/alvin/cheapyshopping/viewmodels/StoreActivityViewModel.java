package com.alvin.cheapyshopping.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.alvin.cheapyshopping.db.entities.Store;
import com.alvin.cheapyshopping.repositories.StoreRepository;

/**
 * Created by cheng on 12/8/2017.
 */

public class StoreActivityViewModel extends AndroidViewModel {

    public StoreActivityViewModel (Application application){
        super(application);
    }

    private LiveData<Store> mStore;

    public LiveData<Store> getStore(String storeId){
        if (mStore == null){
            mStore = StoreRepository.getInstance(getApplication()).findStoreByStoreId(storeId);
        }
        return mStore;
    }
}
