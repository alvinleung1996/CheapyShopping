package com.alvin.cheapyshopping.viewmodels;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.content.Context;
import android.os.AsyncTask;

import com.alvin.cheapyshopping.db.entities.Account;
import com.alvin.cheapyshopping.repositories.AccountRepository;


/**
 * Created by cheng on 12/9/2017.
 */

public class EditAccountInfoDialogViewModel extends AndroidViewModel {

    public EditAccountInfoDialogViewModel (Application application){
        super(application);
    }

    public void updateAccount(Account account){
        new UpdateAccountTask(this.getApplication(), account).execute();
    }

    private static class UpdateAccountTask extends AsyncTask<Void, Void, Void> {
        @SuppressLint("StaticFieldLeak")
        private final Context mContext;
        private final Account mAccount;

        private UpdateAccountTask(Context context, Account account){
            this.mContext = context;
            this.mAccount = account;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            AccountRepository.getInstance(mContext).updateAccount(mAccount);
            return null;
        }

    }
}
