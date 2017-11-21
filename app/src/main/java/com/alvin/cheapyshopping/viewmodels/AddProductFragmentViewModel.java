package com.alvin.cheapyshopping.viewmodels;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.os.AsyncTask;

import com.alvin.cheapyshopping.repositories.ProductRepository;
import com.alvin.cheapyshopping.room.entities.Product;

/**
 * Created by Alvin on 19/11/2017.
 */

public class AddProductFragmentViewModel extends AndroidViewModel {

    private final ProductRepository mProductRepository;

    public AddProductFragmentViewModel(Application application) {
        super(application);
        this.mProductRepository = ProductRepository.getInstance(application);
    }

    public void addProduct(String name, String description, Function<long[], Void> callback) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        new InsertProductTask(this.mProductRepository, callback).execute(product);
    }


    private class InsertProductTask extends AsyncTask<Product, Void, long[]> {

        private ProductRepository mProductRepository;
        private Function<long[], Void> mCallback;

        private InsertProductTask(ProductRepository productRepository, Function<long[], Void> mCallback) {
            this.mProductRepository = productRepository;
            this.mCallback = mCallback;
        }

        @Override
        protected long[] doInBackground(Product... products) {
            return this.mProductRepository.insert(products);
        }

        @Override
        protected void onPostExecute(long[] productIds) {
            if (this.mCallback != null) {
                this.mCallback.apply(productIds);
            }
        }
    }

}
