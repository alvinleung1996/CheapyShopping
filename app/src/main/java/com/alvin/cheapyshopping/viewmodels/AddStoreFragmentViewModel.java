package com.alvin.cheapyshopping.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;

import com.alvin.cheapyshopping.repositories.StoreRepository;
import com.alvin.cheapyshopping.room.entities.Store;

/**
 * Created by Alvin on 19/11/2017.
 */

public class AddStoreFragmentViewModel extends AndroidViewModel {

    private final StoreRepository mStoreRepository;

    public AddStoreFragmentViewModel(Application application) {
        super(application);
        this.mStoreRepository = StoreRepository.getInstance(application);
    }

    public long addStore(String name, String location) {
        Store store = new Store();
        store.setName(name);
        store.setLocation(location);
        return this.mStoreRepository.insertAll(store)[0];
    }

}
