package com.alvin.cheapyshopping.db.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

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

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "account_id")
    private String mAccountId;

    @ColumnInfo(name = "active_shopping_list_id", index = true)
    private String mActiveShoppingListId;

    @ColumnInfo(name = "account_name")
    private String mAccountName;

    @ColumnInfo(name = "account_type")
    private String mAccountType;

    @ColumnInfo(name = "account_mobile")
    private String mAccountMobile;

    @ColumnInfo(name = "account_email")
    private String mAccountEmail;

    @ColumnInfo(name = "account_score")
    private String mAccountScore;

    @ColumnInfo(name = "account_password")
    private String mAccountPassword;


    public String getAccountId() {
        return this.mAccountId;
    }

    public void setAccountId(String accountId) {
        this.mAccountId = accountId;
    }

    public String getActiveShoppingListId() {
        return this.mActiveShoppingListId;
    }

    public void setActiveShoppingListId(String activeShoppingListId) {
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

    public String getAccountMobile() {
        return this.mAccountMobile;
    }

    public void setAccountMobile(String accountMobile) {
        this.mAccountMobile = accountMobile;
    }

    public String getAccountScore() {
        return mAccountScore;
    }

    public void setAccountScore(String accountScore) {
        mAccountScore = accountScore;
    }

    public String getAccountEmail() {
        return mAccountEmail;
    }

    public void setAccountEmail(String accountEmail) {
        mAccountEmail = accountEmail;
    }

    public String getAccountPassword() {
        return mAccountPassword;
    }

    public void setAccountPassword(String accountPassword) {
        mAccountPassword = accountPassword;
    }
}
