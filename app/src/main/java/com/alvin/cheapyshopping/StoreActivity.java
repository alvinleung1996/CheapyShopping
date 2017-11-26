package com.alvin.cheapyshopping;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.alvin.cheapyshopping.fragments.StoreFragment;

/**
 * Created by cheng on 11/26/2017.
 */

public class StoreActivity extends AppCompatActivity {

    public static final String EXTRA_STORE_ID = "com.alvin.cheapyshopping.StoreActivity.EXTRA_STORE_ID";

    private static final String FRAGMENT_STORE = "com.alvin.cheapyshopping.StoreActivity.FRAGMENT_PRODUCT";

    private Toolbar mToolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        Bundle extras = getIntent().getExtras();

        Log.d("Debug", "Created Store Activity");

        // Toolbar
        this.mToolbar = findViewById(R.id.toolbar);
        this.setSupportActionBar(this.mToolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(false);


        this.getSupportFragmentManager().registerFragmentLifecycleCallbacks(new FragmentLifecycleCallbacks(), false);
        // Start ProductFragment
        if (savedInstanceState == null) { //Prevent adding fragment twice
            this.getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, StoreFragment.newInstance(extras.getLong(EXTRA_STORE_ID)), FRAGMENT_STORE)
                    .commit();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*
    ************************************************************************************************
    * Fragment Interactions
    ************************************************************************************************
     */

    private Fragment mActiveFragment;

    private class FragmentLifecycleCallbacks extends FragmentManager.FragmentLifecycleCallbacks {

        @Override
        public void onFragmentAttached(FragmentManager fm, Fragment f, Context context) {
            super.onFragmentAttached(fm, f, context);
            if (f instanceof StoreFragment) {
//                ((ProductFragment) f).setInteractionListener(new ProductFragmentInteractionListener());
            }
        }

    }
}
