package com.alvin.cheapyshopping.repositories;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.util.ArrayMap;

import com.alvin.cheapyshopping.db.AppDatabase;
import com.alvin.cheapyshopping.db.daos.AccountDao;
import com.alvin.cheapyshopping.db.entities.Account;

import java.util.List;
import java.util.Map;

/**
 * Created by Alvin on 21/11/2017.
 */

public class AccountRepository {

    @SuppressLint("StaticFieldLeak")
    private static AccountRepository sInstance;

    public static AccountRepository getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new AccountRepository(context);
        }
        return sInstance;
    }


    private final Context mContext;

    private AccountRepository(Context context) {
        this.mContext = context.getApplicationContext();
    }


    /*
    ************************************************************************************************
    * Dao
    ************************************************************************************************
     */

    private AccountDao mAccountDao;
    private AccountDao getAccountDao() {
        if (this.mAccountDao == null) {
            this.mAccountDao = AppDatabase.getInstance(this.mContext).getAccountDao();
        }
        return this.mAccountDao;
    }


    /*
    ************************************************************************************************
    * Async
    ************************************************************************************************
     */

    private LiveData<List<Account>> mAllAccounts;
    public LiveData<List<Account>> getAllAccounts() {
        if (this.mAllAccounts == null) {
            this.mAllAccounts = this.getAccountDao().getAllAccounts();
        }
        return this.mAllAccounts;
    }

    private LiveData<Account> mCurrentAccount;
    public LiveData<Account> findCurrentAccount() {
        if (this.mCurrentAccount == null) {
            this.mCurrentAccount = this.getAccountDao().findCurrentAccount();
        }
        return this.mCurrentAccount;
    }

    private Map<String, LiveData<Account>> mAccountCache;
    public LiveData<Account> findAccountByAccountId(String accountId) {
        if (this.mAccountCache == null) {
            this.mAccountCache = new ArrayMap<>();
        }
        if (!this.mAccountCache.containsKey(accountId)) {
            this.mAccountCache.put(accountId, this.getAccountDao().findAccountByAccountId(accountId));
        }
        return this.mAccountCache.get(accountId);
    }



    /*
    ************************************************************************************************
    * Sync
    ************************************************************************************************
     */

    public List<Account> getAllAccountsNow() {
        return this.getAccountDao().getAllAccountsNow();
    }

    public Account findCurrentAccountNow() {
        return this.getAccountDao().findCurrentAccountNow();
    }

    public Account findAccountByAccountIdNow(String accountId) {
        return this.getAccountDao().findAccountByAccountIdNow(accountId);
    }

    public void addScoreToCurrentAccountNow(int score){
        Account account = findCurrentAccountNow();

        if (account!=null){
            int newScore =  account.getAccountScore() + score;
            account.setAccountScore(newScore);
            this.updateAccount(account);
        }
    }

    /*
    ************************************************************************************************
    * Other
    ************************************************************************************************
     */

    public long[] insertAccount(Account... accounts) {
        return this.getAccountDao().insertAccount(accounts);
    }

    public int updateAccount(Account... accounts) {
        return this.getAccountDao().updateAccount(accounts);
    }

    public int deleteAccount(Account... accounts) {
        return this.getAccountDao().deleteAccount(accounts);
    }
}
