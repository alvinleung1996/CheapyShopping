package com.alvin.cheapyshopping.viewmodels;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.os.AsyncTask;

import com.alvin.cheapyshopping.db.entities.Account;
import com.alvin.cheapyshopping.db.entities.ShoppingList;
import com.alvin.cheapyshopping.db.entities.pseudo.ShoppingListProduct;
import com.alvin.cheapyshopping.repositories.AccountRepository;
import com.alvin.cheapyshopping.repositories.BestPriceRelationRepository;
import com.alvin.cheapyshopping.repositories.ShoppingListProductRepository;
import com.alvin.cheapyshopping.repositories.ShoppingListRepository;
import com.alvin.cheapyshopping.utils.LivePromise0;
import com.alvin.cheapyshopping.utils.MutableLivePromise0;
import com.alvin.cheapyshopping.utils.SettableLiveData;

import java.util.List;

/**
 * Created by Alvin on 5/12/2017.
 */

public class ShoppingListBottomSheetContentFragmentViewModel extends AndroidViewModel {

    public ShoppingListBottomSheetContentFragmentViewModel(Application application) {
        super(application);
    }



    /*
    ************************************************************************************************
    * current account information
    ************************************************************************************************
     */

    private LiveData<Account> mCurrentAccount;
    public LiveData<Account> findCurrentAccount() {
        if (mCurrentAccount == null) {
            mCurrentAccount = AccountRepository.getInstance(getApplication())
                    .findCurrentAccount();
        }
        return mCurrentAccount;
    }


    /*
    ************************************************************************************************
    * get current account active shopping List: depend on current account
    ************************************************************************************************
     */

    private LiveData<ShoppingList> mCurrentAccountActiveShoppingListCache;
    public LiveData<ShoppingList> findCurrentAccountActiveShoppingList() {
        if (mCurrentAccountActiveShoppingListCache == null) {
            mCurrentAccountActiveShoppingListCache = Transformations.switchMap(
                    findCurrentAccount(),
                    account -> {
                        if (account == null) {
                            return null;
                        }
                        String activeId = account.getActiveShoppingListId();
                        if (activeId == null) {
                            return null;
                        }
                        return ShoppingListRepository.getInstance(getApplication())
                                .findShoppingList(activeId);
                    }
            );
        }
        return mCurrentAccountActiveShoppingListCache;
    }


    /*
    ************************************************************************************************
    * get center data
    ************************************************************************************************
     */

    private SettableLiveData<Double> mCurrentAccountActiveShoppingListCenterLongitude;
    public LiveData<Double> getCurrentAccountActiveShoppingListCenterLongitude() {
        if (mCurrentAccountActiveShoppingListCenterLongitude == null) {
            mCurrentAccountActiveShoppingListCenterLongitude = new SettableLiveData<>(
                    Transformations.map(findCurrentAccountActiveShoppingList(),
                            v -> v == null ? null : v.getCenterLongitude())
            );
        }
        return mCurrentAccountActiveShoppingListCenterLongitude;
    }

    public void setCurrentAccountActiveShoppingListCenterLongitude(double longitude) {
        ((SettableLiveData<Double>) getCurrentAccountActiveShoppingListCenterLongitude())
                .setValue(longitude);
    }


    private SettableLiveData<Double> mCurrentAccountActiveShoppingListCenterLatitude;
    public LiveData<Double> getCurrentAccountActiveShoppingListCenterLatitude() {
        if (mCurrentAccountActiveShoppingListCenterLatitude == null) {
            mCurrentAccountActiveShoppingListCenterLatitude = new SettableLiveData<>(
                    Transformations.map(findCurrentAccountActiveShoppingList(),
                            v -> v == null ? null : v.getCenterLatitude())
            );
        }
        return mCurrentAccountActiveShoppingListCenterLatitude;
    }

    public void setCurrentAccountActiveShoppingListCenterLatitude(double latitude) {
        ((SettableLiveData<Double>) getCurrentAccountActiveShoppingListCenterLatitude())
                .setValue(latitude);
    }


