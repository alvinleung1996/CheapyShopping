package com.alvin.cheapyshopping.repositories;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.ArrayMap;

import com.alvin.cheapyshopping.room.AppDatabase;
import com.alvin.cheapyshopping.room.daos.BestPriceDao;
import com.alvin.cheapyshopping.room.daos.ShoppingListProductDao;
import com.alvin.cheapyshopping.room.entities.ShoppingListProduct;

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
            AppDatabase db = AppDatabase.getInstance(context);
            sInstance = new ShoppingListProductRepository(
                    db.getShoppingListProductDao(),
                    db.getBestPriceDao()
            );
        }
        return sInstance;
    }


    private final ShoppingListProductDao mDao;

    private final BestPriceDao mBestPriceDao;

    private final Map<Long, LiveData<List<ShoppingListProduct>>> mCache;

    private Map<Long, LiveData<List<ShoppingListProductDao.ShoppingListProductDetail>>> mDetailCache;


    private ShoppingListProductRepository(ShoppingListProductDao dao, BestPriceDao bestPriceDao) {
        this.mDao = dao;
        this.mBestPriceDao = bestPriceDao;
        this.mCache = new ArrayMap<>();
        this.mDetailCache = new ArrayMap<>();
    }


    public LiveData<List<ShoppingListProduct>> findByShoppingListId(long shoppingListId) {
        if (!this.mCache.containsKey(shoppingListId)) {
            this.mCache.put(shoppingListId, this.mDao.findByShoppingListId(shoppingListId));
        }
        return this.mCache.get(shoppingListId);
    }

    public LiveData<List<ShoppingListProductDao.ShoppingListProductDetail>> findDetailsByShoppingListId(long shoppingListId) {
        if (!this.mDetailCache.containsKey(shoppingListId)) {
            this.mDetailCache.put(shoppingListId, new LiveShoppingListProductDetails(shoppingListId));
        }
        return this.mDetailCache.get(shoppingListId);
    }


    public long[] insert(ShoppingListProduct... relations) {
        return this.mDao.insert(relations);
    }

    public int update(ShoppingListProduct... relations) {
        return this.mDao.update(relations);
    }



    private class LiveShoppingListProductDetails extends MediatorLiveData<List<ShoppingListProductDao.ShoppingListProductDetail>> {

        private final long shoppingListId;
        private List<ShoppingListProductDao.ShoppingListProductDetail> mDetails;

        private ExecutorService mExecutor;
        private Future<?> mExecutingJob;

        private LiveShoppingListProductDetails(long shoppingListId) {
            final ShoppingListProductRepository repository = ShoppingListProductRepository.this;
            this.shoppingListId = shoppingListId;
            this.addSource(repository.mDao.findDetailsByShoppingListId(shoppingListId), new Observer<List<ShoppingListProductDao.ShoppingListProductDetail>>() {
                @Override
                public void onChanged(@Nullable List<ShoppingListProductDao.ShoppingListProductDetail> shoppingListProductDetails) {
                    LiveShoppingListProductDetails.this.mDetails = shoppingListProductDetails;
                    LiveShoppingListProductDetails.this.update();
                }
            });
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
                private final List<ShoppingListProductDao.ShoppingListProductDetail> mDetails = LiveShoppingListProductDetails.this.mDetails;
                @Override
                public void run() {

                    for (ShoppingListProductDao.ShoppingListProductDetail detail : this.mDetails) {
                        detail.setBestPrices(ShoppingListProductRepository.this.mBestPriceDao.findPriceOfShoppingListProductNow(LiveShoppingListProductDetails.this.shoppingListId, detail.getProductId()));
                        if (Thread.interrupted()) return;
                    }

                    if (!Thread.interrupted()) LiveShoppingListProductDetails.this.postValue(this.mDetails);

                }
            });
        }

    }

}
