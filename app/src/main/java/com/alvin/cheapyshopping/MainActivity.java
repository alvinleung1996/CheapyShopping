package com.alvin.cheapyshopping;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.alvin.cheapyshopping.db.DatabaseHelper;
import com.alvin.cheapyshopping.db.models.ProductModel;
import com.alvin.cheapyshopping.db.models.ShoppingListModel;
import com.alvin.cheapyshopping.db.models.StoreModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!DatabaseHelper.getInstance(this).deleteDatabase()) {
            Log.e("DB", "Cannot delete database");
        }

        ShoppingListModel shoppingList = new ShoppingListModel(this);
        shoppingList.saveOrThrow();

        List<ProductModel> products = new ArrayList<>();
        for (long i = 0; i < 10; ++i) {
            ProductModel product = new ProductModel(this);
            product.name = "Product " + i;
            product.saveOrThrow();
            products.add(product);
        }

        shoppingList.addProduct(products.get(0));


        StoreModel store0 = new StoreModel(this);
        store0.location = "HKU";
        store0.name = "COOP";
        store0.saveOrThrow();

        StoreModel store1 = new StoreModel(this);
        store1.location = "UST";
        store1.name = "Hall 1";
        store1.saveOrThrow();

        if (savedInstanceState == null) {
            this.getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, ShoppingListFragment.newInstance())
                    .commit();
        }

        Button newPriceButton = this.findViewById(R.id.button_new_price);
        newPriceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), AddPriceActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });


        //Navigation Drawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout,R.string.drawer_open,R.string.drawer_close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();


    }

    @Override
    public void onBackPressed() {
        if (this.getSupportFragmentManager().getBackStackEntryCount() > 0) {
            this.getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}
