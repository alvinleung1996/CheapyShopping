package com.alvin.cheapyshopping.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;

import com.alvin.cheapyshopping.db.entities.Product;
import com.alvin.cheapyshopping.repositories.AccountRepository;

/**
 * Created by cheng on 11/26/2017.
 */

public class AccountFragmentViewModel extends AndroidViewModel {

    public AccountFragmentViewModel(Application application){
        super(application);
    }

    /*
    ************************************************************************************************
    * Repository
    ************************************************************************************************
    */

    private AccountRepository mAccountRepository;

    private AccountRepository getAccountRepository(){
        if (this.mAccountRepository == null){
            this.mAccountRepository = AccountRepository.getInstance(this.getApplication());
        }
        return mAccountRepository;
    }


}
