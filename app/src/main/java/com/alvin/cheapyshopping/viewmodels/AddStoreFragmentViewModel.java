package com.alvin.cheapyshopping.viewmodels;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.os.AsyncTask;

import com.alvin.cheapyshopping.repositories.StoreRepository;
import com.alvin.cheapyshopping.db.entities.Store;

import java.util.UUID;

/**
 * Created by Alvin on 19/11/2017.
 */

public class AddStoreFragmentViewModel extends AndroidViewModel {

    public AddStoreFragmentViewModel(Application application) {
        super(application);
    }


    /*
    ************************************************************************************************
    * Add store: async operation
    ************************************************************************************************
     */

    public LiveData<Long> addStore(String name, String address, String placeId, double longitude, double latitude) {
        MutableLiveData<Long> result = new MutableLiveData<>();
        Store store = new Store();
        store.setName(name);
        store.setAddress(address);
        store.setLongitude(longitude);
        store.setLatitude(latitude);
        new InsertStoreTask(this.getApplication(), store, result).execute();
        return result;
    }

    private static class InsertStoreTask extends AsyncTask<Void, Void, long[]> {

        @SuppressLint("StaticFieldLeak")
        private final Context mContext;
        private final Store mStore;
        private final MutableLiveData<Long> mResult;

        private InsertStoreTask(Context context, Store store, MutableLiveData<Long> mResult) {
            this.mContext = context.getApplicationContext();
            this.mStore = store;
            this.mResult = mResult;
        }

        @Override
        protected long[] doInBackground(Void... voids) {
            mStore.setStoreId(UUID.randomUUID().toString());
            return StoreRepository.getInstance(this.mContext).insertStore(this.mStore);
        }

        @Override
        protected void onPostExecute(long[] storeIds) {
            this.mResult.setValue(storeIds[0]);
        }
    }

}
