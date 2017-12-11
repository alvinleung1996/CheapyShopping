package com.alvin.cheapyshopping.viewmodels;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.content.Context;
import android.os.AsyncTask;

import com.alvin.cheapyshopping.db.entities.Product;
import com.alvin.cheapyshopping.repositories.ProductRepository;


/**
 * Created by cheng on 12/8/2017.
 */

public class EditProductInfoDialogViewModel extends AndroidViewModel {

    public EditProductInfoDialogViewModel(Application application){
        super(application);
    }

    public void updateProduct(Product product){
        new UpdateProductTask(this.getApplication(), product).execute();
    }

    private static class UpdateProductTask extends AsyncTask<Void, Void, Void>{
        @SuppressLint("StaticFieldLeak")
        private final Context mContext;
        private final Product mProduct;

        private UpdateProductTask(Context context, Product product){
            this.mContext = context;
            this.mProduct = product;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ProductRepository.getInstance(mContext).updateProduct(mProduct);
            return null;
        }

    }



}
