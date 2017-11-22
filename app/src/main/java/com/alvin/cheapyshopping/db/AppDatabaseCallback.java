package com.alvin.cheapyshopping.db;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;

import com.alvin.cheapyshopping.db.entities.Account;
import com.alvin.cheapyshopping.db.entities.Price;
import com.alvin.cheapyshopping.db.entities.Product;
import com.alvin.cheapyshopping.db.entities.ShoppingList;
import com.alvin.cheapyshopping.db.entities.ShoppingListProductRelation;
import com.alvin.cheapyshopping.db.entities.Store;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
                appDb.getAccountDao().insertAccount(account);
                account = appDb.getAccountDao().getAllAccountsNow().get(0);

                ShoppingList list = new ShoppingList();
                list.setName("Shopping List 0");
                list.setCreationTime(new Date());
                list.setForeignAccountId(account.getAccountId());
                appDb.getShoppingListDao().insertShoppingList(list);
                list = appDb.getShoppingListDao()
                        .findAccountShoppingListsNow(account.getAccountId()).get(0);

                account.setActiveShoppingListId(list.getShoppingListId());
                appDb.getAccountDao().updateAccount(account);

                List<Product> products = new ArrayList<>();
                for (int i = 0; i < 10; ++i) {
                    Product product = new Product();
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
                for (int i = 0; i < 4; ++i) {
                    Store store = new Store();
                    store.setName("Store " + i);
                    store.setLocation("Location " + i);
                    stores.add(store);
                }
                appDb.getStoreDao()
                        .insertStore(stores.toArray(new Store[stores.size()]));
                stores = appDb.getStoreDao().getAllStoresNow();


                List<Price> prices = new ArrayList<>();
                for (Product product : products) {
                    Price price = new Price();
                    price.setType(Price.TYPE_SINGLE);
                    price.setTotal(100);
                    price.setForeignProductId(product.getProductId());
                    price.setForeignStoreId(stores.get((stores.size() + prices.size()) / stores.size()).getStoreId());
                    prices.add(price);
                }
                appDb.getPriceDao()
                        .insertPrice(prices.toArray(new Price[prices.size()]));


            }
        }).start();

    }
}
