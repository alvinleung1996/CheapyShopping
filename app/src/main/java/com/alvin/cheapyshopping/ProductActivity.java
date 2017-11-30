package com.alvin.cheapyshopping;

import android.arch.core.util.Function;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.alvin.cheapyshopping.databinding.ProductActivityBinding;
import com.alvin.cheapyshopping.db.entities.Product;
import com.alvin.cheapyshopping.db.entities.ShoppingList;
import com.alvin.cheapyshopping.fragments.ProductInfoFragment;
import com.alvin.cheapyshopping.fragments.ProductStorePricesFragment;
import com.alvin.cheapyshopping.fragments.dialogs.ChooseShoppingListProductRelationQuantityDialog;
import com.alvin.cheapyshopping.fragments.dialogs.ChooseShoppingListsDialog;
import com.alvin.cheapyshopping.fragments.dialogs.ConfirmDialog;
import com.alvin.cheapyshopping.viewmodels.ProductActivityViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cheng on 11/13/2017.
 */

public class ProductActivity extends AppCompatActivity {

    public static final String EXTRA_PRODUCT_ID = "com.alvin.cheapyshopping.ProductActivity.ARGUMENT_PRODUCT_ID";


    private ProductActivityBinding mBinding;

    private ProductActivityViewModel mViewModel;
    private Fragment mActiveFragment;
    private FragmentPageAdapter mFragmentPageAdapter;
    private FragmentManager mFragmentManager = getSupportFragmentManager();

    private String mProductId;

    private boolean isFabOpen = false;
    private Animation FabOpen, FabClose, FabRClockwise, FabRAnticlockwise;
    private List<ShoppingList> mAvailableShoppingListsFromDB;
    private boolean mSaveProductToShoppingListButtonIsClicked = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.mBinding = DataBindingUtil.setContentView(this, R.layout.activity_product);
        this.mViewModel = ViewModelProviders.of(this).get(ProductActivityViewModel.class);

        Bundle args = getIntent().getExtras();

        String productId = null;
        if (args != null) {
            if (args.containsKey(EXTRA_PRODUCT_ID)) {
                productId = args.getString(EXTRA_PRODUCT_ID);
            }
        }

        if (productId == null) {
            throw new RuntimeException("Product Id required in args!");
        }

        this.mProductId = productId;

//        // Toolbar
//        this.setSupportActionBar(this.mBinding.toolbar);
//        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // View Pager
        mFragmentPageAdapter = new FragmentPageAdapter(this.getSupportFragmentManager());
        this.mBinding.viewPager.setAdapter(mFragmentPageAdapter);

        this.mBinding.tabsContainer.setupWithViewPager(this.mBinding.viewPager);

