package com.alvin.cheapyshopping;

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
import com.alvin.cheapyshopping.fragments.dialogs.EditProductInfoDialog;
import com.alvin.cheapyshopping.utils.CurrentAccountScoreAdder;
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
    private Product mEditedProduct;

    // Fab variables
    private boolean isFabOpen = false;
    private Animation FabOpen, FabClose, FabRClockwise, FabRAnticlockwise;
    // Variables for adding product to shopping lists
    private List<ShoppingList> mAvailableShoppingListsFromDB;
    private boolean mSaveProductToShoppingListButtonIsClicked = false;
    private boolean mEditProductButtonIsClicked = false;


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

        // Toolbar
        this.setSupportActionBar(this.mBinding.toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mViewModel.getProduct(productId).observe(this, p -> setTitle(p == null ? "Product" : p.getName()));

        // View Pager
        mFragmentPageAdapter = new FragmentPageAdapter(this.getSupportFragmentManager());
        this.mBinding.viewPager.setAdapter(mFragmentPageAdapter);

        this.mBinding.tabsContainer.setupWithViewPager(this.mBinding.viewPager);

        // Floating Action Button
        this.mBinding.setOnFloatingActionButtonClickListener(view -> onFloatingActionButtonClick((FloatingActionButton) view));
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
                    // Product Info Fragment
                    ProductActivity.this.mBinding.fabMain.setImageDrawable(getResources().getDrawable(R.drawable.ic_keyboard_arrow_up_white_24dp));
                } else {
                    // Product Store Price Fragment
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

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
                    Fragment fragment = ProductStorePricesFragment.newInstance(ProductActivity.this.mProductId);
                    ((ProductStorePricesFragment) fragment).setInteractionListener(new AddProductStorePricesFragmentInteractionListener());
                    return fragment;
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
    * Fragment Interaction
    ************************************************************************************************
     */

    private class AddProductStorePricesFragmentInteractionListener implements
            ProductStorePricesFragment.InteractionListener{
        @Override
        public void onGoToStoreClicked(String storeId) {
            Intent intent = new Intent(ProductActivity.this, StoreActivity.class);
            intent.putExtra(StoreActivity.EXTRA_STORE_ID, storeId);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
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
                mEditProductButtonIsClicked = true;
                editProduct();
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
    * Dialogs for editing product info
    ************************************************************************************************
     */

    private void editProduct(){
        this.mViewModel.getProduct(mProductId).observe(this, new Observer<Product>() {
            @Override
            public void onChanged(@Nullable Product product) {
                if (product != null){
                    if (mEditProductButtonIsClicked) {

                        EditProductInfoDialog editProductDialog = EditProductInfoDialog.newInstance(product);
                        editProductDialog.show(mFragmentManager, null);
                    }
                    mEditProductButtonIsClicked = false;
                }
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

                // Add score to account
                CurrentAccountScoreAdder.getsInstance(getApplicationContext())
                        .addScore(quantity * shoppingListIds.size() *
                                getResources().getInteger(R.integer.add_product_to_shopping_list));
                Log.d("Debug", "Score: " + (quantity * shoppingListIds.size() * 100));
                Log.d("Debug", "Quantity: " + quantity );
                Log.d("Debug", "list size: " + shoppingListIds.size());

                Toast.makeText(ProductActivity.this ,
                        "Added to  " + shoppingListIds.size() + " shopping list(s)",
                        Toast.LENGTH_LONG).show();
            }
        });
        dialog.show(mFragmentManager, null);
    }


}
