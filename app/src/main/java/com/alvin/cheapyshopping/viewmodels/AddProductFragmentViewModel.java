package com.alvin.cheapyshopping.viewmodels;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.content.Context;
import android.os.AsyncTask;

import com.alvin.cheapyshopping.repositories.ProductRepository;
import com.alvin.cheapyshopping.db.entities.Product;

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

    public void addProduct(String name, String description, Function<long[], Void> callback) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        new InsertProductTask(this.getApplication(), product, callback).execute();
    }

    private static class InsertProductTask extends AsyncTask<Void, Void, long[]> {

        @SuppressLint("StaticFieldLeak")
        private Context mContext;
        private Product mProduct;
        private Function<long[], Void> mCallback;

        private InsertProductTask(Context context, Product product, Function<long[], Void> mCallback) {
            this.mContext = context.getApplicationContext();
            this.mProduct = product;
            this.mCallback = mCallback;
        }

        @Override
        protected long[] doInBackground(Void... voids) {
            return ProductRepository.getInstance(this.mContext).insertProduct(this.mProduct);
        }

        @Override
        protected void onPostExecute(long[] productIds) {
            if (this.mCallback != null) {
                this.mCallback.apply(productIds);
            }
        }
    }

}