    private SettableLiveData<Double> mCurrentAccountActiveShoppingListCenterLongitudeRange;
    public LiveData<Double> getCurrentAccountActiveShoppingListCenterLongitudeRange() {
        if (mCurrentAccountActiveShoppingListCenterLongitudeRange == null) {
            mCurrentAccountActiveShoppingListCenterLongitudeRange = new SettableLiveData<>(
                    Transformations.map(findCurrentAccountActiveShoppingList(),
                            v -> v == null ? null : v.getCenterLongitudeRange())
            );
        }
        return mCurrentAccountActiveShoppingListCenterLongitudeRange;
    }

    public void setCurrentAccountActiveShoppingListCenterLongitudeRange(double longitude) {
        ((SettableLiveData<Double>) getCurrentAccountActiveShoppingListCenterLongitudeRange())
                .setValue(longitude);
    }


    private SettableLiveData<Double> mCurrentAccountActiveShoppingListCenterLatitudeRange;
    public LiveData<Double> getCurrentAccountActiveShoppingListCenterLatitudeRange() {
        if (mCurrentAccountActiveShoppingListCenterLatitudeRange == null) {
            mCurrentAccountActiveShoppingListCenterLatitudeRange = new SettableLiveData<>(
                    Transformations.map(findCurrentAccountActiveShoppingList(),
                            v -> v == null ? null : v.getCenterLatitudeRange())
            );
        }
        return mCurrentAccountActiveShoppingListCenterLatitudeRange;
    }

    public void setCurrentAccountActiveShoppingListCenterLatitudeRange(double latitude) {
        ((SettableLiveData<Double>) getCurrentAccountActiveShoppingListCenterLatitudeRange())
                .setValue(latitude);
    }



    private MutableLiveData<String> mCenterPlaceName;

    private MutableLiveData<String> obtainCenterPlaceName() {
        if (mCenterPlaceName == null) {
            mCenterPlaceName = new MutableLiveData<>();
        }
        return mCenterPlaceName;
    }

    public LiveData<String> getCenterPlaceName() {
        return obtainCenterPlaceName();
    }

    public void setCenterPlaceName(String name) {
        obtainCenterPlaceName().setValue(name);
    }


    /*
    ************************************************************************************************
    * get total
    ************************************************************************************************
     */

    private LiveData<List<ShoppingListProduct>> mCurrentAccountActiveShoppingListProducts;
    private LiveData<List<ShoppingListProduct>> findCurrentAccountActiveShoppingListProducts() {
        if (mCurrentAccountActiveShoppingListProducts == null) {
            mCurrentAccountActiveShoppingListProducts = Transformations.switchMap(
                    findCurrentAccountActiveShoppingList(),
                    list -> list == null ? null : ShoppingListProductRepository.getInstance(getApplication())
                            .findShoppingListProducts(list.getShoppingListId())
            );
        }
        return mCurrentAccountActiveShoppingListProducts;
    }

