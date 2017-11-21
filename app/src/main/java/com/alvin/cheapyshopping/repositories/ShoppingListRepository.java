package com.alvin.cheapyshopping.repositories;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.Transformations;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.ArrayMap;
import android.util.Pair;

import com.alvin.cheapyshopping.room.AppDatabase;
import com.alvin.cheapyshopping.room.daos.ShoppingListDao;
import com.alvin.cheapyshopping.room.daos.ShoppingListProductDao;
import com.alvin.cheapyshopping.room.daos.ShoppingListProductDao.ShoppingListProductDetail;
import com.alvin.cheapyshopping.room.entities.Price;
import com.alvin.cheapyshopping.room.entities.Product;
import com.alvin.cheapyshopping.room.entities.ShoppingList;
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

public class ShoppingListRepository {

    private static ShoppingListRepository sInstance;

    public static ShoppingListRepository getInstance(Context context) {
        if (sInstance == null) {
            AppDatabase db = AppDatabase.getInstance(context);
            sInstance = new ShoppingListRepository(
                    db.getShoppingListDao(),
                    ShoppingListProductRepository.getInstance(context),
                    ProductRepository.getInstance(context),
                    PriceRepository.getInstance(context),
                    StoreRepository.getInstance(context)
            );
        }
        return sInstance;
    }


    private final ShoppingListDao mDao;

    private final ShoppingListProductRepository mListProductRepository;
    private final ProductRepository mProductRepository;
    private final PriceRepository mPriceRepository;
    private final StoreRepository mStoreRepository;

    private final Map<Long, LiveData<Map<Store, List<ShoppingListProductDetail>>>> mResultCache;
    private LiveData<Map<Store, List<ShoppingListProductDetail>>> mResultCacheOfLatest;

    private LiveData<ShoppingList> mLatest;
    private LiveData<List<ShoppingListProduct>> mLatestProducts;
    private LiveData<Map<Store, List<Product>>> mComputedResult;


    private ShoppingListRepository(ShoppingListDao dao,
                                   ShoppingListProductRepository listProductRepository,
                                   ProductRepository productRepository,
                                   PriceRepository priceRepository,
                                   StoreRepository storeRepository) {
        this.mDao = dao;
        this.mListProductRepository = listProductRepository;
        this.mProductRepository = productRepository;
        this.mPriceRepository = priceRepository;
        this.mStoreRepository = storeRepository;

        this.mResultCache = new ArrayMap<>();
    }

    /*
    ************************************************************************************************
    * get ShoppingList
    ************************************************************************************************
     */
    private LiveData<List<ShoppingList>> mAll;
    public LiveData<List<ShoppingList>> getAll() {
        if (this.mAll == null) {
            this.mAll = this.mDao.getAll();
        }
        return this.mAll;
    }


    public LiveData<Map<Store, List<ShoppingListProductDetail>>> getCachedResultById(long shoppingListId) {
        if (!this.mResultCache.containsKey(shoppingListId)) {
            this.mResultCache.put(shoppingListId, new CachedResult(shoppingListId));
        }
        return this.mResultCache.get(shoppingListId);
    }

    public LiveData<Map<Store, List<ShoppingListProductDetail>>> getCachedResultOfLatest() {
        if (this.mResultCacheOfLatest == null) {
            this.mResultCacheOfLatest = Transformations.switchMap(this.findLatest(), new Function<ShoppingList, LiveData<Map<Store, List<ShoppingListProductDetail>>>>() {
                @Override
                public LiveData<Map<Store, List<ShoppingListProductDetail>>> apply(ShoppingList input) {
                    return input != null ?
                            ShoppingListRepository.this.getCachedResultById(input.getShoppingListId())
                            : null;
                }
            });
        }
        return this.mResultCacheOfLatest;
    }

    public LiveData<ShoppingList> findLatest() {
        if (this.mLatest == null) {
            this.mLatest = this.mDao.findLatest();
        }
        return this.mLatest;
    }

    public LiveData<List<ShoppingListProduct>> findLatestListProducts() {
        if (this.mLatestProducts == null) {
            this.mLatestProducts = Transformations.switchMap(this.findLatest(), new Function<ShoppingList, LiveData<List<ShoppingListProduct>>>() {
                @Override
                public LiveData<List<ShoppingListProduct>> apply(ShoppingList input) {
                    return input != null ?
                            ShoppingListRepository.this.mListProductRepository.findByShoppingListId(input.getShoppingListId())
                            : null;
                }
            });
        }
        return this.mLatestProducts;
    }


    public LiveData<List<Pair<ShoppingListProduct, List<Price>>>> findLatestRelationsWithPrice() {
        return null;
    }

    public LiveData<Map<Store, List<Product>>> getResult() {
        if (this.mComputedResult == null) {
            this.mComputedResult = new LiveResult();
        }
        return this.mComputedResult;
    }


