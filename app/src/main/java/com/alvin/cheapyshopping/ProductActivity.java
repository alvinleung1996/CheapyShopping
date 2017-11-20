package com.alvin.cheapyshopping;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.alvin.cheapyshopping.olddb.DatabaseHelper;
import com.alvin.cheapyshopping.olddb.models.PriceModel;
import com.alvin.cheapyshopping.olddb.models.ProductModel;

import java.text.SimpleDateFormat;

/**
 * Created by cheng on 11/13/2017.
 */

public class ProductActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    ProductPriceListAdapter mAdapter;
    TextView mProductName;
    TextView mProductDescription;
    TextView mProductBestPrice;
    TextView mProductBestPriceDate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        Bundle extras = getIntent().getExtras();

        // Toolbar
        this.mToolbar = findViewById(R.id.toolbar);
        this.setSupportActionBar(this.mToolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        ProductModel mProduct = ProductModel.manager.get(this, extras.getLong("mProductID"));
        setupProductBasicInfo(mProduct);

        RecyclerView mRecyclerView = (RecyclerView)findViewById(R.id.myrecyclerview_product_price);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setNestedScrollingEnabled(false);  // For smoother scrolling
        mAdapter = new ProductPriceListAdapter(this,mProduct);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void setupProductBasicInfo(ProductModel mProduct){
        mProductName = findViewById(R.id.text_product_name);
        mProductDescription = findViewById(R.id.text_product_description);
        mProductBestPrice = findViewById(R.id.text_best_price);
        mProductBestPriceDate = findViewById(R.id.text_best_price_date);

        mProductName.setText(mProduct.name);
        mProductDescription.setText(mProduct.description);
        setProductBestPrice(mProduct);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.toolbar_menu_product, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void setProductBestPrice(ProductModel mProduct){
        PriceModel Price = new PriceModel(this);

        // Get best price from database
        String tablename = PriceModel.manager.getTableName();
        String selection = PriceModel.COLUMN_FOREIGN_PRODUCT_ID + " = ?";
        String[] selectionArgs = { String.valueOf(mProduct.productId) };
        String limit = "1";
        SQLiteDatabase db = DatabaseHelper.getInstance(this).getDatabase();
        Cursor cursor = db.query(
                tablename,
                null,
                selection,
                selectionArgs,
                null,
                null,
                "price ASC",
                limit
        );
        if (cursor.moveToFirst()) {
            Price = PriceModel.manager.get(this,cursor);
        }
        cursor.close();

        // Setup Textview for best price information
        mProductBestPrice.setText("$" + Double.toString(Price.price));
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        String updateDate = formatter.format(Price.time);
        mProductBestPriceDate.setText("("+ updateDate + ")");

    }

}
