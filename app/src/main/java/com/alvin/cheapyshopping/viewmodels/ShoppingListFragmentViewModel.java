package com.alvin.cheapyshopping.viewmodels;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Transformations;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

import com.alvin.cheapyshopping.db.entities.Account;
import com.alvin.cheapyshopping.db.entities.ShoppingList;
import com.alvin.cheapyshopping.db.entities.Store;
import com.alvin.cheapyshopping.db.entities.pseudo.ShoppingListProduct;
import com.alvin.cheapyshopping.repositories.AccountRepository;
import com.alvin.cheapyshopping.repositories.ShoppingListProductRepository;
import com.alvin.cheapyshopping.repositories.ShoppingListRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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


    /*
    ************************************************************************************************
    * current account information
    ************************************************************************************************
     */

    private LiveData<Account> mCurrentAccount;
    public LiveData<Account> findCurrentAccount() {
        if (this.mCurrentAccount == null) {
            this.mCurrentAccount = this.getAccountRepository().findCurrentAccount();
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
                this.getAccountRepository().findCurrentAccount(),
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
            Account account = repository.findCurrentAccountNow();
            account.setActiveShoppingListId(this.mShoppingListId);
            return repository.updateAccount(account);
        }

    }

}
