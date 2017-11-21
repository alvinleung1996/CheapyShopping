package com.alvin.cheapyshopping;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.alvin.cheapyshopping.fragments.ProductFragment;
/**
 * Created by cheng on 11/13/2017.
 */

public class ProductActivity extends AppCompatActivity {


    private static final String FRAGMENT_PRODUCT = "com.alvin.cheapyshopping.ProductActivity.FRAGMENT_PRODUCT";

    private Toolbar mToolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        Bundle extras = getIntent().getExtras();

        // Toolbar
        this.mToolbar = findViewById(R.id.toolbar);
        this.setSupportActionBar(this.mToolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(false);


        if (savedInstanceState == null) { //Prevent adding fragment twice
            this.getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, ProductFragment.newInstance(extras.getLong("mProductID")), FRAGMENT_PRODUCT)
                    .commit();
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.toolbar_menu_product, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                return true;
            case R.id.action_edit:
                return true;
            case R.id.action_add_price:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
