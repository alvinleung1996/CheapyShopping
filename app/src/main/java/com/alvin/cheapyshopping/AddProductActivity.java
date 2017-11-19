package com.alvin.cheapyshopping;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.alvin.cheapyshopping.fragments.AddProductFragment;

public class AddProductActivity extends AppCompatActivity {

    public static final String EXTRA_ADDED_PRODUCT_ID = "com.alvin.cheapyshopping.AddProductActivity.EXTRA_ADDED_PRODUCT_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        this.getSupportFragmentManager().registerFragmentLifecycleCallbacks(new FragmentLifecycleCallbacks(), false);

        if (savedInstanceState == null) {
            this.getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, AddProductFragment.newInstance(true))
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
            if (f instanceof AddProductFragment) {
                ((AddProductFragment) f).setInteractionListener(new FragmentInteractionListener());
            }
        }

        @Override
        public void onFragmentDetached(FragmentManager fm, Fragment f) {
            super.onFragmentDetached(fm, f);
            if (f instanceof AddProductFragment) {
                ((AddProductFragment) f).setInteractionListener(null);
            }
        }

    }

    /*
    ************************************************************************************************
    * AddProductFragment Interactions
    ************************************************************************************************
     */

    private class FragmentInteractionListener implements AddProductFragment.InteractionListener {

        @Override
        public void onDiscardOptionSelected(AddProductFragment fragment) {
            AddProductActivity.this.finishActivityWithResult(-1);
        }

        @Override
        public void onNewProductAdded(AddProductFragment fragment, long productId) {
            AddProductActivity.this.finishActivityWithResult(productId);
        }
    }


    private void finishActivityWithResult(long productId) {
        if (productId >= 0) {
            Intent data = new Intent();
            data.putExtra(EXTRA_ADDED_PRODUCT_ID, productId);
            this.setResult(RESULT_OK, data);
        } else {
            this.setResult(RESULT_CANCELED);
        }
        this.finish();
    }
}
