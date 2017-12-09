package com.alvin.cheapyshopping.viewmodels;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.AsyncTask;

import com.alvin.cheapyshopping.db.AppDatabaseCallback;
import com.alvin.cheapyshopping.db.entities.Setting;
import com.alvin.cheapyshopping.repositories.ProductRepository;
import com.alvin.cheapyshopping.db.entities.Product;
import com.alvin.cheapyshopping.repositories.SettingRepository;

import java.util.UUID;

/**
 * Created by Alvin on 19/11/2017.
 */

public class AddProductFragmentViewModel extends AndroidViewModel {

    public AddProductFragmentViewModel(Application application) {
        super(application);
    }


    /*
    ************************************************************************************************
    * Add product
    ************************************************************************************************
     */

    public void addProduct(String name, String description,Boolean imageIsSet, Function<String, Void> callback) {
        Product product = new Product();
        product.setProductId(UUID.randomUUID().toString());
        product.setName(name);
        product.setDescription(description);
        product.setImageExist(imageIsSet);
        new InsertProductTask(this.getApplication(), product, callback).execute();
    }

    private static class InsertProductTask extends AsyncTask<Void, Void, String> {

        @SuppressLint("StaticFieldLeak")
        private Context mContext;
        private Product mProduct;
        private Function<String, Void> mCallback;

        private InsertProductTask(Context context, Product product, Function<String, Void> mCallback) {
            this.mContext = context.getApplicationContext();
            this.mProduct = product;
            this.mCallback = mCallback;
        }

        @Override
        protected String doInBackground(Void... voids) {
            mProduct.setProductId(UUID.randomUUID().toString());
            ProductRepository.getInstance(this.mContext).insertProduct(this.mProduct);
            return mProduct.getProductId();
        }

        @Override
        protected void onPostExecute(String productIds) {
            if (this.mCallback != null) {
                this.mCallback.apply(productIds);
            }
        }
    }

    /*
    ************************************************************************************************
    * Product Image setting
    ************************************************************************************************
     */

    private LiveData<Setting> mSetting;
    public LiveData<Setting> getSetting(String settingId){
        if (mSetting == null){
            mSetting = SettingRepository.getInstance(getApplication()).getSettingById(settingId);
        }
        return mSetting;
    }

    public void newProductImageSettingIsSet(boolean imageIsSet){
        new ResetNewProductImageSettingTask(getApplication(), imageIsSet).execute();
    }

    private static class ResetNewProductImageSettingTask extends AsyncTask<Void, Void, Void>{
        @SuppressLint("StaticFieldLeak")
        private Context mContext;
        private Setting mSetting;
        private boolean mImageIsSet;

        private ResetNewProductImageSettingTask(Context context, boolean isSet){
            this.mContext = context;
            this.mImageIsSet = isSet;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            this.mSetting = SettingRepository.getInstance(mContext).getSettingByIdNow(AppDatabaseCallback.SETTING_NEW_PRODUCT_IMAGE);
            if (mImageIsSet){
                mSetting.setSettingBoolean(true);
            }else {
                mSetting.setSettingBoolean(false);
            }
            SettingRepository.getInstance(this.mContext).updateSetting(mSetting);
            return null;
        }
    }

}
