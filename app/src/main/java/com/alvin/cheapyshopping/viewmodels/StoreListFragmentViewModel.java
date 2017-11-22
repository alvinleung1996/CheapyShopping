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

    private final StoreRepository mStoreRepository;

    public StoreListFragmentViewModel(Application application) {
        super(application);
        this.mStoreRepository = StoreRepository.getInstance(application);
    }


    public LiveData<List<Store>> getStores() {
        return this.mStoreRepository.getAllStores();
    }

}
