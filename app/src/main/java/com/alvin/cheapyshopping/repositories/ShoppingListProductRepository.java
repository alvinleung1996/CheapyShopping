package com.alvin.cheapyshopping.repositories;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.ArrayMap;

import com.alvin.cheapyshopping.db.AppDatabase;
import com.alvin.cheapyshopping.db.daos.ShoppingListProductDao;
import com.alvin.cheapyshopping.db.daos.ShoppingListProductRelationDao;
import com.alvin.cheapyshopping.db.entities.Price;
import com.alvin.cheapyshopping.db.entities.Store;
import com.alvin.cheapyshopping.db.entities.pseudo.ShoppingListProduct;
import com.alvin.cheapyshopping.db.entities.pseudo.StorePrice;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Alvin on 22/11/2017.
 */

public class ShoppingListProductRepository {

    @SuppressLint("StaticFieldLeak")
    private static ShoppingListProductRepository sInstance;

    public static ShoppingListProductRepository getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new ShoppingListProductRepository(context);
        }
        return sInstance;
    }


    private final Context mContext;

    private ShoppingListProductRepository(Context context) {
        this.mContext = context.getApplicationContext();
    }


    /*
    ************************************************************************************************
    * Dao
    ************************************************************************************************
     */

    private ShoppingListProductDao mShoppingListProductDao;
    private ShoppingListProductDao getShoppingListProductDao() {
        if (this.mShoppingListProductDao == null) {
            this.mShoppingListProductDao = AppDatabase.getInstance(this.mContext).getShoppingListProductDao();
        }
        return this.mShoppingListProductDao;
    }


    /*
    ************************************************************************************************
    * Repository
    ************************************************************************************************
     */

    private StorePriceRepository mStorePriceRepository;
    private StorePriceRepository getStorePriceRepository() {
        if (this.mStorePriceRepository == null) {
            this.mStorePriceRepository = StorePriceRepository.getInstance(this.mContext);
        }
        return this.mStorePriceRepository;
    }


    /*
    ************************************************************************************************
    * Query, Async
    ************************************************************************************************
     */

    private Map<Long, LiveData<List<ShoppingListProduct>>> mShoppingListProductsCache;
    public LiveData<List<ShoppingListProduct>> findShoppingListProducts(long shoppingListId) {
        if (this.mShoppingListProductsCache == null) {
            this.mShoppingListProductsCache = new ArrayMap<>();
        }
        if (!this.mShoppingListProductsCache.containsKey(shoppingListId)) {
            this.mShoppingListProductsCache.put(shoppingListId,
                    new ShoppingListProductAssembler(shoppingListId));
        }
        return this.mShoppingListProductsCache.get(shoppingListId);
    }

    private class ShoppingListProductAssembler extends MediatorLiveData<List<ShoppingListProduct>> {

        private List<LiveData<List<StorePrice>>> mSources;
        private Set<ShoppingListProduct> mNotReadyProducts;
        private Set<ShoppingListProduct> mReadyProducts;

        private ShoppingListProductAssembler(final long shoppingListId) {
            this.addSource(
                ShoppingListProductRepository.this.findPartialShoppingListProducts(shoppingListId),
                new Observer<List<ShoppingListProduct>>() {
                    @Override
                    public void onChanged(@Nullable List<ShoppingListProduct> partialShoppingListProducts) {
                        ShoppingListProductAssembler.this
                                .onPartialShoppingListProductsChanged(shoppingListId, partialShoppingListProducts);
                    }
                }
            );
        }

        private void onPartialShoppingListProductsChanged(final long shoppingListId, List<ShoppingListProduct> partialShoppingListProducts) {

            if (this.mSources != null) {
                for (LiveData<List<StorePrice>> source : this.mSources) {
                    this.removeSource(source);
                }
            }

            this.mSources = null;
            this.mNotReadyProducts = null;
            this.mReadyProducts = null;

            if (partialShoppingListProducts == null) {
                this.setValue(null);
                return;
            }

            this.mSources = new ArrayList<>();
            this.mNotReadyProducts = new TreeSet<>();
            this.mReadyProducts = new TreeSet<>();

            StorePriceRepository storePriceRepository = ShoppingListProductRepository.this.getStorePriceRepository();

            for (final ShoppingListProduct product : partialShoppingListProducts) {

                this.mNotReadyProducts.add(product);

                LiveData<List<StorePrice>> source = storePriceRepository
                        .findShoppingListProductBestStorePrices(shoppingListId, product.getProductId());

                this.mSources.add(source);

                this.addSource(
                    source,
                    new Observer<List<StorePrice>>() {
                        @Override
                        public void onChanged(@Nullable List<StorePrice> bestPrices) {
                            ShoppingListProductAssembler.this.onBestStorePriceChanged(product, bestPrices);
                        }
                    }
                );
            }
        }

        private void onBestStorePriceChanged(ShoppingListProduct shoppingListProduct, List<StorePrice> bestPrices) {

            shoppingListProduct.setBestStorePrices(bestPrices);

            if (this.mNotReadyProducts.contains(shoppingListProduct) && bestPrices != null) {
                this.mNotReadyProducts.remove(shoppingListProduct);
                this.mReadyProducts.add(shoppingListProduct);

            } else if (this.mReadyProducts.contains(shoppingListProduct) && bestPrices == null) {
                this.mReadyProducts.remove(shoppingListProduct);
                this.mNotReadyProducts.add(shoppingListProduct);
            }

            this.onReadyProductsChanged();
        }

        private void onReadyProductsChanged() {
            if (this.mNotReadyProducts.size() == 0) {
                List<ShoppingListProduct> result = new ArrayList<>(this.mReadyProducts);
                this.setValue(result);
            }
        }

    }

    private Map<Long, LiveData<List<ShoppingListProduct>>> mPartialShoppingListProductsCache;
    private LiveData<List<ShoppingListProduct>> findPartialShoppingListProducts(long shoppingListId) {
        if (this.mPartialShoppingListProductsCache == null) {
            this.mPartialShoppingListProductsCache = new ArrayMap<>();
        }
        if (!this.mPartialShoppingListProductsCache.containsKey(shoppingListId)) {
            this.mPartialShoppingListProductsCache.put(shoppingListId,
                    new ShoppingListProductAssembler(shoppingListId));
        }
        return this.mPartialShoppingListProductsCache.get(shoppingListId);
    }


    private Map<Long, LiveData<Map<Store, List<ShoppingListProduct>>>> mGroupedShoppingListProductsCache;
    public LiveData<Map<Store, List<ShoppingListProduct>>> findGroupedShoppingListProducts(long shoppingListId) {
        if (this.mGroupedShoppingListProductsCache == null) {
            this.mGroupedShoppingListProductsCache = new ArrayMap<>();
        }
        if (!this.mGroupedShoppingListProductsCache.containsKey(shoppingListId)) {

        }
        return this.mGroupedShoppingListProductsCache.get(shoppingListId);
    }

    private class ShoppingListProductsGrouper extends MediatorLiveData<Map<Store, List<ShoppingListProduct>>> {

        private ShoppingListProductsGrouper(long shoppingListId) {
            this.addSource(
                ShoppingListProductRepository.this.findShoppingListProducts(shoppingListId),
                new Observer<List<ShoppingListProduct>>() {
                    @Override
                    public void onChanged(@Nullable List<ShoppingListProduct> shoppingListProducts) {
                        ShoppingListProductsGrouper.this
                                .onShoppingListProductsChanged(shoppingListProducts);
                    }
                }
            );
        }

        private void onShoppingListProductsChanged(@Nullable List<ShoppingListProduct> shoppingListProducts) {
            Map<Store, List<ShoppingListProduct>> result = ShoppingListProductRepository.this
                    .groupShoppingListProducts(shoppingListProducts);
            this.setValue(result);
        }

    }


    /*
    ************************************************************************************************
    * Query, Sync
    ************************************************************************************************
     */

    public List<ShoppingListProduct> findShoppingListProductsNow(long shoppingListId) {
        List<ShoppingListProduct> products
                = this.findPartialShoppingListProductsNow(shoppingListId);
        for (ShoppingListProduct product : products) {
            product.setBestStorePrices(this.getStorePriceRepository()
                    .findShoppingListProductBestStorePriceNow(shoppingListId, product.getProductId()));
        }
        return products;
    }

    private List<ShoppingListProduct> findPartialShoppingListProductsNow(long shoppingListId) {
        return this.getShoppingListProductDao().findPartialShoppingListProductsNow(shoppingListId);
    }


    public Map<Store, List<ShoppingListProduct>> findGroupedShoppingListProductsNow(long shoppingListId) {
        List<ShoppingListProduct> shoppingListProducts
                = this.findShoppingListProductsNow(shoppingListId);
        return this.groupShoppingListProducts(shoppingListProducts);
    }



    /*
    ************************************************************************************************
    * Common Method
    ************************************************************************************************
     */

    private Map<Store, List<ShoppingListProduct>> groupShoppingListProducts(
            @Nullable List<ShoppingListProduct> shoppingListProducts) {

        if (shoppingListProducts == null) {
            return null;
        }

        Map<Long, List<ShoppingListProduct>> idResult = new ArrayMap<>();
        Map<Long, Store> storeIdMap = new ArrayMap<>();

        for (ShoppingListProduct product : shoppingListProducts) {

            List<StorePrice> bestPrices = product.getBestStorePrices();

            if (bestPrices.size() > 0) {

                Store store = bestPrices.get(0).getStore();
                long storeId = store.getStoreId();

                if (!idResult.containsKey(storeId)) {
                    idResult.put(storeId, new ArrayList<ShoppingListProduct>());
                }

                idResult.get(storeId).add(product);
                storeIdMap.put(storeId, store);

            } else {

                if (!idResult.containsKey(null)) {
                    idResult.put(null, new ArrayList<ShoppingListProduct>());
                }

                idResult.get(null).add(product);
                storeIdMap.put(null, null);
            }
        }

        Map<Store, List<ShoppingListProduct>> result = new ArrayMap<>();

        for (Map.Entry<Long, List<ShoppingListProduct>> entry : idResult.entrySet()) {

            Store store = storeIdMap.get(entry.getKey());
            result.put(store, entry.getValue());

        }

        return result;
    }
}
