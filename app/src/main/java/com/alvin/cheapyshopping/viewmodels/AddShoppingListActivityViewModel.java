package com.alvin.cheapyshopping.viewmodels;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.content.Context;
import android.os.AsyncTask;

import com.alvin.cheapyshopping.repositories.ShoppingListRepository;
import com.alvin.cheapyshopping.db.entities.ShoppingList;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Alvin on 21/11/2017.
 */

public class AddShoppingListActivityViewModel extends AndroidViewModel {

    private final ShoppingListRepository mShoppingListRepository;

    public AddShoppingListActivityViewModel(Application application) {
        super(application);
        this.mShoppingListRepository = ShoppingListRepository.getInstance(application);
    }


    private String mAccountId;

    public void setAccountId(String accountId) {
        this.mAccountId = accountId;
    }

    public String getAccountId() {
        return this.mAccountId;
    }



    public void addShoppingList(String name, Function<long[], Void> callback) {
        if (this.mAccountId == null) {
            throw new RuntimeException("account id not set");
        }
        ShoppingList shoppingList = new ShoppingList();
        shoppingList.setName(name);
        shoppingList.setCreationTime(Calendar.getInstance());
        new InsertShoppingListTask(this.getApplication(), this.mAccountId, name, callback).execute();
    }

    private static class InsertShoppingListTask extends AsyncTask<Void, Void, long[]> {

        @SuppressLint("StaticFieldLeak")
        private final Context mContext;
        private final String mAccountId;
        private final String mName;
        private final Function<long[], Void> mCallback;

        private InsertShoppingListTask(Context context, String accountId, String name, Function<long[], Void> callback) {
            this.mContext = context.getApplicationContext();
            this.mAccountId = accountId;
            this.mName = name;
            this.mCallback = callback;
        }

        @Override
        protected long[] doInBackground(Void... voids) {
            ShoppingList list = new ShoppingList();
            list.setForeignAccountId(this.mAccountId);
            list.setName(mName);
            return ShoppingListRepository.getInstance(this.mContext).insertShoppingList(list);
        }

        @Override
        protected void onPostExecute(long[] shoppingListIds) {
            if (this.mCallback != null) {
                this.mCallback.apply(shoppingListIds);
            }
        }
    }

}
