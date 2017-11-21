package com.alvin.cheapyshopping.fragments;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.alvin.cheapyshopping.R;
import com.alvin.cheapyshopping.olddb.DatabaseHelper;
import com.alvin.cheapyshopping.olddb.models.PriceModel;
import com.alvin.cheapyshopping.olddb.models.ProductModel;
import com.alvin.cheapyshopping.olddb.models.StoreModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cheng on 11/21/2017.
 */

public class ProductFragment extends Fragment {

    private class ProductPriceItem {

        private StoreModel store;
        private PriceModel price;

        private ProductPriceItem(PriceModel price, StoreModel store) {
            this.store = store;
            this.price = price;
        }
    }

    private class ProductPriceListAdapter extends RecyclerView.Adapter<ProductPriceListAdapter.ProductPriceListItemViewHolder> {


        private List<ProductPriceItem> mPriceList = new ArrayList<>();

        public ProductPriceListAdapter(Context context, ProductModel mProduct) {
            getPriceList(context, mProduct);
        }

        public class ProductPriceListItemViewHolder extends RecyclerView.ViewHolder {
            private TextView mStoreName;
            private TextView mPrice;
            private TextView mPriceID;
            private TextView mStoreLocation;
            private TextView mPriceUpdateDate;

            private ProductPriceListItemViewHolder(View v) {
                super(v);
                View view = this.itemView;
                mStoreName = view.findViewById(R.id.text_store_name);
                mPrice = view.findViewById(R.id.text_store_price);
                mPriceID = view.findViewById(R.id.text_price_id);
                mStoreLocation = view.findViewById(R.id.text_store_location);
                mPriceUpdateDate = view.findViewById(R.id.text_price_date);
            }
        }


        @Override
        public ProductPriceListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.item_product_price, parent, false);
            ProductPriceListItemViewHolder viewHolder = new ProductPriceListItemViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ProductPriceListItemViewHolder holder, int position) {
            holder.mStoreName.setText(mPriceList.get(position).store.name);
            holder.mStoreLocation.setText(mPriceList.get(position).store.location);
            holder.mPrice.setText(Double.toString(mPriceList.get(position).price.price));
            holder.mPriceID.setText("Price id: " + mPriceList.get(position).price.priceId);

            // Set price update date & time
            SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy     HH:mm:ss");
            String updateDate = formatter.format(mPriceList.get(position).price.time);
            holder.mPriceUpdateDate.setText(updateDate);
        }

        public int getItemCount() {
            return mPriceList.size();
        }

        private void getPriceList(Context context, ProductModel mProduct) {
            PriceModel mPrice = new PriceModel(context);

            String tablename = PriceModel.manager.getTableName();
            String selection = PriceModel.COLUMN_FOREIGN_PRODUCT_ID + " = ?";
            String[] selectionArgs = {String.valueOf(mProduct.productId)};
            SQLiteDatabase db = DatabaseHelper.getInstance(context).getDatabase();
            Cursor cursor = db.query(
                    tablename,
                    null,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    "price ASC"
            );

            if (cursor.moveToFirst()) {
                do {
                    mPrice = PriceModel.manager.get(context, cursor);

                    // Get corresponding StoreModel
                    StoreModel mStore = StoreModel.manager.get(context, mPrice.foreignStoreId);
                    this.mPriceList.add(new ProductPriceItem(mPrice, mStore));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

    }



    public static ProductFragment newInstance(long productID) {
        ProductFragment fragment = new ProductFragment();

        Bundle args = new Bundle();
        args.putLong("productID", productID);
        fragment.setArguments(args);
        return fragment;
    }


    private RecyclerView mProductPriceItemList;
    ProductPriceListAdapter mAdapter;
    TextView mProductName;
    TextView mProductDescription;
    TextView mProductBestPrice;
    TextView mProductBestPriceDate;

    private ProductPriceItem mProductPriceList;
    private ProductModel mProduct;

    public ProductFragment(){
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_price, container, false);

        // Recycler View
        mProductPriceItemList = view.findViewById(R.id.list_product_price_items);
        mProductName = view.findViewById(R.id.text_product_name);
        mProductDescription = view.findViewById(R.id.text_product_description);
        mProductBestPrice = view.findViewById(R.id.text_best_price);
        mProductBestPriceDate = view.findViewById(R.id.text_best_price_date);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Setup Product Model
        Bundle args = getArguments();
        long productID = args.getLong("productID", 0);
        mProduct = ProductModel.manager.get(this.getContext(), productID);
        setupProductBasicInfo(mProduct);

        mProductPriceItemList.setLayoutManager(new LinearLayoutManager(this.getContext()));
        mProductPriceItemList.setNestedScrollingEnabled(false);  // For smoother scrolling
        mAdapter = new ProductPriceListAdapter(this.getContext(),mProduct);
        mProductPriceItemList.setAdapter(mAdapter);
    }

    public void setupProductBasicInfo(ProductModel mProduct){
        mProductName.setText(mProduct.name);
        mProductDescription.setText(mProduct.description);
        setProductBestPrice(this.getContext(), mProduct);
    }

    public void setProductBestPrice(Context context, ProductModel mProduct){
        PriceModel Price = new PriceModel(context);

        // Get best price from database
        String tablename = PriceModel.manager.getTableName();
        String selection = PriceModel.COLUMN_FOREIGN_PRODUCT_ID + " = ?";
        String[] selectionArgs = { String.valueOf(mProduct.productId) };
        String limit = "1";
        SQLiteDatabase db = DatabaseHelper.getInstance(context).getDatabase();
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
            Price = PriceModel.manager.get(context,cursor);
        }
        cursor.close();

        // Setup Textview for best price information
        mProductBestPrice.setText("$" + Double.toString(Price.price));
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        String updateDate = formatter.format(Price.time);
        mProductBestPriceDate.setText("("+ updateDate + ")");

    }
}
