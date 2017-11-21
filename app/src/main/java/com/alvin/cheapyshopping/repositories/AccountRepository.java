package com.alvin.cheapyshopping.repositories;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.content.Context;
import android.util.ArrayMap;

import com.alvin.cheapyshopping.room.AppDatabase;
import com.alvin.cheapyshopping.room.daos.AccountDao;
import com.alvin.cheapyshopping.room.entities.Account;

import java.util.List;
import java.util.Map;

/**
 * Created by Alvin on 21/11/2017.
 */

public class AccountRepository {

    private static AccountRepository sInstance;

    private static AccountRepository getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new AccountRepository(context.getApplicationContext());
        }
        return sInstance;
    }


    private final Context mContext;

    private AccountDao mAccountDao;

    private LiveData<List<Account>> mAllAccounts;
    private Map<Long, LiveData<Account>> mCache;

    private LiveData<Account> mCurrentAccount;


    private AccountRepository(Context context) {
        this.mContext = context;
        this.mCache = new ArrayMap<>();
    }


    private AccountDao getAccountDao() {
        if (this.mAccountDao == null) {
            this.mAccountDao = AppDatabase.getInstance(this.mContext).getAccountDao();
        }
        return this.mAccountDao;
    }


    public LiveData<List<Account>> getAllAccounts() {
        if (this.mAllAccounts == null) {
            this.mAllAccounts = this.getAccountDao().getAllAccounts();
        }
        return this.mAllAccounts;
    }

    public LiveData<Account> getCurrentAccount() {
        if (this.mCurrentAccount == null) {
            this.mCurrentAccount = Transformations.map(this.getAllAccounts(), new Function<List<Account>, Account>() {
                @Override
                public Account apply(List<Account> input) {
                    // Just return the first user
                    return input != null && input.size() > 0 ? input.get(0) : null;
                }
            });
        }
        return this.mCurrentAccount;
    }

    public LiveData<Account> findAccountByAccountId(long accountId) {
        if (!this.mCache.containsKey(accountId)) {
            this.mCache.put(accountId, this.getAccountDao().findAccountByAccountId(accountId));
        }
        return this.mCache.get(accountId);
    }
}
