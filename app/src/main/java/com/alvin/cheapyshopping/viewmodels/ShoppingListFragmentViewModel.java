package com.alvin.cheapyshopping.viewmodels;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.Transformations;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

import com.alvin.cheapyshopping.repositories.AccountRepository;
import com.alvin.cheapyshopping.repositories.ShoppingListProductRelationRepository;
import com.alvin.cheapyshopping.repositories.ShoppingListRepository;
import com.alvin.cheapyshopping.db.daos.ShoppingListProductRelationDao.ShoppingListProduct;
import com.alvin.cheapyshopping.db.entities.Account;
import com.alvin.cheapyshopping.db.entities.Product;
import com.alvin.cheapyshopping.db.entities.ShoppingList;
import com.alvin.cheapyshopping.db.entities.Store;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by Alvin on 20/11/2017.
 */

public class ShoppingListFragmentViewModel extends AndroidViewModel {

    private AccountRepository mAccountRepository;
    private ShoppingListRepository mShoppingListRepository;
    private ShoppingListProductRelationRepository mShoppingListProductRelationRepository;


    public ShoppingListFragmentViewModel(Application application) {
        super(application);
    }

    private AccountRepository getAccountRepository() {
        if (this.mAccountRepository == null) {
            this.mAccountRepository = AccountRepository.getInstance(this.getApplication());
        }
        return mAccountRepository;
    }

    private ShoppingListRepository getShoppingListRepository() {
        if (this.mShoppingListRepository == null) {
            this.mShoppingListRepository = ShoppingListRepository.getInstance(this.getApplication());
        }
        return this.mShoppingListRepository;
    }

    private ShoppingListProductRelationRepository getShoppingListProductRelationRepository() {
        if (this.mShoppingListProductRelationRepository == null) {
            this.mShoppingListProductRelationRepository = ShoppingListProductRelationRepository.getInstance(this.getApplication());
        }
        return this.mShoppingListProductRelationRepository;
    }


    /*
    ************************************************************************************************
    * get account
    ************************************************************************************************
     */

    public LiveData<Account> mCurrentAccount;

    public LiveData<Account> getCurrentAccount() {
        if (this.mCurrentAccount == null) {
            this.mCurrentAccount = this.getAccountRepository().getCurrentAccount();
        }
        return this.mCurrentAccount;
    }


    /*
    ************************************************************************************************
    * get Shopping List
    ************************************************************************************************
     */

    private LiveData<List<ShoppingList>> mCurrentAccountShoppingLists;

    public LiveData<List<ShoppingList>> getCurrentAccountShoppingLists() {
        if (this.mCurrentAccountShoppingLists == null) {
            this.mCurrentAccountShoppingLists = Transformations.switchMap(
                this.getAccountRepository().getCurrentAccount(),
                new Function<Account, LiveData<List<ShoppingList>>>() {
                    @Override
                    public LiveData<List<ShoppingList>> apply(Account input) {
                        return input == null ? null
                            : ShoppingListFragmentViewModel.this
                                .getShoppingListRepository()
                                .findAccountShoppingLists(input.getAccountId());
                    }
                }
            );
        }
        return this.mCurrentAccountShoppingLists;
    }

    /*
    ************************************************************************************************
    * get Shopping List
    ************************************************************************************************
     */

    private LiveData<Map<Store, List<ShoppingListProduct>>> mCurrentAccountShoppingListResult;

    public LiveData<Map<Store, List<ShoppingListProduct>>> getCurrentAccountShoppingListResult() {
        if (this.mCurrentAccountShoppingListResult == null) {
            this.mCurrentAccountShoppingListResult = Transformations.switchMap(
                this.getCurrentAccount(),
                new Function<Account, LiveData<Map<Store, List<ShoppingListProduct>>>>() {
                    @Override
                    public LiveData<Map<Store, List<ShoppingListProduct>>> apply(Account input) {
                        return input == null ? null
                            : ShoppingListFragmentViewModel.this.getShoppingListProductRelationRepository()
                                .getShoppingListResult(input.getActiveShoppingListId());
                    }
                }
            );
        }
        return this.mCurrentAccountShoppingListResult;
    }


