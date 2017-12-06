package com.alvin.cheapyshopping.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.util.ArrayMap;

import com.alvin.cheapyshopping.R;
import com.alvin.cheapyshopping.db.entities.Account;
import com.alvin.cheapyshopping.fragments.AccountFragment;
import com.alvin.cheapyshopping.fragments.ProductListFragment;
import com.alvin.cheapyshopping.fragments.ShoppingListFragment;
import com.alvin.cheapyshopping.fragments.StoreListFragment;
import com.alvin.cheapyshopping.repositories.AccountRepository;

import java.util.Map;

/**
 * Created by cheng on 11/26/2017.
 */

public class MainActivityViewModel extends AndroidViewModel {

    public MainActivityViewModel(Application application){
        super(application);
    }


    /*
    ************************************************************************************************
    * Find current account
    ************************************************************************************************
    */

    private LiveData<Account> mCurrentAccount;
    public LiveData<Account> findCurrentAccount() {
        if (mCurrentAccount == null) {
            mCurrentAccount = AccountRepository.getInstance(getApplication()).findCurrentAccount();
        }
        return mCurrentAccount;
    }

}
