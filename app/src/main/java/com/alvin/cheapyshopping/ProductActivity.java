package com.alvin.cheapyshopping;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.alvin.cheapyshopping.db.DatabaseHelper;
import com.alvin.cheapyshopping.db.models.ProductModel;

/**
 * Created by cheng on 11/13/2017.
 */

public class ProductActivity extends AppCompatActivity {


    ProductPriceListAdapter mAdapter;

    TextView mProductName;
    TextView mProductDescription;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        Bundle extras = getIntent().getExtras();

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
        mProductName.setText(mProduct.name);
        mProductDescription = findViewById(R.id.text_product_description);
        mProductDescription.setText(mProduct.description);
    }


}
