package com.alvin.cheapyshopping;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import com.alvin.cheapyshopping.db.DatabaseHelper;
import com.alvin.cheapyshopping.db.models.ProductModel;
import com.alvin.cheapyshopping.db.models.ShoppingListModel;
import com.alvin.cheapyshopping.db.models.StoreModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    Toolbar mToolbar;
    FloatingActionButton fab_plus;

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




        //Tool Bar
        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        mToolbar.setTitle("Home");
        mToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(mToolbar);

        //Navigation Drawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout,R.string.drawer_open,R.string.drawer_close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Floating action buttons
        fab_plus = (FloatingActionButton)findViewById(R.id.fab_plus);
        fab_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), AddPriceActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (this.getSupportFragmentManager().getBackStackEntryCount() > 0) {
            this.getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.toolbar_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int res_id = item.getItemId();
        if(res_id == R.id.toolbar_refresh)
        {
            Toast.makeText(getApplicationContext(), "Page will be refreshed", Toast.LENGTH_LONG).show();
        }

        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }
        return true;
    }
}
