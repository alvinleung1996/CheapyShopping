package com.alvin.cheapyshopping.repositories;

import android.annotation.SuppressLint;
import android.content.Context;

import com.alvin.cheapyshopping.db.AppDatabase;
import com.alvin.cheapyshopping.db.daos.BestPriceRelationDao;
import com.alvin.cheapyshopping.db.entities.BestPriceRelation;
import com.alvin.cheapyshopping.db.entities.Price;
import com.alvin.cheapyshopping.db.entities.Product;
import com.alvin.cheapyshopping.db.entities.ShoppingList;
import com.alvin.cheapyshopping.db.entities.ShoppingListProductRelation;
import com.alvin.cheapyshopping.db.entities.Store;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by Alvin on 21/11/2017.
 */

public class BestPriceRelationRepository {

    @SuppressLint("StaticFieldLeak")
    private static BestPriceRelationRepository sInstance;

    public static BestPriceRelationRepository getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new BestPriceRelationRepository(context);
        }
        return sInstance;
    }


    private final Context mContext;

    private BestPriceRelationRepository(Context context) {
        this.mContext = context.getApplicationContext();
    }


    /*
    ************************************************************************************************
    * Dao
    ************************************************************************************************
     */

    private BestPriceRelationDao mBestPriceRelationDao;
    private BestPriceRelationDao getBestPriceRelationDao() {
        if (this.mBestPriceRelationDao == null) {
            this.mBestPriceRelationDao = AppDatabase.getInstance(this.mContext).getBestPriceRelationDao();
        }
        return this.mBestPriceRelationDao;
    }


    /*
    ************************************************************************************************
    * Repository
    ************************************************************************************************
     */

    private PriceRepository mPriceRepository;
    private PriceRepository getPriceRepository() {
        if (this.mPriceRepository == null) {
            this.mPriceRepository = PriceRepository.getInstance(this.mContext);
        }
        return this.mPriceRepository;
    }


    private ShoppingListProductRelationRepository mShoppingListProductRelationRepository;
    private ShoppingListProductRelationRepository getShoppingListProductRelationRepository() {
        if (this.mShoppingListProductRelationRepository == null) {
            this.mShoppingListProductRelationRepository = ShoppingListProductRelationRepository
                    .getInstance(this.mContext);
        }
        return this.mShoppingListProductRelationRepository;
    }


    private StoreRepository mStoreRepository;
    private StoreRepository getStoreRepository() {
        if (this.mStoreRepository == null) {
            this.mStoreRepository = StoreRepository.getInstance(this.mContext);
        }
        return this.mStoreRepository;
    }

    /*
    ************************************************************************************************
    * Query, Async
    ************************************************************************************************
     */



    /*
    ************************************************************************************************
    * Query, Sync
    ************************************************************************************************
     */




    /*
    ************************************************************************************************
    * Bulk delete shopping list product best price
    ************************************************************************************************
     */

    public int deleteShoppingListProductBestPrice(String shoppingListId, String productId) {
        return this.getBestPriceRelationDao().deleteShoppingListProductBestPrice(shoppingListId, productId);
    }


    /*
    ************************************************************************************************
    * Other
    ************************************************************************************************
     */

    public long[] insertBestPrice(BestPriceRelation... bestPriceRelations) {
        return this.getBestPriceRelationDao().insertBestPrice(bestPriceRelations);
    }

    public int udpateBestPrice(BestPriceRelation... bestPriceRelations) {
        return this.getBestPriceRelationDao().updateBestPrice(bestPriceRelations);
    }

    public int deleteBestPrice(BestPriceRelation... bestPriceRelations) {
        return this.getBestPriceRelationDao().deleteBestPrice(bestPriceRelations);
    }


    /*
    ************************************************************************************************
    * Other
    ************************************************************************************************
     */

    private BestPriceRelationRefresher mBestPriceRelationRefresher;
    public void refreshBestPriceRelation(String shoppingListId) {
        if (this.mBestPriceRelationRefresher == null) {
            this.mBestPriceRelationRefresher = new BestPriceRelationRefresher();
        }
        this.mBestPriceRelationRefresher.refresh(shoppingListId);
    }

    private class BestPriceRelationRefresher {

        private ExecutorService mExecutor;
        private Future<?> mExecutingJob;

        private void refresh(String shoppingListId) {
            if (this.mExecutingJob != null) {
                this.mExecutingJob.cancel(true);
                this.mExecutingJob = null;
            }
            if (this.mExecutor == null) {
                this.mExecutor = Executors.newCachedThreadPool();
            }
            this.mExecutingJob = this.mExecutor.submit(new Job(shoppingListId));
        }

        private class Job implements Runnable {

            private final String shoppingListId;

            private Job(String shoppingListId) {
                this.shoppingListId = shoppingListId;
            }

            @Override
            public void run() {
                List<Store> nearbyStores = BestPriceRelationRepository.this.getStoreRepository()
                        .findNearbyStoresNow();

                List<String> nearByStoreIds = new ArrayList<>(nearbyStores.size());
                for (Store store : nearbyStores) {
                    nearByStoreIds.add(store.getStoreId());
                }

                List<ShoppingListProductRelation> relations = BestPriceRelationRepository.this
                        .getShoppingListProductRelationRepository()
                        .findShoppingListProductRelationsNow(shoppingListId);

                List<BestPriceRelation> bestPrices = new ArrayList<>();

                for (ShoppingListProductRelation relation : relations) {

                    List<Price> prices = BestPriceRelationRepository.this.getPriceRepository()
                            .computeProductBestPriceNow(relation.getForeignProductId(), nearByStoreIds, relation.getQuantity());

                    BestPriceRelationRepository.this.deleteShoppingListProductBestPrice(
                            relation.getForeignShoppingListId(), relation.getForeignProductId());

                    for (Price price : prices) {
                        BestPriceRelation bestPrice = new BestPriceRelation();
                        bestPrice.setForeignShoppingListId(relation.getForeignShoppingListId());
                        bestPrice.setForeignProductId(relation.getForeignProductId());
                        bestPrice.setForeignPriceId(price.getPriceId());
                        bestPrices.add(bestPrice);
                    }
                }

                BestPriceRelationRepository.this
                        .insertBestPrice(bestPrices.toArray(new BestPriceRelation[bestPrices.size()]));
            }
        }

    }

}
