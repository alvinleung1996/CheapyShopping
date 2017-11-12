package com.alvin.cheapyshopping;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alvin.cheapyshopping.db.DatabaseHelper;
import com.alvin.cheapyshopping.db.models.ProductModel;
import com.alvin.cheapyshopping.db.models.ShoppingListModel;
import com.alvin.cheapyshopping.db.models.StoreModel;

import java.util.ArrayList;
import java.util.List;


public class PopularProductsFragment extends Fragment {


    public static class PopularProductAdapter extends RecyclerView.Adapter<PopularProductAdapter.AbstractViewHolder> {


        public static abstract class AbstractViewHolder extends RecyclerView.ViewHolder{
            private AbstractViewHolder(View v) {super(v);}
        }

        public static class PopularProductViewHolder extends AbstractViewHolder{
            private TextView mPopularProductPrice;
            private PopularProductViewHolder (View v){
                super(v);
                mPopularProductPrice = v.findViewById(R.id.popular_products_price);
            }
        }

        private PopularProductAdapter(List<ProductModel> listItems) {
            this.listItems = listItems;
        }

        private List<ProductModel> listItems;

        @Override
        public int getItemCount() {
            return listItems.size();
        }

        @Override
        public AbstractViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v;
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_popular_product, parent, false);
            return new PopularProductViewHolder(v);
        }

        @Override
        public void onBindViewHolder(AbstractViewHolder holder, int position) {
            PopularProductViewHolder h = (PopularProductViewHolder) holder;
            //h.mPopularProductPrice.setText(this.listItems.get(position));
        }
    }// End of recycler view

    public static PopularProductsFragment newInstance() {
        PopularProductsFragment fragment = new PopularProductsFragment();
        return fragment;
    }


    public PopularProductsFragment() {

    }


    private RecyclerView mProductList;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_popular_products, container, false);
        this.mProductList = view.findViewById(R.id.popular_products);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Config LinearLayoutManager for horizontal scroll
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false);
        this.mProductList.setLayoutManager(layoutManager);

        // Search for products with 'popular' == 1
        List<ProductModel> items = new ArrayList<>();
        String tablename = ProductModel.manager.getTableName();
        String selection = ProductModel.COLUMN_POPULAR + " = ?";
        String[] selectionArgs = { "1" };
        SQLiteDatabase db = DatabaseHelper.getInstance(this.getContext()).getDatabase();
        Cursor cursor = db.query(
                tablename,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        if (cursor.moveToFirst()){
            do{
                items.add(new ProductModel(this.getContext()).manager.getByCursor(this.getContext(),cursor));
            }while(cursor.moveToNext());
        }
        cursor.close();

        mProductList.setAdapter(new PopularProductAdapter(items));

    }
}
