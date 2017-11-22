package com.alvin.cheapyshopping;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.alvin.cheapyshopping.fragments.AddStoreFragment;

public class AddStoreActivity extends AppCompatActivity {

    public static final String EXTRA_ADDED_STORE_ID = "com.alvin.cheapyshopping.AddStoreActivity.EXTRA_ADDED_STORE_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_store);

        this.getSupportFragmentManager().registerFragmentLifecycleCallbacks(new FragmentLifecycleCallbacks(), false);

        if (savedInstanceState == null) {
            this.getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, AddStoreFragment.newInstance(true))
                    .commit();
        }
    }

    /*
    ************************************************************************************************
    * Fragment Interactions
    ************************************************************************************************
     */

    private class FragmentLifecycleCallbacks extends FragmentManager.FragmentLifecycleCallbacks {

        @Override
        public void onFragmentAttached(FragmentManager fm, Fragment f, Context context) {
            super.onFragmentAttached(fm, f, context);
            if (f instanceof AddStoreFragment) {
                ((AddStoreFragment) f).setInteractionListener(new FragmentInteractionListener());
            }
        }

        @Override
        public void onFragmentDetached(FragmentManager fm, Fragment f) {
            super.onFragmentDetached(fm, f);
            if (f instanceof AddStoreFragment) {
                ((AddStoreFragment) f).setInteractionListener(null);
            }
        }

    }

    /*
    ************************************************************************************************
    * AddStoreFragment Interactions
    ************************************************************************************************
     */

    private class FragmentInteractionListener implements AddStoreFragment.InteractionListener {

        @Override
        public void onDiscardOptionSelected(AddStoreFragment fragment) {
            AddStoreActivity.this.finishActivityWithResult(-1);
        }

        @Override
        public void onNewStoreAdded(AddStoreFragment fragment, long storeId) {
            AddStoreActivity.this.finishActivityWithResult(storeId);
        }
    }


    private void finishActivityWithResult(long storeId) {
        if (storeId >= 0) {
            Intent data = new Intent();
            data.putExtra(EXTRA_ADDED_STORE_ID, storeId);
            this.setResult(RESULT_OK, data);
        } else {
            this.setResult(RESULT_CANCELED);
        }
        this.finish();
    }
}