    private LiveData<Double> mCurrentAccountActiveShoppingListTotal;
    public LiveData<Double> findCurrentAccountActiveShoppingListTotal() {
        if (mCurrentAccountActiveShoppingListTotal == null) {
            mCurrentAccountActiveShoppingListTotal = Transformations.map(
                    findCurrentAccountActiveShoppingListProducts(),
                    products -> {
                        if (products == null) {
                            return null;
                        }
                        double total = 0.0;
                        for (ShoppingListProduct product : products) {
                            if (!product.getBestStorePrices().isEmpty()) {
                                total += product.getBestStorePrices().get(0)
                                        .getPriceValue(product.getQuantity());
                            }
                        }
                        return total;
                    }
            );
        }
        return mCurrentAccountActiveShoppingListTotal;
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
    public LivePromise0<Integer, Void> updateBestPriceRelations() {
        ShoppingList shoppingList = findCurrentAccountActiveShoppingList().getValue();
        Double centerLongitude = getCurrentAccountActiveShoppingListCenterLongitude().getValue();
        Double centerLatitude = getCurrentAccountActiveShoppingListCenterLatitude().getValue();
        Double centerLongitudeRange = getCurrentAccountActiveShoppingListCenterLongitudeRange().getValue();
        Double centerLatitudeRange = getCurrentAccountActiveShoppingListCenterLatitudeRange().getValue();

        if (shoppingList == null || centerLongitude == null || centerLatitude == null || centerLongitudeRange == null || centerLatitudeRange == null) {
            MutableLivePromise0<Integer, Void> promise = new MutableLivePromise0<>();
            promise.setRejectValue(null);
            return promise;
        }

        if (mUpdater == null) {
            ShoppingList copy = new ShoppingList(shoppingList);
            copy.setCenterLongitude(centerLongitude);
            copy.setCenterLatitude(centerLatitude);
            copy.setCenterLongitudeRange(centerLongitudeRange);
            copy.setCenterLatitudeRange(centerLatitudeRange);
            mUpdater = new ShoppingListBestPriceRelationsUpdater(copy);
            mUpdater.getPromise().observeForeverResolve(v -> mUpdater = null);
            mUpdater.getPromise().observeForeverReject(v -> mUpdater = null);
            mUpdater.execute();
        }
        return mUpdater.getPromise();
    }

    @SuppressLint("StaticFieldLeak")
    private class ShoppingListBestPriceRelationsUpdater extends AsyncTask<Void, Void, Void> {

        private final ShoppingList mShoppingList;
        private final MutableLivePromise0<Integer, Void> mPromise;

        private ShoppingListBestPriceRelationsUpdater(ShoppingList shoppingList) {
            mShoppingList = shoppingList;
            mPromise = new MutableLivePromise0<>();
        }

        private LivePromise0<Integer, Void> getPromise() {
            return mPromise;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ShoppingListRepository.getInstance(getApplication())
                    .updateShoppingList(mShoppingList);

            BestPriceRelationRepository.getInstance(getApplication())
                    .refreshBestPriceRelation(mShoppingList.getShoppingListId());

            mPromise.postResolveValue(1);

            return null;
        }
    }

//    private class ShoppingListBestPriceRelationsUpdater {
//
//        private Set<String> mShoppingListIds;
//        private MutableLivePromise0<Integer, Void> mPromise;
//
//        private LivePromise0<Integer, Void> update(BaseActivity activity, String shoppingListId) {
//            if (mShoppingListIds == null) {
//                mShoppingListIds = new ArraySet<>();
//            }
//
//            mShoppingListIds.add(shoppingListId);
//
//            if (mPromise == null) {
//                mPromise = new MutableLivePromise0<>();
//
//                LivePromise0<Location, Void> promise = LocationManager.getInstance(getApplication())
//                        .updateLocation(activity);
//                promise.observeResolve(activity, this::onLocationUpdated);
//                promise.observeReject(activity, v -> onLocationUpdateFailed());
//            }
//
//            return mPromise;
//        }
//
//        private void onLocationUpdateFailed() {
//            mPromise.setRejectValue(null);
//            mPromise = null;
//        }
//
//        @SuppressLint("StaticFieldLeak")
//        private void onLocationUpdated(Location location) {
//            String text = "Longtitude: " + location.getLongitude() + ", Latitude: " + location.getLatitude();
//            Toast.makeText(getApplication(), text, Toast.LENGTH_SHORT).show();
//
//            mUpdater = null;
//
//            new AsyncTask<Void, Void, Integer>() {
//
//                @Override
//                protected Integer doInBackground(Void... voids) {
//                    List<ShoppingList> listToUpdate = new ArrayList<>();
//                    ShoppingListRepository repository = ShoppingListRepository.getInstance(getApplication());
//
//                    for (String id : mShoppingListIds) {
//
//                        ShoppingList shoppingList = repository.findShoppingListNow(id);
//
//                        shoppingList.setCenterLongitude(location.getLongitude());
//                        shoppingList.setCenterLatitude(location.getLatitude());
//
//                        listToUpdate.add(shoppingList);
//                    }
//
//                    return repository.updateShoppingList(listToUpdate
//                            .toArray(new ShoppingList[listToUpdate.size()]));
//                }
//
//                @Override
//                protected void onPostExecute(Integer integer) {
//                    BestPriceRelationRepository repository = BestPriceRelationRepository.getInstance(getApplication());
//                    for (String id : mShoppingListIds) {
//                        repository.refreshBestPriceRelation(id);
//                    }
//                }
//
//            }.execute();
//        }
//
//    }

}
