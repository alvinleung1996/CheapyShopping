package com.alvin.cheapyshopping;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.alvin.cheapyshopping.db.DatabaseHelper;
import com.alvin.cheapyshopping.db.models.ProductModel;

/**
 * Created by cheng on 11/13/2017.
 */

public class ProductActivity extends AppCompatActivity {


    Toolbar mToolbar;
    TextView mProductName;
    TextView mProductDescription;
    ProductModel mProduct;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        Bundle extras = getIntent().getExtras();

        mProduct = getProductByID(extras.getLong("mProductID"));

        // Tool Bar
        mToolbar = findViewById(R.id.product_toolBar);
        mToolbar.setTitle("");
        mToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProductName = findViewById(R.id.product_name);
        mProductName.setText(mProduct.name);
        mProductDescription = findViewById(R.id.product_description);
        mProductDescription.setText(mProduct.description);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.toolbar_menu_product, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int res_id = item.getItemId();
        if(res_id == R.id.item_refresh)
        {
            Toast.makeText(getApplicationContext(), "Page will be refreshed", Toast.LENGTH_LONG).show();
        }

        return true;
    }

    public ProductModel getProductByID(long productID){
        ProductModel mProduct = new ProductModel(this);

        String tablename = ProductModel.manager.getTableName();
        String selection = ProductModel.COLUMN_PRODUCT_ID + " = ?";
        String[] selectionArgs = { String.valueOf(productID) };
        String limit = "1";
        SQLiteDatabase db = DatabaseHelper.getInstance(this).getDatabase();
        Cursor cursor = db.query(
                tablename,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null,
                limit
        );

        if (cursor.moveToFirst()) {
            mProduct = ProductModel.manager.get(this,cursor);
        }
        cursor.close();
        return mProduct;
    }


}
