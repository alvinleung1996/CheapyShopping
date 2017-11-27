package com.alvin.cheapyshopping.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.v7.app.AppCompatActivity;

import com.alvin.cheapyshopping.EditStoreActivity;
import com.alvin.cheapyshopping.repositories.StoreRepository;

/**
 * Created by cheng on 11/27/2017.
 */

public class EditStoreActivityViewModel extends AndroidViewModel {

    public EditStoreActivityViewModel(Application application){
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
        return mStoreRepository;
    }

}
