package com.alvin.cheapyshopping.viewmodels;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.util.ArraySet;
import android.util.Log;
import android.widget.Toast;

import com.alvin.cheapyshopping.BaseActivity;
import com.alvin.cheapyshopping.db.entities.ShoppingListProductRelation;
import com.alvin.cheapyshopping.db.entities.pseudo.ShoppingListProduct;
import com.alvin.cheapyshopping.repositories.AccountRepository;
import com.alvin.cheapyshopping.repositories.BestPriceRelationRepository;
import com.alvin.cheapyshopping.repositories.ShoppingListProductRelationRepository;
import com.alvin.cheapyshopping.repositories.ShoppingListProductRepository;
import com.alvin.cheapyshopping.repositories.ShoppingListRepository;
import com.alvin.cheapyshopping.db.entities.Account;
import com.alvin.cheapyshopping.db.entities.ShoppingList;
import com.alvin.cheapyshopping.db.entities.Store;
import com.alvin.cheapyshopping.utils.LivePromise;
import com.alvin.cheapyshopping.utils.LocationManager;
import com.alvin.cheapyshopping.utils.MutableLivePromise;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Alvin on 20/11/2017.
 */

public class ShoppingListFragmentViewModel extends AndroidViewModel {



    public ShoppingListFragmentViewModel(Application application) {
        super(application);
    }


    /*
    ************************************************************************************************
    * Repository
    ************************************************************************************************
     */

    private AccountRepository mAccountRepository;
    private AccountRepository getAccountRepository() {
        if (this.mAccountRepository == null) {
            this.mAccountRepository = AccountRepository.getInstance(this.getApplication());
        }
        return mAccountRepository;
    }

    private ShoppingListRepository mShoppingListRepository;
    private ShoppingListRepository getShoppingListRepository() {
        if (this.mShoppingListRepository == null) {
            this.mShoppingListRepository = ShoppingListRepository.getInstance(this.getApplication());
        }
        return this.mShoppingListRepository;
    }

