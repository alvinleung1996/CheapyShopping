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

    @Query("SELECT * FROM Account")
    public abstract LiveData<List<Account>> getAllAccounts();

    @Query("SELECT * FROM Account")
    public abstract List<Account> getAllNow();

    @Query("SELECT * FROM Account WHERE account_id = :accountId")
    public abstract LiveData<Account> findAccountByAccountId(long accountId);

    @Insert
    public abstract long[] insert(Account... accounts);

    @Update
    public abstract int update(Account... accounts);

    @Delete
    public abstract int delete(Account... accounts);

}
