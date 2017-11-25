package com.alvin.cheapyshopping;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.alvin.cheapyshopping.databinding.AddShoppingListProductRelationActivityBinding;
import com.alvin.cheapyshopping.fragments.SelectProductFragment;
import com.alvin.cheapyshopping.viewmodels.AddShoppingListProductRelationActivityViewModel;

import java.util.List;

public class AddShoppingListProductRelationActivity extends AppCompatActivity {

    public static final String EXTRA_SHOPPING_LIST_ID = "com.alvin.cheapyshopping.AddShoppingListProductRelationActivity.EXTRA_SHOPPING_LIST_ID";

    public static final String EXTRA_SELECTED_PRODUCT_IDS = "com.alvin.cheapyshopping.AddShoppingListProductRelationActivity.EXTRA_SELECTED_PRODUCT_IDS";


    private AddShoppingListProductRelationActivityViewModel mViewModel;

    private AddShoppingListProductRelationActivityBinding mBinding;

    private long mShoppingListId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.mBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_shopping_product_relation);

        this.mViewModel = ViewModelProviders.of(this).get(AddShoppingListProductRelationActivityViewModel.class);

        this.getSupportFragmentManager().registerFragmentLifecycleCallbacks(new FragmentLifecycleCallbacks(), false);

        if (savedInstanceState == null) {
            Bundle args = this.getIntent().getExtras();

            Long shoppingListId = null;
            if (args != null) {
                shoppingListId = this.mShoppingListId = args.getLong(EXTRA_SHOPPING_LIST_ID);
            }

            if (shoppingListId == null) {
                throw new RuntimeException("EXTRA_SHOPPING_LIST_ID is missing");
            }

            this.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, SelectProductFragment.newInstance(false, this.mShoppingListId))
                    .commit();
        }
    }

    private void finishActivity(List<Long> productIds) {
        Intent intent = new Intent();
        long[] data = new long[productIds.size()];
        for (int i = 0; i < productIds.size(); ++i) {
            data[i] = productIds.get(i);
        }
        intent.putExtra(EXTRA_SELECTED_PRODUCT_IDS, data);
        this.setResult(RESULT_OK, intent);
        this.finish();
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
    * SelectStoreFragment Interactions
    ************************************************************************************************
     */

    private class SelectProductFragmentInteractionListener implements SelectProductFragment.InteractionListener {

        @Override
        public void onAddProductOptionSelected(SelectProductFragment fragment) {
            // Do nothing
            // User are not allowed to add product at this stage,
            // they have to go to other page to add product.
            // Also the add product button has been disabled/hidden (newInstance(false))
        }

        @Override
        public void onProductItemsSelected(SelectProductFragment fragment, List<Long> productIds) {
            AddShoppingListProductRelationActivity.this.mViewModel
                    .addShoppingListProduct(AddShoppingListProductRelationActivity.this.mShoppingListId, productIds, 1);
            AddShoppingListProductRelationActivity.this.finishActivity(productIds);
        }
    }

}