        // Floating Action Button
        this.mBinding.setOnFloatingActionButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProductActivity.this.onFloatingActionButtonClick((FloatingActionButton) view);
            }
        });
        setupProductInfoFragmentSubFabButtonClick();

        // Setup Floating Action Button Animations
        FabOpen = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        FabClose = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        FabRClockwise = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_clockwise);
        FabRAnticlockwise = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_anticlockwise);

        // Floating Action Button change based on page
        this.mBinding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mActiveFragment = mFragmentPageAdapter.getRegisteredFragment(mBinding.viewPager.getCurrentItem());
                if (position == 0){
                    ProductActivity.this.mBinding.fabMain.setImageDrawable(getResources().getDrawable(R.drawable.ic_keyboard_arrow_up_white_24dp));
                } else {
                    ProductActivity.this.mBinding.fabMain.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_white_24dp));
                    if (isFabOpen){
                        onProductInfoFragmentMainFabButtonClick();
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                this.finish();
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

    /*
    ************************************************************************************************
    * Fragment Page Adapter
    ************************************************************************************************
     */

    private class FragmentPageAdapter extends FragmentPagerAdapter {

        private FragmentPageAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        SparseArray<Fragment> registeredFragments = new SparseArray<>();

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return super.instantiateItem(container, position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    mActiveFragment = ProductInfoFragment.newInstance(ProductActivity.this.mProductId);
                    return mActiveFragment;
                case 1:
                    return ProductStorePricesFragment.newInstance(ProductActivity.this.mProductId);
                default:
                    return null;
            }
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Info";
                case 1:
                    return "Store Prices";
                default:
                    return null;
            }
        }
    }

    /*
    ************************************************************************************************
    * Floating Action Button
    ************************************************************************************************
     */


    private void onFloatingActionButtonClick(FloatingActionButton fab) {
        if (mActiveFragment != null){
            if(mActiveFragment instanceof ProductInfoFragment){
                onProductInfoFragmentMainFabButtonClick();
            }else if (mActiveFragment instanceof ProductStorePricesFragment){
                Intent intent = new Intent(this.getApplicationContext(), AddProductPriceActivity.class);
                intent.putExtra(AddProductPriceActivity.EXTRA_PRODUCT_ID, this.mProductId);
                this.startActivity(intent);
            }
        }
    }

    private void onProductInfoFragmentMainFabButtonClick(){
        if(isFabOpen){
            mBinding.fabMain.startAnimation(FabRAnticlockwise);

            mBinding.fabSubAddToShoppingList.startAnimation(FabClose);
            mBinding.fabSubEdit.startAnimation(FabClose);
            mBinding.textFabSubAddToShoppingList.startAnimation(FabClose);
            mBinding.textFabSubEdit.startAnimation(FabClose);

            mBinding.fabSubAddToShoppingList.setClickable(false);
            mBinding.fabSubEdit.setClickable(false);

            isFabOpen = false;
        }else{
            mBinding.fabMain.startAnimation(FabRClockwise);

            mBinding.fabSubAddToShoppingList.startAnimation(FabOpen);
            mBinding.fabSubEdit.startAnimation(FabOpen);
            mBinding.textFabSubAddToShoppingList.startAnimation(FabOpen);
            mBinding.textFabSubEdit.startAnimation(FabOpen);

            mBinding.fabSubAddToShoppingList.setClickable(true);
            mBinding.fabSubEdit.setClickable(true);

            isFabOpen = true;
        }
    }

    private void setupProductInfoFragmentSubFabButtonClick(){
        mBinding.fabSubEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        mBinding.fabSubAddToShoppingList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSaveProductToShoppingListButtonIsClicked = true;
                saveProductToShoppingLists();
            }
        });
    }


    /*
    ************************************************************************************************
    * Dialogs for adding product to shopping List
    ************************************************************************************************
     */

    public void saveProductToShoppingLists() {
        // Only add to shopping list(s) without the product
        mViewModel.findShoppingListsNotContainProduct(mProductId).observe(this, new Observer<List<ShoppingList>>() {
            @Override
            public void onChanged(@Nullable List<ShoppingList> shoppingLists) {
                if (mSaveProductToShoppingListButtonIsClicked) {
                    boolean canProceed = false;
                    mAvailableShoppingListsFromDB = shoppingLists;
                    if (mAvailableShoppingListsFromDB != null) {
                        if (mAvailableShoppingListsFromDB.size() > 0) {
                            canProceed = true;
                        }
                    }

                    if (canProceed) {
                        ChooseShoppingListsDialog dialog = ChooseShoppingListsDialog.newInstance(mAvailableShoppingListsFromDB);
                        dialog.setInteractionListener(new ChooseShoppingListsDialog.InteractionListener() {
                            @Override
                            public void onSelectShoppingListsConfirmed(List<ShoppingList> shoppingLists) {
                                // Get ShoppingLists' ID
                                List<String> shoppingListIds = new ArrayList<>(shoppingLists.size());
                                for (ShoppingList shoppingList : shoppingLists) {
                                    shoppingListIds.add(shoppingList.getShoppingListId());
                                }
                                ProductActivity.this.saveProductToSelectedShoppingLists(shoppingListIds);
                            }
                        });
                        dialog.show(mFragmentManager, null);
                    } else {
                        ConfirmDialog dialog = ConfirmDialog.newInstance("No available shopping list.", false);
                        dialog.show(mFragmentManager, null);
                    }

                    mSaveProductToShoppingListButtonIsClicked = false;
                }
            }
        });

    }

    private void saveProductToSelectedShoppingLists(final List<String> shoppingListIds){
        // Choose the quantity to be added
        ChooseShoppingListProductRelationQuantityDialog dialog = ChooseShoppingListProductRelationQuantityDialog.newInstance();
        dialog.setInteractionListener(new ChooseShoppingListProductRelationQuantityDialog.InteractionListener() {
            @Override
            public void onQuantityChosen(int quantity) {
                mViewModel.addProductToShoppingLists(mProductId, shoppingListIds, quantity);

                Toast.makeText(ProductActivity.this ,
                        "Added to  " + shoppingListIds.size() + " shopping list(s)",
                        Toast.LENGTH_LONG).show();
            }
        });
        dialog.show(mFragmentManager, null);
    }


}
