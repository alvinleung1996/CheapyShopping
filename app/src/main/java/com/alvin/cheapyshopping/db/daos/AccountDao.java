package com.alvin.cheapyshopping.db.daos;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.alvin.cheapyshopping.db.entities.Account;
import com.alvin.cheapyshopping.db.entities.Rank;

import java.util.List;

/**
 * Created by Alvin on 21/11/2017.
 */

@Dao
public interface AccountDao {

    /*
    ************************************************************************************************
    * Query, Async
    ************************************************************************************************
     */

    @Query("SELECT * FROM Account")
    LiveData<List<Account>> getAllAccounts();

    // TODO other method?
    @Query("SELECT * FROM Account LIMIT 1")
    LiveData<Account> findCurrentAccount();

    @Query("SELECT * FROM Account WHERE account_id = :accountId")
    LiveData<Account> findAccountByAccountId(String accountId);


    /*
    ************************************************************************************************
    * Query, Sync
    ************************************************************************************************
     */

    // Use by sample data
    @Query("SELECT * FROM Account")
    List<Account> getAllAccountsNow();

    // Use by shopping list fragment view model
    @Query("SELECT * FROM Account LIMIT 1")
    // TODO other method?
    Account findCurrentAccountNow();

    @Query("SELECT * FROM Account WHERE account_id = :accountId")
    Account findAccountByAccountIdNow(String accountId);


    /*
    ************************************************************************************************
    * Other
    ************************************************************************************************
     */

    @Insert
    long[] insertAccount(Account... accounts);

    @Update
    int updateAccount(Account... accounts);

    @Delete
    int deleteAccount(Account... accounts);

}
