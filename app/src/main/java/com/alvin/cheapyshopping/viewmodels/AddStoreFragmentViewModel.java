package com.alvin.cheapyshopping.viewmodels;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.content.Context;
import android.os.AsyncTask;

import com.alvin.cheapyshopping.repositories.StoreRepository;
import com.alvin.cheapyshopping.room.entities.Store;

/**
 * Created by Alvin on 19/11/2017.
 */

public class AddStoreFragmentViewModel extends AndroidViewModel {

    private final StoreRepository mStoreRepository;

    public AddStoreFragmentViewModel(Application application) {
        super(application);
        this.mStoreRepository = StoreRepository.getInstance(application);
    }

    public void addStore(String name, String location, Function<long[], Void> callback) {
        Store store = new Store();
        store.setName(name);
        store.setLocation(location);
        new InsertStoreTask(this.mStoreRepository, callback).execute(store);
    }

    private static class InsertStoreTask extends AsyncTask<Store, Void, long[]> {

        private final StoreRepository mStoreRepository;
        private final Function<long[], Void> mCallback;

        private InsertStoreTask(StoreRepository storeRepository, Function<long[], Void> callback) {
            this.mStoreRepository = storeRepository;
            this.mCallback = callback;
        }

        @Override
        protected long[] doInBackground(Store... stores) {
            return this.mStoreRepository.insert(stores);
        }

        @Override
        protected void onPostExecute(long[] storeIds) {
            if (this.mCallback != null) {
                this.mCallback.apply(storeIds);
            }
        }
    }

}
