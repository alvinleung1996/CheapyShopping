package com.alvin.cheapyshopping.db;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;

import com.alvin.cheapyshopping.db.entities.Account;
import com.alvin.cheapyshopping.db.entities.Price;
import com.alvin.cheapyshopping.db.entities.Product;
import com.alvin.cheapyshopping.db.entities.Rank;
import com.alvin.cheapyshopping.db.entities.ShoppingList;
import com.alvin.cheapyshopping.db.entities.ShoppingListProductRelation;
import com.alvin.cheapyshopping.db.entities.Store;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by Alvin on 21/11/2017.
 */

public class AppDatabaseCallback extends RoomDatabase.Callback {

    private final Context mContext;

    AppDatabaseCallback(Context context) {
        this.mContext = context.getApplicationContext();
    }

    @Override
    public void onCreate(@NonNull SupportSQLiteDatabase db) {
        super.onCreate(db);

        new Thread(new Runnable() {
            @Override
            public void run() {

                AppDatabase appDb = AppDatabase.getInstance(mContext);

                Account account = new Account();
                account.setAccountId(UUID.randomUUID().toString());
                account.setAccountName("Account 0");
                appDb.getAccountDao().insertAccount(account);
                account = appDb.getAccountDao().getAllAccountsNow().get(0);

                ShoppingList list = new ShoppingList();
                list.setShoppingListId(UUID.randomUUID().toString());
                list.setName("Shopping List 0");
                list.setCreationTime(Calendar.getInstance());
                list.setForeignAccountId(account.getAccountId());
                appDb.getShoppingListDao().insertShoppingList(list);
                list = appDb.getShoppingListDao()
                        .findAccountShoppingListsNow(account.getAccountId()).get(0);

                account.setActiveShoppingListId(list.getShoppingListId());
                appDb.getAccountDao().updateAccount(account);

                List<Product> products = new ArrayList<>();
                for (int i = 0; i < 10; ++i) {
                    Product product = new Product();
                    product.setProductId(UUID.randomUUID().toString());
                    product.setName("Product " + i);
                    product.setDescription("I am product number " + i);
                    products.add(product);
                }
                appDb.getProductDao()
                        .insertProduct(products.toArray(new Product[products.size()]));
                products = appDb.getProductDao().getAllProductsNow();

                List<ShoppingListProductRelation> listProducts = new ArrayList<>();
                for (int i = 0; i < 7; ++i) {
                    ShoppingListProductRelation listProduct = new ShoppingListProductRelation();
                    listProduct.setForeignShoppingListId(list.getShoppingListId());
                    listProduct.setForeignProductId(products.get(i).getProductId());
                    listProduct.setQuantity(i);
                    listProducts.add(listProduct);
                }
                appDb.getShoppingListProductRelationDao()
                        .insertShoppingListProductRelation(listProducts.toArray(new ShoppingListProductRelation[listProducts.size()]));

                List<Store> stores = new ArrayList<>();
                stores.add(this.newStore("ChIJW6SHeIT_AzQR9bGVjdwkzuA", "百佳", "Parknshop - Hill Road", 114.135282, 22.2861835));
                stores.add(this.newStore("ChIJ07jUtoT_AzQRFzl3vPWyvto", "西寶城", "石塘咀卑路乍街8號", 114.135403, 22.2861676));
                stores.add(this.newStore("ChIJsz0z94P_AzQR_Ek-nv0PCgQ", "豐澤", "G/F, REAR PORTION, WAH MING CENTRE, 396 DES VOEUX ROAD WEST, Shek Tong Tsui", 114.1367759, 22.2861362));
                stores.add(this.newStore("ChIJbUt0XoP_AzQRcVo_QMDYI4M","一田超市", "石塘咀干諾道西188號香港商業中心地下", 114.1361631, 22.2870644));
                stores.add(this.newStore("ChIJ47sSZIb_AzQRysVCSOvGlvQ", "百佳hku", "Chong Yuet Ming Cultural Centre, Lung Fu Shan", 114.1388094, 22.2825287));
//                for (int i = 0; i < 4; ++i) {
//                    Store store = new Store();
//                    store.setName("Store " + i);
//                    store.setAddress("ShoppingListLocationUpdater " + i);
//                    stores.add(store);
//                }
                appDb.getStoreDao()
                        .insertStore(stores.toArray(new Store[stores.size()]));
                stores = appDb.getStoreDao().getAllStoresNow();


                List<Price> prices = new ArrayList<>();
                for (Product product : products) {
                    Price price = new Price();
                    price.setPriceId(UUID.randomUUID().toString());
                    price.setType(Price.TYPE_SINGLE);
                    price.setTotal(100);
                    price.setCreationTime(Calendar.getInstance());
                    price.setForeignProductId(product.getProductId());
                    price.setForeignStoreId(stores.get((stores.size() + prices.size()) / stores.size()).getStoreId());
                    prices.add(price);
                }

//                Price price = new Price();
//                price.setType(Price.TYPE_SINGLE);
//                price.setTotal(30);
//                price.setForeignProductId(1);
//                price.setForeignStoreId(3);
//                price.setCreationTime(Calendar.getInstance());
//                prices.add(price);

                appDb.getPriceDao()
                        .insertPrice(prices.toArray(new Price[prices.size()]));

                List<Rank> ranks = new ArrayList<>();
                int rankScoreIncrement = 500;
                for (int i = 0; i <= 10 ;i++){
                    Rank rank = new Rank();
                    rank.setRankId(UUID.randomUUID().toString());
                    rank.setRank("Rank " + i);
                    rank.setMinScore(i * rankScoreIncrement);
                    rank.setMaxScore(rank.getMinScore() + rankScoreIncrement);

                    ranks.add(rank);
                }

                appDb.getRankDao()
                        .insertRank(ranks.toArray(new Rank[ranks.size()]));



            }

            private Store newStore(String storeId, String name, String location, double longitude, double latitude) {
                Store store = new Store();
                store.setStoreId(storeId);
                store.setName(name);
                store.setAddress(location);
                store.setLongitude(longitude);
                store.setLatitude(latitude);
                return store;
            }
        }).start();

    }
}
