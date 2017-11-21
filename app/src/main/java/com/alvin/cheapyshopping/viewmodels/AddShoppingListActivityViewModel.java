package com.alvin.cheapyshopping.viewmodels;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.content.Context;
import android.os.AsyncTask;

import com.alvin.cheapyshopping.repositories.ShoppingListRepository;
import com.alvin.cheapyshopping.room.entities.ShoppingList;

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

    public void addShoppingList(String name, Function<long[], Void> callback) {
        ShoppingList shoppingList = new ShoppingList();
        shoppingList.setName(name);
        shoppingList.setCreationTime(new Date());
        new InsertShoppingListTask(this.mShoppingListRepository, callback).execute(shoppingList);
    }



    private static class InsertShoppingListTask extends AsyncTask<ShoppingList, Void, long[]> {

        private final ShoppingListRepository mShoppingListRepository;
        private final Function<long[], Void> mCallback;

        private InsertShoppingListTask(ShoppingListRepository shoppingListRepository, Function<long[], Void> callback) {
            this.mShoppingListRepository = shoppingListRepository;
            this.mCallback = callback;
        }

        @Override
        protected long[] doInBackground(ShoppingList... shoppingLists) {
            return mShoppingListRepository.insert(shoppingLists);
        }

        @Override
        protected void onPostExecute(long[] shoppingListIds) {
            if (this.mCallback != null) {
                this.mCallback.apply(shoppingListIds);
            }
        }
    }

}
