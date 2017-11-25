package com.alvin.cheapyshopping.viewmodels;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.content.Context;
import android.os.AsyncTask;

import com.alvin.cheapyshopping.repositories.StoreRepository;
import com.alvin.cheapyshopping.db.entities.Store;

/**
 * Created by Alvin on 19/11/2017.
 */

public class AddStoreFragmentViewModel extends AndroidViewModel {

    public AddStoreFragmentViewModel(Application application) {
        super(application);
    }


    /*
    ************************************************************************************************
    * Add store
    ************************************************************************************************
     */

    public void addStore(String name, String location, Function<long[], Void> callback) {
        Store store = new Store();
        store.setName(name);
        store.setLocation(location);
        new InsertStoreTask(this.getApplication(), store, callback).execute();
    }

    private static class InsertStoreTask extends AsyncTask<Void, Void, long[]> {

        @SuppressLint("StaticFieldLeak")
        private final Context mContext;
        private final Store mStore;
        private final Function<long[], Void> mCallback;

        private InsertStoreTask(Context context, Store store, Function<long[], Void> callback) {
            this.mContext = context.getApplicationContext();
            this.mStore = store;
            this.mCallback = callback;
        }

        @Override
        protected long[] doInBackground(Void... voids) {
            return StoreRepository.getInstance(this.mContext).insertStore(this.mStore);
        }

        @Override
        protected void onPostExecute(long[] storeIds) {
            if (this.mCallback != null) {
                this.mCallback.apply(storeIds);
            }
        }
    }

}
