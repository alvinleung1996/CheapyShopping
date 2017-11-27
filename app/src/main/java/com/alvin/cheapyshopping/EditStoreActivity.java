package com.alvin.cheapyshopping;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.alvin.cheapyshopping.databinding.EditStoreActivityBinding;
import com.alvin.cheapyshopping.db.entities.Store;
import com.alvin.cheapyshopping.viewmodels.EditStoreActivityViewModel;

/**
 * Created by cheng on 11/27/2017.
 */

public class EditStoreActivity extends AppCompatActivity {

    public static final String EXTRA_STORE_ID = "com.alvin.cheapyshopping.EditStoreActivity.EXTRA_STORE_ID";

    private EditStoreActivityBinding mBinding;
    private Store mStore;

    private EditStoreActivityViewModel mViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        this.mBinding = EditStoreActivityBinding.inflate(getLayoutInflater());
        this.mViewModel = ViewModelProviders.of(this).get(EditStoreActivityViewModel.class);


        // Toolbar
        this.setSupportActionBar((Toolbar) this.mBinding.toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(false);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.add_store_fragment_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
