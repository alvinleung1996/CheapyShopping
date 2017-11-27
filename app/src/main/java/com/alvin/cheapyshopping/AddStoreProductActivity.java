package com.alvin.cheapyshopping;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.alvin.cheapyshopping.databinding.AddStoreProductActivityBinding;
import com.alvin.cheapyshopping.fragments.SelectProductFragment;

import java.util.List;

/**
 * Created by cheng on 11/28/2017.
 */

public class AddStoreProductActivity extends AppCompatActivity {

    public static final String EXTRA_STORE_ID= "com.alvin.cheapyshopping.AddStoreProductActivity.EXTRA_STORE_ID";


    private AddStoreProductActivityBinding mBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.mBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_store_product);

        Bundle args = this.getIntent().getExtras();
        String storeId = null;
        if (args != null) {
            storeId = args.getString(EXTRA_STORE_ID);
        }

        if (storeId == null) {
            throw new RuntimeException("Store Id required!");
        }

        this.getSupportFragmentManager().registerFragmentLifecycleCallbacks(new FragmentLifeCycleCallback(), false);

        if (savedInstanceState == null) {
            this.getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, SelectProductFragment.newInstance(true, null))
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (this.getSupportFragmentManager().getBackStackEntryCount() > 0) {
            this.getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }



    /*
    ************************************************************************************************
    * Fragment Interaction
    ************************************************************************************************
     */

    private class FragmentLifeCycleCallback extends FragmentManager.FragmentLifecycleCallbacks {

        @Override
        public void onFragmentAttached(FragmentManager fm, Fragment f, Context context) {
            super.onFragmentAttached(fm, f, context);
            if (f instanceof SelectProductFragment) {
                ((SelectProductFragment) f).setInteractionListener(new SelectProductFragmentInteractionListener());
            }
        }

        @Override
        public void onFragmentDetached(FragmentManager fm, Fragment f) {
            super.onFragmentDetached(fm, f);
            if (f instanceof SelectProductFragment) {
                ((SelectProductFragment) f).setInteractionListener(null);
            }
        }
    }

    /*
    ************************************************************************************************
    * SelectProductFragment Interaction
    ************************************************************************************************
     */
    private class SelectProductFragmentInteractionListener
        implements  SelectProductFragment.InteractionListener{

        @Override
        public void onAddProductOptionSelected(SelectProductFragment fragment) {

        }

        @Override
        public void onProductItemsSelected(SelectProductFragment fragment, List<String> products) {

        }
    }

}
