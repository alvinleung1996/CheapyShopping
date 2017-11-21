package com.alvin.cheapyshopping.repositories;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.ArrayMap;

import com.alvin.cheapyshopping.room.AppDatabase;
import com.alvin.cheapyshopping.room.daos.ShoppingListProductDao;
import com.alvin.cheapyshopping.room.daos.ShoppingListProductDao.ShoppingListProductDetail;
import com.alvin.cheapyshopping.room.entities.Price;
import com.alvin.cheapyshopping.room.entities.ShoppingListProduct;
import com.alvin.cheapyshopping.room.entities.Store;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by Alvin on 20/11/2017.
 */

public class ShoppingListProductRepository {

    private static ShoppingListProductRepository sInstance;

    public static ShoppingListProductRepository getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new ShoppingListProductRepository(context);
        }
        return sInstance;
    }


    private final Context mContext;

    private ShoppingListProductDao mShoppingListProductDao;
    private BestPriceRepository mBestPriceRepository;
    private StoreRepository mStoreRepository;



    private ShoppingListProductRepository(Context context) {
        this.mContext = context.getApplicationContext();
    }

    private ShoppingListProductDao getShoppingListDao() {
        if (this.mShoppingListProductDao == null) {
            this.mShoppingListProductDao = AppDatabase.getInstance(this.mContext).getShoppingListProductDao();
        }
        return this.mShoppingListProductDao;
    }

    private BestPriceRepository getBestPriceRepository() {
        if (this.mBestPriceRepository == null) {
            this.mBestPriceRepository = BestPriceRepository.getInstance(this.mContext);
        }
        return this.mBestPriceRepository;
    }

    private StoreRepository getStoreRepository() {
        if (this.mStoreRepository == null) {
            this.mStoreRepository = StoreRepository.getInstance(this.mContext);
        }
        return this.mStoreRepository;
    }



    /*
    ************************************************************************************************
    * get shopping list product detail
    ************************************************************************************************
     */

    private Map<Long, LiveData<List<ShoppingListProductDetail>>> mShoppingListProductDetailCache;

    public LiveData<List<ShoppingListProductDetail>> findShoppingListProductDetails(long shoppingListId) {
        if (this.mShoppingListProductDetailCache == null) {
            this.mShoppingListProductDetailCache = new ArrayMap<>();
        }
        if (!this.mShoppingListProductDetailCache.containsKey(shoppingListId)) {
            this.mShoppingListProductDetailCache.put(shoppingListId, new LiveShoppingListProductDetails(shoppingListId));
        }
        return this.mShoppingListProductDetailCache.get(shoppingListId);
    }

    /**
     * Class to merge ShoppingListProduct with Price pointed by BestPrice,
     * they cannot be fetch together by Room
     */
    private class LiveShoppingListProductDetails extends MediatorLiveData<List<ShoppingListProductDetail>> {

        private List<ShoppingListProductDetail> mDetails;

        private ExecutorService mExecutor;
        private Future<?> mExecutingJob;

        private LiveShoppingListProductDetails(long shoppingListId) {
            final ShoppingListProductRepository repository = ShoppingListProductRepository.this;
            this.addSource(
                repository.getShoppingListDao().findDetailsByShoppingListId(shoppingListId),
                new Observer<List<ShoppingListProductDetail>>() {
                    @Override
                    public void onChanged(@Nullable List<ShoppingListProductDetail> shoppingListProductDetails) {
                        LiveShoppingListProductDetails.this.mDetails = shoppingListProductDetails;
                        LiveShoppingListProductDetails.this.update();
                    }
                }
            );
        }

        private void update() {
            if (this.mExecutingJob != null) {
                this.mExecutingJob.cancel(true);
                this.mExecutingJob = null;
            }
            if (this.mExecutor == null) {
                this.mExecutor = Executors.newSingleThreadExecutor();
            }
            if (this.mDetails == null) {
                this.setValue(null);
                return;
            }
            this.mExecutingJob = this.mExecutor.submit(new Runnable() {

                private final List<ShoppingListProductDetail> mDetails = LiveShoppingListProductDetails.this.mDetails;

                @Override
                public void run() {

                    for (ShoppingListProductDetail detail : this.mDetails) {
                        detail.setBestPrices(
                                ShoppingListProductRepository.this
                                        .getBestPriceRepository()
                                        .findShoppingListProductBestPricesNow(
                                                detail.getForeignShoppingListId(),
                                                detail.getProductId()
                                        )
                        );

                        if (Thread.interrupted()) return;
                    }

                    if (!Thread.interrupted()) LiveShoppingListProductDetails.this.postValue(this.mDetails);

                }
            });
        }

    }



    /*
    ************************************************************************************************
    * get shopping list result
    ************************************************************************************************
     */

    private Map<Long, LiveData<Map<Store, List<ShoppingListProductDetail>>>> mShoppingListResultCache;

    public LiveData<Map<Store, List<ShoppingListProductDetail>>> getShoppingListResult(long shoppingListId) {
        if (this.mShoppingListResultCache == null) {
            this.mShoppingListResultCache = new ArrayMap<>();
        }
        if (!this.mShoppingListResultCache.containsKey(shoppingListId)) {
            this.mShoppingListResultCache.put(shoppingListId, new LiveShoppingListResult(shoppingListId));
        }
        return this.mShoppingListResultCache.get(shoppingListId);
    }

    private class LiveShoppingListResult extends MediatorLiveData<Map<Store, List<ShoppingListProductDetail>>> {

        private List<ShoppingListProductDetail> mDetails;

        private ExecutorService mExecutor;
        private Future<?> mExecutingJob;

        private LiveShoppingListResult(long shoppingListId) {
            final ShoppingListProductRepository repository = ShoppingListProductRepository.this;
            this.addSource(
                repository.findShoppingListProductDetails(shoppingListId),
                new Observer<List<ShoppingListProductDetail>>() {
                    @Override
                    public void onChanged(@Nullable List<ShoppingListProductDetail> shoppingListProductDetails) {
                        LiveShoppingListResult.this.mDetails = shoppingListProductDetails;
                        LiveShoppingListResult.this.update();
                    }
                }
            );
        }

        private void update() {
            if (this.mExecutingJob != null) {
                this.mExecutingJob.cancel(true);
                this.mExecutingJob = null;
            }
            if (this.mDetails == null) {
                this.setValue(null);
            }
            if (this.mExecutor == null) {
                this.mExecutor = Executors.newSingleThreadExecutor();
            }
            this.mExecutingJob = this.mExecutor.submit(new Runnable() {

                private final List<ShoppingListProductDetail> mDetails = LiveShoppingListResult.this.mDetails;

                @Override
                public void run() {
                    Map<Long, List<ShoppingListProductDetail>> idResult = new ArrayMap<>();

                    for (ShoppingListProductDetail detail : this.mDetails) {

                        List<Price> bestPrices = detail.getBestPrices();

                        if (bestPrices.size() > 0) {

                            long bestPriceId = bestPrices.get(0).getForeignStoreId();
                            if (!idResult.containsKey(bestPriceId)) {
                                idResult.put(bestPriceId, new ArrayList<ShoppingListProductDetail>());
                            }
                            idResult.get(bestPriceId).add(detail);

                        } else {
                            if (!idResult.containsKey(null)) {
                                idResult.put(null, new ArrayList<ShoppingListProductDetail>());
                            }
                            idResult.get(null).add(detail);
                        }
                    }

                    if (Thread.interrupted()) return;

                    Map<Store, List<ShoppingListProductDetail>> result = new ArrayMap<>();

                    for (Map.Entry<Long, List<ShoppingListProductDetail>> entry : idResult.entrySet()) {

                        Store store = entry.getKey() == null ? null
                            : ShoppingListProductRepository.this
                                .getStoreRepository()
                                .findStoreByIdNow(entry.getKey());

                        result.put(store, entry.getValue());
                    }

                    if (!Thread.interrupted()) LiveShoppingListResult.this.postValue(result);
                }
            });
        }
    }


    /*
    ************************************************************************************************
    * Other
    ************************************************************************************************
     */

    public long[] insert(ShoppingListProduct... relations) {
        return this.mShoppingListProductDao.insert(relations);
    }

    public int update(ShoppingListProduct... relations) {
        return this.mShoppingListProductDao.update(relations);
    }



}
