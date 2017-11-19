package com.alvin.cheapyshopping;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.alvin.cheapyshopping.olddb.models.ProductModel;
import com.alvin.cheapyshopping.olddb.models.ShoppingListModel;
import com.alvin.cheapyshopping.fragments.SelectProductFragment;

import java.util.List;

public class AddShoppingListProductActivity extends AppCompatActivity
        implements SelectProductFragment.SelectProductFragmentListener {

    public static final String EXTRA_SELECTED_PRODUCT_IDS = "com.alvin.cheapyshopping.AddShoppingListProductActivity.EXTRA_SELECTED_PRODUCT_IDS";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shopping_list_item);

        if (savedInstanceState == null) {
            long[] shoppingListProductIds = this.getShoppingListProductIds();
            this.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, SelectProductFragment.newInstance(false, shoppingListProductIds))
                    .commit();
        }
    }

    private long[] getShoppingListProductIds() {
        ShoppingListModel shoppingList = ShoppingListModel.manager.getLatestShoppingList(this);
        if (shoppingList == null) {
            return new long[0];
        }

        List<ProductModel> products = shoppingList.getProducts();
        long[] productIds = new long[products.size()];
        for (int i = 0; i < products.size(); ++i) {
            productIds[i] = products.get(i).productId;
        }
        return productIds;
    }

    private void finishActivity(List<ProductModel> models) {
        Intent intent = new Intent();
        long[] productIds = new long[models.size()];
        for (int i = 0; i < models.size(); ++i) {
            productIds[i] = models.get(i).productId;
        }
        intent.putExtra(EXTRA_SELECTED_PRODUCT_IDS, productIds);
        this.setResult(RESULT_OK, intent);
        this.finish();
    }

    /*
    ************************************************************************************************
    * SelectStoreFragment Interactions
    ************************************************************************************************
     */

    @Override
    public void onAddProductOptionSelected(SelectProductFragment fragment) {
        // Do nothing
        // User are not allowed to add product at this stage,
        // they have to go to other page to add product.
        // Also the add product button has been disabled/hidden (newInstance(false))
    }

    @Override
    public void onProductItemsSelected(SelectProductFragment fragment, List<ProductModel> models) {
        ShoppingListModel shoppingList = ShoppingListModel.manager.getLatestShoppingList(this);
        for (ProductModel product : models) {
            if (!shoppingList.addProduct(product)) {
                Log.e("AddProduct failed", "onProductItemSelected: Cannot add product " + product.productId + " to shopping list " + shoppingList.shoppingListId);
            }
        }
        this.finishActivity(models);
    }



}
