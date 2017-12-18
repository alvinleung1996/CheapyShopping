package com.alvin.cheapyshopping.viewmodels;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.os.AsyncTask;

import com.alvin.cheapyshopping.db.entities.Product;
import com.alvin.cheapyshopping.db.entities.ShoppingListProductRelation;
import com.alvin.cheapyshopping.repositories.BestPriceRelationRepository;
import com.alvin.cheapyshopping.repositories.ProductRepository;
import com.alvin.cheapyshopping.repositories.ShoppingListProductRelationRepository;
import com.alvin.cheapyshopping.utils.Promise;

/**
 * Created by Alvin on 3/12/2017.
 */

public class ModifyShoppingListProductRelationDialogFragmentViewModel extends AndroidViewModel {

    public ModifyShoppingListProductRelationDialogFragmentViewModel(Application application) {
        super(application);
    }


    /*
    ************************************************************************************************
    * Repository
    ************************************************************************************************
     */

    private ShoppingListProductRelationRepository mShoppingListProductRelationRepository;
    private ShoppingListProductRelationRepository getShoppingListProductRelationRepository() {
        if (this.mShoppingListProductRelationRepository == null) {
            this.mShoppingListProductRelationRepository
                    = ShoppingListProductRelationRepository.getInstance(this.getApplication());
        }
        return this.mShoppingListProductRelationRepository;
    }


    private ProductRepository mProductRepository;
    private ProductRepository getProductRepository() {
        if (this.mProductRepository == null) {
            this.mProductRepository = ProductRepository.getInstance(this.getApplication());
        }
        return this.mProductRepository;
    }


    private BestPriceRelationRepository mBestPriceRelationRepository;
    private BestPriceRelationRepository getBestPriceRelationRepository() {
        if (this.mBestPriceRelationRepository == null) {
            this.mBestPriceRelationRepository = BestPriceRelationRepository.getInstance(this.getApplication());
        }
        return this.mBestPriceRelationRepository;
    }


    /*
    ************************************************************************************************
    * Setting
    ************************************************************************************************
     */

    private MutableLiveData<String> mShoppingListId = new MutableLiveData<>();
    private MutableLiveData<String> mProductId = new MutableLiveData<>();
    public void setShoppingListProductRelationId(String shoppingListId, String productId) {
        this.mShoppingListId.setValue(shoppingListId);
        this.mProductId.setValue(productId);
    }

    public LiveData<String> getShoppingListId() {
        return this.mShoppingListId;
    }

    public LiveData<String> getProductId() {
        return this.mProductId;
    }


    /*
    ************************************************************************************************
    * Relation data
    ************************************************************************************************
     */

    private LiveData<ShoppingListProductRelation> mShoppingListProductRelation;
    private LiveData<ShoppingListProductRelation> getShoppingListProductRelation() {
        if (this.mShoppingListProductRelation == null) {
            this.mShoppingListProductRelation = new ShoppingListProductRelationFinder();
        }
        return this.mShoppingListProductRelation;
    }

    private class ShoppingListProductRelationFinder extends MediatorLiveData<ShoppingListProductRelation> {

        private String mShoppingListId;
        private String mProductId;
        private LiveData<ShoppingListProductRelation> mSource;

        private ShoppingListProductRelationFinder() {
            this.addSource(
                ModifyShoppingListProductRelationDialogFragmentViewModel.this.getShoppingListId(),
                i -> {
                    this.mShoppingListId = i;
                    this.onIdChanged();
                }
            );
            this.addSource(
                ModifyShoppingListProductRelationDialogFragmentViewModel.this.getProductId(),
                i -> {
                    this.mProductId = i;
                    this.onIdChanged();
                }
            );
        }

        private void onIdChanged() {
            if (this.mShoppingListId == null || this.mProductId == null) {
                return;
            }
            if (this.mSource != null) {
                this.removeSource(this.mSource);
            }
            this.mSource = ModifyShoppingListProductRelationDialogFragmentViewModel.this
                    .getShoppingListProductRelationRepository()
                    .getShoppingListProductRelation(this.mShoppingListId, this.mProductId);
            this.addSource(this.mSource, this::setValue);
        }
    }



    private LiveData<Product> mProduct;
    public LiveData<Product> getProduct() {
        if (this.mProduct == null) {
            this.mProduct = Transformations.switchMap(this.getProductId(),
                    i -> i == null ? null : this.getProductRepository().findProductByProductId(i));
        }
        return this.mProduct;
    }