    public long[] insert(ShoppingList... stores) {
        return this.mDao.insert(stores);
    }

    public int update(ShoppingList... stores) {
        return this.mDao.update(stores);
    }

    public int delete(ShoppingList... stores) {
        return this.mDao.delete(stores);
    }



    private class CachedResult extends MediatorLiveData<Map<Store, List<ShoppingListProductDetail>>> {

        private List<ShoppingListProductDetail> mDetails;

        private ExecutorService mExecutor;
        private Future<?> mExecutingJob;

        private CachedResult(long shoppingListId) {
            final ShoppingListRepository repository = ShoppingListRepository.this;
            this.addSource(repository.mListProductRepository.findDetailsByShoppingListId(shoppingListId),
                    new Observer<List<ShoppingListProductDetail>>() {
                        @Override
                        public void onChanged(@Nullable List<ShoppingListProductDetail> shoppingListProductDetails) {
                            CachedResult.this.mDetails = shoppingListProductDetails;
                            CachedResult.this.update();
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
                private final List<ShoppingListProductDetail> mDetails = CachedResult.this.mDetails;
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
                        Store store = ShoppingListRepository.this.mStoreRepository.findByIdNow(entry.getKey());
                        result.put(store, entry.getValue());
                    }

                    if (!Thread.interrupted()) CachedResult.this.postValue(result);
                }
            });
        }

    }

    private class LiveResult extends MediatorLiveData<Map<Store, List<Product>>> {

        private ExecutorService mExecutor;
        private Future<?> mExecutingJob;

        private List<ShoppingListProduct> mRelations;
        private List<Store> mNearbyStores;

        private LiveResult() {
            this.addSource(ShoppingListRepository.this.findLatestListProducts(), new Observer<List<ShoppingListProduct>>() {
                @Override
                public void onChanged(@Nullable List<ShoppingListProduct> relations) {
                    LiveResult.this.mRelations = relations;
                    LiveResult.this.compute();
                }
            });
            this.addSource(ShoppingListRepository.this.mStoreRepository.findNearbyStores(), new Observer<List<Store>>() {
                @Override
                public void onChanged(@Nullable List<Store> stores) {
                    LiveResult.this.mNearbyStores = stores;
                    LiveResult.this.compute();
                }
            });
        }


        private void cancelExecutingJob() {
            if (this.mExecutingJob != null) {
                this.mExecutingJob.cancel(true);
                this.mExecutingJob = null;
            }
        }

        private ExecutorService getExecutor() {
            if (this.mExecutor == null) {
                this.mExecutor = Executors.newSingleThreadExecutor();
            }
            return mExecutor;
        }

        private void compute() {
            if (this.mExecutingJob != null) {
                this.mExecutingJob.cancel(true);
                this.mExecutingJob = null;
            }
            if (this.mRelations == null || this.mNearbyStores == null) {
                this.setValue(null);
                return;
            }
            if (this.mExecutor == null) {
                this.mExecutor = Executors.newSingleThreadExecutor();
            }
            this.mExecutingJob = this.mExecutor.submit(new Runnable() {
                private final List<ShoppingListProduct> mRelations = LiveResult.this.mRelations;
                private final List<Store> mNearbyStores = LiveResult.this.mNearbyStores;
                @Override
                public void run() {
                    Map<Store, List<Product>> result = new ArrayMap<>();

                    List<Long> nearbyStoreIds = new ArrayList<>(this.mNearbyStores.size());
                    for (Store store : this.mNearbyStores) {
                        nearbyStoreIds.add(store.getStoreId());
                    }

                    Map<Long, Map<Product, List<Price>>> idResult = new ArrayMap<>();
                    // TODO check if we should override equal and hashCode method of the entities

                    for (ShoppingListProduct relation : this.mRelations) {
                        List<Price> bestPrices = ShoppingListRepository.this.mPriceRepository.findBestPriceOfProductNow(
                                relation.getForeignProductId(),
                                nearbyStoreIds,
                                relation.getQuantity()
                        );
                        if (!bestPrices.isEmpty()) {
                            Price bestPrice = bestPrices.get(0);
                            relation.setForeignPriceId(bestPrice.getPriceId());
                        } else {
                            relation.setForeignPriceId(null);
                        }
                    }

                    if (Thread.interrupted()) return;

                    ShoppingListRepository.this.mListProductRepository.update(
                            this.mRelations.toArray(new ShoppingListProduct[this.mRelations.size()])
                    );

                    if (Thread.interrupted()) return;

                    if (!Thread.interrupted()) LiveResult.this.postValue(result);
                }
            });
        }

        private void readFromCache() {
            final ShoppingListRepository repository = ShoppingListRepository.this;


        }

    }

}

