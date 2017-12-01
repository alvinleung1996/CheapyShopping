package com.alvin.cheapyshopping.viewmodels;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.AsyncTask;

import com.alvin.cheapyshopping.db.entities.Account;
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

    /*
    ************************************************************************************************
    * remove / add custom account image
    ************************************************************************************************
     */

    public void removeCustomAccountImage(Account account){
        account.setImageExist(false);
        new UpdateProductImageTask(this.getApplication(), account).execute();
    }

    public void addCustomAccountImage(Account account){
        account.setImageExist(true);
        new UpdateProductImageTask(this.getApplication(), account).execute();
    }

    private static class UpdateProductImageTask extends AsyncTask<Void, Void, Void> {

        @SuppressLint("StaticFieldLeak")
        private Context mContext;
        private Account mAccount;

        private UpdateProductImageTask(Context context, Account account) {
            this.mContext = context.getApplicationContext();
            this.mAccount = account;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            AccountRepository.getInstance(this.mContext).updateAccount(mAccount);
            return null;
        }
    }


}