    /*
    ************************************************************************************************
    * UI data
    ************************************************************************************************
     */

    private QuantityData mQuantity;
    public LiveData<Integer> getQuantity() {
        if (this.mQuantity == null) {
            this.mQuantity = new QuantityData();

        }
        return this.mQuantity;
    }

    public void setQuantity(int quantity) {
        if (this.mQuantity == null) {
            this.mQuantity = new QuantityData();
        }
        this.mQuantity.setQuantity(quantity);
    }

    private class QuantityData extends MediatorLiveData<Integer> {

        private LiveData<ShoppingListProductRelation> mSource;

        private QuantityData() {
            this.mSource = ModifyShoppingListProductRelationDialogFragmentViewModel.this
                    .getShoppingListProductRelation();
            this.addSource(
                this.mSource,
                r -> {
                    if (r != null) this.setValue(r.getQuantity());
                }
            );
        }

        private void setQuantity(int quantity) {
            if (this.mSource != null) {
                this.removeSource(this.mSource);
                this.mSource = null;
            }
            this.setValue(quantity);
        }
    }


    /*
    ************************************************************************************************
    * update operation
    ************************************************************************************************
     */

    public Promise<Integer> updateShoppingListProductRelation() {
        return new Promise<>(handler -> {
            ShoppingListProductRelation relation = this.getShoppingListProductRelation().getValue();
            if (relation == null) {
                throw new IllegalStateException();
            }

            Integer quantity = this.getQuantity().getValue();
            if (quantity == null) {
                throw new IllegalStateException();
            } else if (quantity == relation.getQuantity()) {
                handler.resolve(0);
                return;
            }

            // Copy in order to changing the original one
            relation = new ShoppingListProductRelation(relation);
            relation.setQuantity(quantity);
            new ShoppingListProductRelationUpdater(relation, handler).execute();
        });
    }

    @SuppressLint("StaticFieldLeak")
    private class ShoppingListProductRelationUpdater extends AsyncTask<Void, Void, Void> {

        private final ShoppingListProductRelation mRelation;
        private final Promise<? super Integer>.Handler mPromiseHandler;

        private ShoppingListProductRelationUpdater(ShoppingListProductRelation relation, Promise<? super Integer>.Handler promiseHandler) {
            this.mRelation = relation;
            this.mPromiseHandler = promiseHandler;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ModifyShoppingListProductRelationDialogFragmentViewModel.this
                    .getBestPriceRelationRepository()
                    .deleteShoppingListProductBestPrice(this.mRelation.getForeignShoppingListId(),
                            this.mRelation.getForeignProductId());

            int affected = ModifyShoppingListProductRelationDialogFragmentViewModel.this
                    .getShoppingListProductRelationRepository()
                    .updateShoppingListProductRelation(this.mRelation);

            this.mPromiseHandler.resolve(affected);

            return null;
        }
    }


    /*
    ************************************************************************************************
    * remove operation
    ************************************************************************************************
     */

    public Promise<Integer> deleteShoppingListProductRelation() {
        return new Promise<>(handler -> {
            String shoppingListId = this.mShoppingListId.getValue();
            String productId = this.mProductId.getValue();
            if (shoppingListId == null || productId == null) {
                throw new RuntimeException("Shopping list id or product id is not set");
            }
            new ShoppingListProductRelationRemover(shoppingListId, productId, handler).execute();
        });
    }

    @SuppressLint("StaticFieldLeak")
    private class ShoppingListProductRelationRemover extends AsyncTask<Void, Void, Void> {

        private final String shoppingListId;
        private final String productId;
        private final Promise<? super Integer>.Handler mPromiseHandler;

        private ShoppingListProductRelationRemover(String shoppingListId, String productId,
                                                   Promise<? super Integer>.Handler promiseHandler) {
            this.shoppingListId = shoppingListId;
            this.productId = productId;
            this.mPromiseHandler = promiseHandler;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            int affected = ModifyShoppingListProductRelationDialogFragmentViewModel.this
                    .getShoppingListProductRelationRepository()
                    .deleteShoppingListProductRelation(this.shoppingListId, this.productId);

            this.mPromiseHandler.resolve(affected);

            return null;
        }
    }
}