    /*
    ************************************************************************************************
    * set active shopping list item
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

        private ShoppingListItem(Product product) {
            this.type = TYPE_PRODUCT;
            this.store = null;
            this.product = product;
        }

        public int type;
        public Store store;
        public Product product;

    }

    private LiveData<List<ShoppingListItem>> mCurrentAccountShoppingListItems;

    public LiveData<List<ShoppingListItem>> getCurrentAccountShoppingListItems() {
        if (this.mCurrentAccountShoppingListItems == null) {
            this.mCurrentAccountShoppingListItems = new CurrentAccountShoppingListItemsComputer();
        }
        return this.mCurrentAccountShoppingListItems;
    }

    private class CurrentAccountShoppingListItemsComputer extends MediatorLiveData<List<ShoppingListItem>> {

        private Map<Store, List<ShoppingListProduct>> mCurrentAccountShoppingListResult;
        private ExecutorService mExecutor;
        private Future<?> mExecutingJob;

        private CurrentAccountShoppingListItemsComputer() {
            this.addSource(
                ShoppingListFragmentViewModel.this.getCurrentAccountShoppingListResult(),
                new Observer<Map<Store, List<ShoppingListProduct>>>() {
                    @Override
                    public void onChanged(@Nullable Map<Store, List<ShoppingListProduct>> result) {
                        CurrentAccountShoppingListItemsComputer.this.mCurrentAccountShoppingListResult
                                = result;
                        CurrentAccountShoppingListItemsComputer.this.compute();
                    }
                }
            );
        }

        private void compute() {
            if (this.mExecutingJob != null) {
                this.mExecutingJob.cancel(true);
                this.mExecutingJob = null;
            }
            if (this.mExecutor == null) {
                this.mExecutor = Executors.newSingleThreadExecutor();
            }
            if (this.mCurrentAccountShoppingListResult == null) {
                this.setValue(null);
                return;
            }
            this.mExecutingJob = this.mExecutor.submit(new Runnable() {
                private final Map<Store, List<ShoppingListProduct>> mCurrentAccountShoppingListResult
                        = CurrentAccountShoppingListItemsComputer.this.mCurrentAccountShoppingListResult;
                @Override
                public void run() {
                    List<ShoppingListItem> items = new ArrayList<>();
                    for (Map.Entry<Store, List<ShoppingListProduct>> entry
                            : this.mCurrentAccountShoppingListResult.entrySet()) {

                        final Store store = entry.getKey();
                        final List<ShoppingListProduct> products = entry.getValue();

                        if (products.size() > 0) {
                            items.add(new ShoppingListItem(store));

                            for (Product product : products) {
                                items.add(new ShoppingListItem(product));
                            }
                        }
                    }

                    if (!Thread.interrupted()) CurrentAccountShoppingListItemsComputer.this.postValue(items);
                }
            });
        }

    }


    /*
    ************************************************************************************************
    * set active shopping list
    ************************************************************************************************
     */

    public void setShoppingListId(long shoppingListId) {
        new UpdateAccountTask(this.getApplication()).execute(shoppingListId);
    }

    private static class UpdateAccountTask extends AsyncTask<Long, Void, Integer> {

        @SuppressLint("StaticFieldLeak")
        private final Context mContext;

        private UpdateAccountTask(Context context) {
            this.mContext = context.getApplicationContext();
        }

        @Override
        protected Integer doInBackground(Long... shoppingListId) {
            AccountRepository repository = AccountRepository.getInstance(this.mContext);
            Account account = repository.getCurrentAccountNow();
            account.setActiveShoppingListId(shoppingListId[0]);
            return repository.updateAccount(account);
        }

    }

}
