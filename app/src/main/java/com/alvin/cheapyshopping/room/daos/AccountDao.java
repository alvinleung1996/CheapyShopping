package com.alvin.cheapyshopping.room.daos;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.alvin.cheapyshopping.room.entities.Account;

import java.util.List;

/**
 * Created by Alvin on 21/11/2017.
 */

@Dao
public abstract class AccountDao {

    /*
    ************************************************************************************************
    * Query, Async
    ************************************************************************************************
     */

    @Query("SELECT * FROM Account")
    public abstract LiveData<List<Account>> getAllAccounts();

    // TODO other method?
    @Query("SELECT * FROM Account LIMIT 1")
    public abstract LiveData<Account> getCurrentAccount();

    @Query("SELECT * FROM Account WHERE account_id = :accountId")
    public abstract LiveData<Account> findAccountByAccountId(long accountId);


    /*
    ************************************************************************************************
    * Query, Sync
    ************************************************************************************************
     */

    // Use by sample data
    @Query("SELECT * FROM Account")
    public abstract List<Account> getAllNow();

    // Use by shopping list fragment view model
    @Query("SELECT * FROM Account LIMIT 1")
    // TODO other method?
    public abstract Account getCurrentAccountNow();


    /*
    ************************************************************************************************
    * Other
    ************************************************************************************************
     */

    @Insert
    public abstract long[] insert(Account... accounts);

    @Update
    public abstract int update(Account... accounts);

    @Delete
    public abstract int delete(Account... accounts);

}
