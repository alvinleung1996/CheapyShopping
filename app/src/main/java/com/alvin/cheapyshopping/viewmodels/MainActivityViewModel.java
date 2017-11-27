package com.alvin.cheapyshopping.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.alvin.cheapyshopping.db.entities.Account;
import com.alvin.cheapyshopping.repositories.AccountRepository;

/**
 * Created by cheng on 11/26/2017.
 */

public class MainActivityViewModel extends AndroidViewModel {

    public MainActivityViewModel(Application application){
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

    /*
    ************************************************************************************************
    * Find current account
    ************************************************************************************************
    */

    private LiveData<Account> mCurrentAccount;
    public LiveData<Account> findCurrentAccount() {
        if (this.mCurrentAccount == null) {
            this.mCurrentAccount = this.getAccountRepository().getCurrentAccount();
        }
        return this.mCurrentAccount;
    }


}
