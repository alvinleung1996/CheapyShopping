package com.alvin.cheapyshopping.viewmodels;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.util.ArrayMap;

import com.alvin.cheapyshopping.db.entities.Account;
import com.alvin.cheapyshopping.db.entities.Product;
import com.alvin.cheapyshopping.db.entities.ShoppingList;
import com.alvin.cheapyshopping.db.entities.ShoppingListProductRelation;
import com.alvin.cheapyshopping.db.entities.pseudo.StorePrice;
import com.alvin.cheapyshopping.repositories.AccountRepository;
import com.alvin.cheapyshopping.repositories.PriceRepository;
import com.alvin.cheapyshopping.repositories.ProductRepository;
import com.alvin.cheapyshopping.repositories.ShoppingListProductRelationRepository;
import com.alvin.cheapyshopping.repositories.ShoppingListRepository;
import com.alvin.cheapyshopping.repositories.StorePriceRepository;
import com.alvin.cheapyshopping.repositories.StoreRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by cheng on 11/25/2017.
 */

public class ProductInfoFragmentViewModel extends AndroidViewModel {

    public ProductInfoFragmentViewModel(Application application) {
        super(application);
    }

    /*
    ************************************************************************************************
    * Repository
    ************************************************************************************************
    */

    private ProductRepository mProductRepository;

    private ProductRepository getProductRepository() {
        if (this.mProductRepository == null) {
            this.mProductRepository = ProductRepository.getInstance(this.getApplication());
        }
        return mProductRepository;
    }


    /*
    ************************************************************************************************
    * get product
    ************************************************************************************************
     */

    // Assume the fragment will not change the product id throughout its life time
    private LiveData<Product> mProduct;
    public LiveData<Product> getProduct(String productId) {
        if (this.mProduct == null) {
            this.mProduct = this.getProductRepository().findProductByProductId(productId);
        }
        return this.mProduct;
    }

    /*
    ************************************************************************************************
    * remove / add custom product image
    ************************************************************************************************
     */

    public void removeCustomProductImage(Product product){
        product.setImageExist(false);
        new UpdateProductImageTask(this.getApplication(), product).execute();
    }

    public void addCustomProductImage(Product product){
        product.setImageExist(true);
        new UpdateProductImageTask(this.getApplication(), product).execute();
    }

    private static class UpdateProductImageTask extends AsyncTask<Void, Void, Void> {

        @SuppressLint("StaticFieldLeak")
        private Context mContext;
        private Product mProduct;

        private UpdateProductImageTask(Context context, Product product) {
            this.mContext = context.getApplicationContext();
            this.mProduct = product;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ProductRepository.getInstance(this.mContext).updateProduct(mProduct);
            return null;
        }
    }



}
