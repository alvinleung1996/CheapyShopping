package com.alvin.cheapyshopping.db.entities.pseudo;

/**
 * Created by cheng on 11/26/2017.
 */

import android.arch.persistence.room.Embedded;

import com.alvin.cheapyshopping.db.entities.Price;
import com.alvin.cheapyshopping.db.entities.Product;

/**
 * This entity store the price together with the product data
 */
public class ProductPrice extends Price {

    @Embedded
    private Product mProduct;

    public Product getProduct() {
        return this.mProduct;
    }

    public void setProduct(Product product) {
        this.mProduct = product;
    }
}