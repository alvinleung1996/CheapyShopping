package com.alvin.cheapyshopping.viewmodels;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.content.Context;
import android.os.AsyncTask;

import com.alvin.cheapyshopping.db.entities.Store;
import com.alvin.cheapyshopping.repositories.StoreRepository;

/**
 * Created by cheng on 12/8/2017.
 */

public class EditStoreInfoDialogViewModel extends AndroidViewModel {
    public EditStoreInfoDialogViewModel(Application application){
        super(application);
    }


    public void updateStore(Store store){
        new UpdateStoreTask(this.getApplication(), store).execute();
    }

    private static class UpdateStoreTask extends AsyncTask<Void, Void, Void> {
        @SuppressLint("StaticFieldLeak")
        private final Context mContext;
        private final Store mStore;

        private UpdateStoreTask(Context context, Store store){
            this.mContext = context;
            this.mStore = store;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            StoreRepository.getInstance(mContext).updateStore(mStore);
            return null;
        }

    }
}
