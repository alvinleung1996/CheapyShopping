package com.alvin.cheapyshopping.viewmodels;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.os.AsyncTask;

import com.alvin.cheapyshopping.db.entities.Price;
import com.alvin.cheapyshopping.db.entities.Store;
import com.alvin.cheapyshopping.repositories.PriceRepository;
import com.alvin.cheapyshopping.repositories.StoreRepository;

import java.util.Calendar;
import java.util.UUID;

/**
 * Created by Alvin on 27/11/2017.
 */

public class AddProductPriceFragmentViewModel extends AndroidViewModel {

    public AddProductPriceFragmentViewModel(Application application) {
        super(application);
    }



    /*
    ************************************************************************************************
    * Repositories
    ************************************************************************************************
     */

    private PriceRepository mPriceRepository;
    private PriceRepository getPriceRepository() {
        if (this.mPriceRepository == null) {
            this.mPriceRepository = PriceRepository.getInstance(this.getApplication());
        }
        return this.mPriceRepository;
    }

    private StoreRepository mStoreRepository;
    private StoreRepository getStoreRepository(){
        if(this.mStoreRepository == null){
            this.mStoreRepository = StoreRepository.getInstance(this.getApplication());
        }
        return this.mStoreRepository;
    }


    /*
    ************************************************************************************************
    * Add Price
    ************************************************************************************************
     */

    public LiveData<long[]> addPrice(String productId,
                             @Price.Type int priceType, double priceTotal, int priceQuantity, int priceFreeQuantity, double priceDiscount,
                             String storeId, String storeName, String storeAddress,
                             double storeLongitude, double storeLatitude) {
        MutableLiveData<long[]> result = new MutableLiveData<>();
        new PriceInserter(
                this.getApplication(), productId,
                priceType, priceTotal, priceQuantity, priceFreeQuantity, priceDiscount,
                storeId, storeName, storeAddress,
                storeLongitude, storeLatitude,
                result
        ).execute();
        return result;
    }

    private static class PriceInserter extends AsyncTask<Void, Void, Void> {

        @SuppressLint("StaticFieldLeak")
        private final Context mContext;

        private final String mProductId;

        @Price.Type
        private final int mPriceType;
        private final double priceTotal;
        private final int mPriceQuantity;
        private final int mPriceFreeQuantity;
        private final double mPriceDiscount;

        private final String mStoreId;
        private final String mStoreName;
        private final String mStoreAddress;
        private final double mStoreLongitude;
        private final double mStoreLatitude;

        private final MutableLiveData<long[]> mResult;

        private PriceInserter(Context context, String productId,
                              @Price.Type int priceType, double priceTotal, int priceQuantity, int priceFreeQuantity, double priceDiscount,
                              String storeId, String storeName, String storeAddress,
                              double storeLongitude, double storeLatitude,
                              MutableLiveData<long[]> result) {
            this.mContext = context.getApplicationContext();

            this.mProductId = productId;

            this.mPriceType = priceType;
            this.priceTotal = priceTotal;
            this.mPriceQuantity = priceQuantity;
            this.mPriceFreeQuantity = priceFreeQuantity;
            this.mPriceDiscount = priceDiscount;

            this.mStoreId = storeId;
            this.mStoreName = storeName;
            this.mStoreAddress = storeAddress;
            this.mStoreLongitude = storeLongitude;
            this.mStoreLatitude = storeLatitude;

            this.mResult = result;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            final StoreRepository storeRepository = StoreRepository.getInstance(this.mContext);

            // Find the store first
            Store store = storeRepository.findStoreByIdNow(this.mStoreId);
            boolean newStore = store == null;
            if (newStore) {
                store = new Store();
                store.setStoreId(this.mStoreId);
            }
            store.setName(this.mStoreName);
            store.setAddress(this.mStoreAddress);
            store.setLongitude(this.mStoreLongitude);
            store.setLatitude(this.mStoreLatitude);
            if (newStore) {
                storeRepository.insertStore(store);
            } else {
                storeRepository.updateStore(store);
            }

            // Then create a price record, price record should be immutable
            // so that no need to check if there is already an identical record.
            // The new record will contains the current time
            Price price = new Price();
            price.setPriceId(UUID.randomUUID().toString());
            price.setType(this.mPriceType);
            price.setTotal(this.priceTotal);
            price.setQuantity(this.mPriceQuantity);
            price.setFreeQuantity(this.mPriceFreeQuantity);
            price.setDiscount(this.mPriceDiscount);
            price.setCreationTime(Calendar.getInstance());
            price.setForeignProductId(this.mProductId);
            price.setForeignStoreId(store.getStoreId());

            final PriceRepository priceRepository = PriceRepository.getInstance(this.mContext);

            long[] result = priceRepository.insertPrice(price);

            this.mResult.postValue(result);
            return null;
        }
    }

    /*
    ************************************************************************************************
    * Get Store
    ************************************************************************************************
     */

    private LiveData<Store> mStore;

    public LiveData<Store> findStoreByStoreId(String storeId){
        if (this.mStore == null){
            this.mStore = this.getStoreRepository().findStoreByStoreId(storeId);
        }
        return this.mStore;
    }

}
