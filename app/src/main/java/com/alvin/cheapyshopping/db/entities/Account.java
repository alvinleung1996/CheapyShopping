package com.alvin.cheapyshopping.db.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by Alvin on 21/11/2017.
 */

@Entity(
    foreignKeys = {
        @ForeignKey(
            childColumns = "active_shopping_list_id",
            entity = ShoppingList.class,
            parentColumns = "shopping_list_id",
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.SET_NULL
        )
    }
)
public class Account {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "account_id")
    private long mAccountId;

    @ColumnInfo(name = "active_shopping_list_id", index = true)
    private Long mActiveShoppingListId;

    @ColumnInfo(name = "account_name")
    private String mAccountName;

    @ColumnInfo(name = "account_type")
    private String mAccountType;

    @ColumnInfo(name = "account_mobile")
    private Long mAccountMobile;

    @ColumnInfo(name = "account_email")
    private String mAccountEmail;

    @ColumnInfo(name = "account_score")
    private Long mAccountScore;

    @ColumnInfo(name = "account_password")
    private Long mAccountPassword;


    public long getAccountId() {
        return this.mAccountId;
    }

    public void setAccountId(long accountId) {
        this.mAccountId = accountId;
    }

    public Long getActiveShoppingListId() {
        return this.mActiveShoppingListId;
    }

    public void setActiveShoppingListId(Long activeShoppingListId) {
        this.mActiveShoppingListId = activeShoppingListId;
    }

    public String getAccountName() {
        return this.mAccountName;
    }

    public void setAccountName(String accountName) {
        this.mAccountName = accountName;
    }

    public String getAccountType() {
        return this.mAccountType;
    }

    public void setAccountType(String accountType) {
        this.mAccountType = accountType;
    }

    public Long getAccountMobile() {
        return this.mAccountMobile;
    }

    public void setAccountMobile(Long accountMobile) {
        this.mAccountMobile = accountMobile;
    }

    public Long getAccountScore() {
        return mAccountScore;
    }

    public void setAccountScore(Long accountScore) {
        mAccountScore = accountScore;
    }

    public String getAccountEmail() {
        return mAccountEmail;
    }

    public void setAccountEmail(String accountEmail) {
        mAccountEmail = accountEmail;
    }

    public Long getAccountPassword() {
        return mAccountPassword;
    }

    public void setAccountPassword(Long accountPassword) {
        mAccountPassword = accountPassword;
    }
}
