package com.alvin.cheapyshopping.db.entities.pseudo;

/**
 * Created by Alvin on 22/11/2017.
 */

import android.arch.persistence.room.Embedded;

import com.alvin.cheapyshopping.db.entities.Price;
import com.alvin.cheapyshopping.db.entities.Store;

/**
 * This entity store the price together with the store data
 */
public class StorePrice extends Price {

    @Embedded
    private Store mStore;

    public Store getStore() {
        return this.mStore;
    }

    public void setStore(Store store) {
        this.mStore = store;
    }
}
