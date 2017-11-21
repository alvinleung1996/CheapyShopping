package com.alvin.cheapyshopping.room;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;

import com.alvin.cheapyshopping.repositories.PriceRepository;
import com.alvin.cheapyshopping.repositories.StoreRepository;
import com.alvin.cheapyshopping.room.entities.Account;
import com.alvin.cheapyshopping.room.entities.Price;
import com.alvin.cheapyshopping.room.entities.Product;
import com.alvin.cheapyshopping.room.entities.ShoppingList;
import com.alvin.cheapyshopping.room.entities.ShoppingListProduct;
import com.alvin.cheapyshopping.room.entities.Store;

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

                ShoppingList list = new ShoppingList();
                list.setName("Shopping List 0");
                list.setCreationTime(new Date());
                appDb.getShoppingListDao().insert(list);
                list = appDb.getShoppingListDao().getAllNow().get(0);

                Account account = new Account();
                account.setActiveShoppingListId(list.getShoppingListId());
                appDb.getAccountDao().insert(account);
                account = appDb.getAccountDao().getAllNow().get(0);

                List<Product> products = new ArrayList<>();
                for (int i = 0; i < 10; ++i) {
                    Product product = new Product();
                    product.setName("Product " + i);
                    product.setDescription("I am product number " + i);
                    products.add(product);
                }
                appDb.getProductDao()
                        .insert(products.toArray(new Product[products.size()]));
                products = appDb.getProductDao().getAllNow();

                List<ShoppingListProduct> listProducts = new ArrayList<>();
                for (int i = 0; i < 7; ++i) {
                    ShoppingListProduct listProduct = new ShoppingListProduct();
                    listProduct.setForeignShoppingListId(list.getShoppingListId());
                    listProduct.setForeignProductId(products.get(i).getProductId());
                    listProduct.setQuantity(i);
                    listProducts.add(listProduct);
                }
                appDb.getShoppingListProductDao()
                        .insert(listProducts.toArray(new ShoppingListProduct[listProducts.size()]));
                listProducts = appDb.getShoppingListProductDao().getAllNow();

                List<Store> stores = new ArrayList<>();
                for (int i = 0; i < 4; ++i) {
                    Store store = new Store();
                    store.setName("Store " + i);
                    store.setLocation("Location " + i);
                    stores.add(store);
                }
                appDb.getStoreDao()
                        .insert(stores.toArray(new Store[stores.size()]));
                stores = appDb.getStoreDao().getAllNow();


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
                        .insert(prices.toArray(new Price[prices.size()]));
                prices = appDb.getPriceDao().getAllNow();


            }
        }).start();

    }
}