    private ShoppingListProductRepository mShoppingListProductRepository;
    private ShoppingListProductRepository getShoppingListProductRepository() {
        if (this.mShoppingListProductRepository == null) {
            this.mShoppingListProductRepository = ShoppingListProductRepository.getInstance(this.getApplication());
        }
        return this.mShoppingListProductRepository;
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
    * current account information
    ************************************************************************************************
     */

    private LiveData<Account> mCurrentAccount;
    public LiveData<Account> findCurrentAccount() {
        if (this.mCurrentAccount == null) {
            this.mCurrentAccount = this.getAccountRepository().getCurrentAccount();
        }
        return this.mCurrentAccount;
    }


    /*
    ************************************************************************************************
    * get Shopping Lists: depend on current account
    ************************************************************************************************
     */

    private LiveData<List<ShoppingList>> mCurrentAccountShoppingLists;
    public LiveData<List<ShoppingList>> findCurrentAccountShoppingLists() {
        if (this.mCurrentAccountShoppingLists == null) {
            this.mCurrentAccountShoppingLists = Transformations.switchMap(
                this.getAccountRepository().getCurrentAccount(),
                input -> input == null ? null : ShoppingListFragmentViewModel.this.getShoppingListRepository()
                        .findAccountShoppingLists(input.getAccountId())
            );
        }
        return this.mCurrentAccountShoppingLists;
    }

    /*
    ************************************************************************************************
    * get current account active shopping List: depend on current account
    ************************************************************************************************
     */

    private LiveData<ShoppingList> mCurrentAccountActiveShoppingListCache;
    public LiveData<ShoppingList> findCurrentAccountActiveShoppingList() {
        if (this.mCurrentAccountActiveShoppingListCache == null) {
            this.mCurrentAccountActiveShoppingListCache = Transformations.switchMap(
                this.findCurrentAccount(),
                account -> {
                    if (account == null) {
                        return null;
                    }
                    String activeId = account.getActiveShoppingListId();
                    if (activeId == null) {
                        return null;
                    }
                    return ShoppingListFragmentViewModel.this.getShoppingListRepository()
                            .findShoppingList(activeId);
                }
            );
        }
        return this.mCurrentAccountActiveShoppingListCache;
    }

    /*
    ************************************************************************************************
    * get Shopping List Products: depend on current account active shopping list
    * Used by bottom sheet
    ************************************************************************************************
     */

    private LiveData<List<ShoppingListProduct>> mCurrentAccountActiveShoppingListProductsCache;
    public LiveData<List<ShoppingListProduct>> findCurrentAccountActiveShoppingListProducts() {
        if (this.mCurrentAccountActiveShoppingListProductsCache == null) {
            this.mCurrentAccountActiveShoppingListProductsCache = Transformations.switchMap(
                this.findCurrentAccountActiveShoppingList(),
                list -> list == null ? null : this.getShoppingListProductRepository()
                        .findShoppingListProducts(list.getShoppingListId())
            );
        }
        return this.mCurrentAccountActiveShoppingListProductsCache;
    }


    private LiveData<Double> mCurrentAccountActiveShoppingListTotal;
    public LiveData<Double> findCurrentAccountActiveShoppingListTotal() {
        if (this.mCurrentAccountActiveShoppingListTotal == null) {
            this.mCurrentAccountActiveShoppingListTotal = Transformations.map(
                    this.findCurrentAccountActiveShoppingListProducts(),
                    products -> {
                        if (products == null) {
                            return null;
                        }
                        double total = 0.0;
                        for (ShoppingListProduct product : products) {
                            if (!product.getBestStorePrices().isEmpty()) {
                                total += product.getBestStorePrices().get(0)
                                        .getComputedPrice(product.getQuantity());
                            }
                        }
                        return total;
                    }
            );
        }
        return this.mCurrentAccountActiveShoppingListTotal;
    }

    /*
    ************************************************************************************************
    * get Grouped Shopping List Products: depend on current account active shopping list
    ************************************************************************************************
     */

    private LiveData<Map<Store, List<ShoppingListProduct>>> mCurrentAccountGroupedActiveShoppingListProductsCache;
    public LiveData<Map<Store, List<ShoppingListProduct>>> findCurrentAccountGroupedActiveShoppingListProducts() {
        if (this.mCurrentAccountGroupedActiveShoppingListProductsCache == null) {
            this.mCurrentAccountGroupedActiveShoppingListProductsCache = Transformations.switchMap(
                    this.findCurrentAccountActiveShoppingList(),
                    list -> list == null ? null : this.getShoppingListProductRepository()
                            .findGroupedShoppingListProducts(list.getShoppingListId())
            );
        }
        return this.mCurrentAccountGroupedActiveShoppingListProductsCache;
    }


    /*
    ************************************************************************************************
    * get Shopping List Product List Item: depend on Grouped Shopping List Products
    ************************************************************************************************
     */

    public class ShoppingListItem {

        public static final int TYPE_STORE = 0;
        public static final int TYPE_PRODUCT = 1;

        private ShoppingListItem(Store store) {
            this.type = TYPE_STORE;
            this.store = store;
            this.product = null;
        }

        private ShoppingListItem(ShoppingListProduct product) {
            this.type = TYPE_PRODUCT;
            this.store = null;
            this.product = product;
        }

        public int type;
        public Store store;
        public ShoppingListProduct product;

    }

    private LiveData<List<ShoppingListItem>> mCurrentAccountShoppingListItems;
    public LiveData<List<ShoppingListItem>> findCurrentAccountShoppingListItems() {
        if (this.mCurrentAccountShoppingListItems == null) {
            this.mCurrentAccountShoppingListItems = new CurrentAccountShoppingListItemsComputer();
        }
        return this.mCurrentAccountShoppingListItems;
    }

    private class CurrentAccountShoppingListItemsComputer extends MediatorLiveData<List<ShoppingListItem>> {

        private CurrentAccountShoppingListItemsComputer() {
            this.addSource(
                ShoppingListFragmentViewModel.this
                        .findCurrentAccountGroupedActiveShoppingListProducts(),
                this::onCurrentAccountGroupedShoppingListProductsChanged
            );
        }

        private void onCurrentAccountGroupedShoppingListProductsChanged(
                @Nullable Map<Store, List<ShoppingListProduct>> groups) {

            if (groups == null) {
                this.setValue(null);
                return;
            }

            List<ShoppingListItem> items = new ArrayList<>();
            for (Map.Entry<Store, List<ShoppingListProduct>> entry : groups.entrySet()) {

                final Store store = entry.getKey();
                final List<ShoppingListProduct> products = entry.getValue();

                if (products.size() > 0) {
                    items.add(new ShoppingListItem(store));

                    for (ShoppingListProduct product : products) {
                        items.add(new ShoppingListItem(product));
                    }
                }
            }

            this.setValue(items);
        }

    }


    /*
    ************************************************************************************************
    * set active shopping list
    ************************************************************************************************
     */

    public void setShoppingListId(String shoppingListId) {
        new UpdateAccountTask(this.getApplication(), shoppingListId).execute();
    }

    private static class UpdateAccountTask extends AsyncTask<Void, Void, Integer> {

        @SuppressLint("StaticFieldLeak")
        private final Context mContext;
        private final String mShoppingListId;

        private UpdateAccountTask(Context context, String shoppingListId) {
            this.mContext = context.getApplicationContext();
            this.mShoppingListId = shoppingListId;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            AccountRepository repository = AccountRepository.getInstance(this.mContext);
            Account account = repository.getCurrentAccountNow();
            account.setActiveShoppingListId(this.mShoppingListId);
            return repository.updateAccount(account);
        }

    }


    /*
    ************************************************************************************************
    * update best price relation
    * 1. Find location
    * 2. Update shopping list center location
    * 3. Compute best price relation
    ************************************************************************************************
     */

    private ShoppingListBestPriceRelationsUpdater mUpdater;
    public LivePromise<Integer, Void> refreshBestPriceRelations(BaseActivity activity, String shoppingListId) {
        if (this.mUpdater == null) {
            this.mUpdater = new ShoppingListBestPriceRelationsUpdater();
        }
        return this.mUpdater.update(activity, shoppingListId);
    }

    private class ShoppingListBestPriceRelationsUpdater {

        private Set<String> mShoppingListIds;
        private MutableLivePromise<Integer, Void> mPromise;

        private LivePromise<Integer, Void> update(BaseActivity activity, String shoppingListId) {
            if (this.mShoppingListIds == null) {
                this.mShoppingListIds = new ArraySet<>();
            }

            this.mShoppingListIds.add(shoppingListId);

            if (this.mPromise == null) {
                this.mPromise = new MutableLivePromise<>();

                LivePromise<Location, Void> promise = LocationManager.getInstance(ShoppingListFragmentViewModel.this.getApplication()).updateLocation(activity);
                promise.observeResolve(activity, this::onLocationUpdated);
                promise.observeReject(activity, v -> this.onLocationUpdateFailed());
            }

            return this.mPromise;
        }

        private void onLocationUpdateFailed() {
            this.mPromise.setRejectValue(null);
            this.mPromise = null;
        }

        @SuppressLint("StaticFieldLeak")
        private void onLocationUpdated(Location location) {
            String text = "Longtitude: " + location.getLongitude() + ", Latitude: " + location.getLatitude();
            Toast.makeText(ShoppingListFragmentViewModel.this.getApplication(), text, Toast.LENGTH_SHORT).show();

            ShoppingListFragmentViewModel.this.mUpdater = null;

            new AsyncTask<Void, Void, Integer>() {

                @Override
                protected Integer doInBackground(Void... voids) {
                    List<ShoppingList> listToUpdate = new ArrayList<>();

                    for (String id : ShoppingListBestPriceRelationsUpdater.this.mShoppingListIds) {

                        ShoppingList shoppingList = ShoppingListFragmentViewModel.this.getShoppingListRepository()
                                .findShoppingListNow(id);

                        shoppingList.setCenterLongitude(location.getLongitude());
                        shoppingList.setCenterLatitude(location.getLatitude());

                        listToUpdate.add(shoppingList);
                    }

                    return ShoppingListFragmentViewModel.this.getShoppingListRepository().updateShoppingList(
                            listToUpdate.toArray(new ShoppingList[listToUpdate.size()]));
                }

                @Override
                protected void onPostExecute(Integer integer) {
                    BestPriceRelationRepository repository = ShoppingListFragmentViewModel.this.getBestPriceRelationRepository();
                    for (String id : ShoppingListBestPriceRelationsUpdater.this.mShoppingListIds) {
                        repository.refreshBestPriceRelation(id);
                    }
                }

            }.execute();
        }

    }


    /*
    ************************************************************************************************
    * update shopping list product relation quantity
    ************************************************************************************************
     */

    public LiveData<Integer> updateShoppingListProductRelationQuantity(String shoppingListId, String productId, int quantity) {
        MutableLiveData<Integer> result = new MutableLiveData<>();
        new ShoppingListProductRelationQuantityUpdater(this.getApplication(), shoppingListId, productId, quantity, result).execute();
        return result;
    }

    private static class ShoppingListProductRelationQuantityUpdater extends AsyncTask<Void, Void, Void> {

        @SuppressLint("StaticFieldLeak")
        private final Context mContext;
        private final String mShoppingListId;
        private final String mProductId;
        private final int mQuantity;
        private final MutableLiveData<Integer> result;

        private ShoppingListProductRelationQuantityUpdater(
                Context context, String shoppingListId, String productId, int quantity,
                MutableLiveData<Integer> result) {
            this.mContext = context.getApplicationContext();
            this.mShoppingListId = shoppingListId;
            this.mProductId = productId;
            this.mQuantity = quantity;
            this.result = result;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ShoppingListProductRelationRepository repository
                    = ShoppingListProductRelationRepository.getInstance(this.mContext);


            ShoppingListProductRelation relation
                    = repository.getShoppingListProductRelationNow(this.mShoppingListId, this.mProductId);

            if (relation == null) {
                Log.e("Shopping List", "Cannot find relation! splId: " + this.mShoppingListId + ", pId: " + this.mProductId);
                return null;
            }

            BestPriceRelationRepository.getInstance(this.mContext)
                    .deleteShoppingListProductBestPrice(this.mShoppingListId, this.mProductId);

            relation.setQuantity(this.mQuantity);

            int ret = repository.updateShoppingListProductRelation(relation);
            this.result.postValue(ret);

            return null;
        }
    }

}
