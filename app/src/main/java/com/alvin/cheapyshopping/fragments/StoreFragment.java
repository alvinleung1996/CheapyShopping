package com.alvin.cheapyshopping.fragments;


import android.support.v4.app.Fragment;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.alvin.cheapyshopping.R;
import com.alvin.cheapyshopping.StoreActivity;
import com.alvin.cheapyshopping.databinding.ProductFragmentBinding;
import com.alvin.cheapyshopping.databinding.StoreFragmentBinding;
import com.alvin.cheapyshopping.db.entities.Product;
import com.alvin.cheapyshopping.db.entities.ShoppingList;
import com.alvin.cheapyshopping.db.entities.pseudo.StorePrice;
import com.alvin.cheapyshopping.viewmodels.ProductFragmentViewModel;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by cheng on 11/26/2017.
 */

public class StoreFragment extends Fragment {

    public static StoreFragment newInstance(long productID) {
        StoreFragment fragment = new StoreFragment();

        Bundle args = new Bundle();
        args.putLong(StoreActivity.EXTRA_STORE_ID, productID);
        fragment.setArguments(args);
        return fragment;
    }


    private StoreFragmentBinding mBinding;
    private long mCurrentStoreId;
    private Product mCurrentStore;


    public StoreFragment(){
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);

        // Get Product ID
        Bundle args = getArguments();
        mCurrentStoreId= args.getLong(StoreActivity.EXTRA_STORE_ID, 0);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.mBinding = StoreFragmentBinding.inflate(inflater, container, false);

        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }


    /*
    ************************************************************************************************
    * Menu
    ************************************************************************************************
     */

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.toolbar_menu_product, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_save:

                return true;
            case R.id.item_edit:
                return true;
            case R.id.item_add_price:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
