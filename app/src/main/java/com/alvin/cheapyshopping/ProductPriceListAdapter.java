package com.alvin.cheapyshopping;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alvin.cheapyshopping.db.DatabaseHelper;
import com.alvin.cheapyshopping.db.models.PriceModel;
import com.alvin.cheapyshopping.db.models.ProductModel;
import com.alvin.cheapyshopping.db.models.StoreModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cheng on 11/18/2017.
 */
public class ProductPriceListAdapter extends RecyclerView.Adapter<ProductPriceListAdapter.ProductPriceListItemViewHolder> {


    private class ProductPriceItem {

        private StoreModel store;
        private PriceModel price;

        private ProductPriceItem(PriceModel price, StoreModel store) {
            this.store = store;
            this.price = price;
        }
    }

    private List<ProductPriceItem> mPriceList = new ArrayList<>();

    public ProductPriceListAdapter(Context context,ProductModel mProduct) {
        getPriceList(context, mProduct);
    }

    public class ProductPriceListItemViewHolder extends RecyclerView.ViewHolder {
        private TextView mStoreName;
        private TextView mPrice;
        private TextView mPriceID;
        private TextView mStoreLocation;
        private ProductPriceListItemViewHolder(View v) {
            super(v);
            View view = this.itemView;
            mStoreName = view.findViewById(R.id.text_store_name);
            mPrice = view.findViewById(R.id.text_store_price);
            mPriceID = view.findViewById(R.id.text_price_id);
            mStoreLocation = view.findViewById(R.id.text_store_location);
        }
    }


    @Override
    public ProductPriceListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_product_price,parent,false);
        ProductPriceListItemViewHolder viewHolder = new ProductPriceListItemViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ProductPriceListItemViewHolder holder, int position) {
        holder.mStoreName.setText(mPriceList.get(position).store.name);
        holder.mStoreLocation.setText(mPriceList.get(position).store.location);
        holder.mPrice.setText(Double.toString(mPriceList.get(position).price.price));
        holder.mPriceID.setText("Price id: " + mPriceList.get(position).price.priceId);
    }

    public int getItemCount() {
        return mPriceList.size();
    }

    private void getPriceList (Context context,ProductModel mProduct){
        PriceModel mPrice = new PriceModel(context);

        String tablename = PriceModel.manager.getTableName();
        String selection = PriceModel.COLUMN_FOREIGN_PRODUCT_ID + " = ?";
        String[] selectionArgs = { String.valueOf(mProduct.productId) };
        SQLiteDatabase db = DatabaseHelper.getInstance(context).getDatabase();
        Cursor cursor = db.query(
                tablename,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            do{
                mPrice = PriceModel.manager.get(context,cursor);

                // Get corresponding StoreModel
                StoreModel mStore = StoreModel.manager.get(context, mPrice.foreignStoreId);
                this.mPriceList.add(new ProductPriceItem(mPrice, mStore));
            }while(cursor.moveToNext());
        }
        cursor.close();
    }

}