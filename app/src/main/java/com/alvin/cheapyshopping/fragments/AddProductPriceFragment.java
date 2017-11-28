package com.alvin.cheapyshopping.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;

import com.alvin.cheapyshopping.R;
import com.alvin.cheapyshopping.databinding.AddProductPriceFragmentBinding;
import com.alvin.cheapyshopping.db.entities.Price;
import com.alvin.cheapyshopping.db.entities.Store;
import com.alvin.cheapyshopping.viewmodels.AddProductPriceFragmentViewModel;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddProductPriceFragment extends Fragment {

    public interface InteractionListener {

        void onDiscardNewPriceOptionSelected(AddProductPriceFragment fragment);

        void onNewPriceAdded(AddProductPriceFragment fragment, long rowId);

    }


    private static final int REQUEST_PICK_PLACE = 1;

    private static final String ARGUMENT_PRODUCT_ID = "com.alvin.cheapyshopping.fragments.AddProductPriceFragment.ARGUMENT_PRODUCT_ID";
    private static final String ARGUMENT_CREATE_OPTIONS_MENU = "com.alvin.cheapyshopping.fragments.AddProductPriceFragment.ARGUMENT_CREATE_OPTIONS_MENU";
    private static final String ARGUMENT_STORE_ID = "com.alvin.cheapyshopping.fragments.AddProductPriceFragment.ARGUMENT_STORE_ID";

    public static AddProductPriceFragment newInstance(String productId, boolean createOptionsMenu, String storeId) {
        AddProductPriceFragment fragment = new AddProductPriceFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARGUMENT_PRODUCT_ID, productId);
        bundle.putBoolean(ARGUMENT_CREATE_OPTIONS_MENU, createOptionsMenu);
        bundle.putString(ARGUMENT_STORE_ID, storeId);
        fragment.setArguments(bundle);
        return fragment;
    }


    public AddProductPriceFragment() {
        // Required empty public constructor
    }


    private String mProductId;

    private AddProductPriceFragmentViewModel mViewModel;
    private AddProductPriceFragmentBinding mBinding;

    private InteractionListener mInteractionListener;

    private RadioButton[] radioButtons;
    private Integer mChoice;

    private boolean mStoreSelected;
    private String mStoreId;
    private String mStoreName;
    private String mStoreAddress;
    private double mStoreLongitude;
    private double mStoreLatitude;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = this.getArguments();
        if (args != null) {
            this.mProductId = args.getString(ARGUMENT_PRODUCT_ID);
            this.setHasOptionsMenu(args.getBoolean(ARGUMENT_CREATE_OPTIONS_MENU, true));

            this.mStoreId = args.getString(ARGUMENT_STORE_ID);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.mBinding = AddProductPriceFragmentBinding.inflate(inflater, container, false);

        this.mBinding.radioSinglePrice.setTag(Price.TYPE_SINGLE);
        this.mBinding.radioMultiplePrice.setTag(Price.TYPE_MULTIPLE);
        this.mBinding.radioFree.setTag(Price.TYPE_BUY_X_GET_Y_FREE);
        this.mBinding.radioDiscount.setTag(Price.TYPE_DISCOUNT_FOR_X);
        this.radioButtons = new RadioButton[] {
                this.mBinding.radioSinglePrice,
                this.mBinding.radioMultiplePrice,
                this.mBinding.radioFree,
                this.mBinding.radioDiscount
        };
        for (RadioButton button : this.radioButtons) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AddProductPriceFragment.this.onRadioButtonClick(view);
                }
            });
        }

        this.mBinding.setOnPickPlaceButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddProductPriceFragment.this.onPickPlaceButtonClick((Button) view);
            }
        });

        return this.mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.mViewModel = ViewModelProviders.of(this).get(AddProductPriceFragmentViewModel.class);

        // If store already exist
        if(this.mStoreId != null){
            this.mViewModel.findStoreByStoreId(mStoreId).observe(this, new Observer<Store>() {
                @Override
                public void onChanged(@Nullable Store store) {
                    if (store != null){
                        Log.d("Debug: ", "Store_" + store.getName() + " detected");
                        // Disable the place pick button
                        AddProductPriceFragment.this.mBinding.buttonPickPlace.setVisibility(View.GONE);

                        // Setup store information for save
                        AddProductPriceFragment.this.mStoreSelected = true;
                        AddProductPriceFragment.this.mStoreId = store.getStoreId();
                        AddProductPriceFragment.this.mStoreName = store.getName();
                        AddProductPriceFragment.this.mStoreAddress = store.getAddress();
                        AddProductPriceFragment.this.mStoreLongitude = store.getLongitude();
                        AddProductPriceFragment.this.mStoreLatitude = store.getLatitude();

                    }
                }
            });
        }
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_PICK_PLACE:
                if (resultCode == RESULT_OK) {
                    Place place = PlacePicker.getPlace(this.getContext(), data);
                    if (place != null) {
                        this.mStoreSelected = true;
                        this.mStoreId = place.getId();
                        this.mStoreName = place.getName().toString();
                        this.mStoreAddress = place.getAddress().toString();
                        this.mStoreLongitude = place.getLatLng().longitude;
                        this.mStoreLatitude = place.getLatLng().latitude;
                    } else {
                        this.mStoreSelected = false;
                        this.mStoreId = null;
                        this.mStoreName = null;
                        this.mStoreAddress = null;
                        this.mStoreLongitude = -1;
                        this.mStoreLatitude = -1;
                    }
                }
        }
    }



    public void setInteractionListener(InteractionListener listener) {
        this.mInteractionListener = listener;
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.add_price_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_discard:
                this.onDiscardOptionItemSelected(item);
                return true;
            case R.id.item_save:
                this.onSaveOptionItemSelected(item);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    private void onDiscardOptionItemSelected(MenuItem item) {
        if (this.mInteractionListener != null) {
            this.mInteractionListener.onDiscardNewPriceOptionSelected(this);
        }
    }

    private void onSaveOptionItemSelected(MenuItem item) {
        this.saveInput();
    }



    private void onRadioButtonClick(View view) {
        this.mChoice = (Integer) view.getTag();
        for (RadioButton button : this.radioButtons) {
            button.setChecked(button.getTag().equals(this.mChoice));
        }
    }

    private void onPickPlaceButtonClick(Button button) {
        try {
            Intent intent = new PlacePicker.IntentBuilder().build(this.getActivity());
            this.startActivityForResult(intent, REQUEST_PICK_PLACE);
        } catch (GooglePlayServicesNotAvailableException
                | GooglePlayServicesRepairableException e) {
            Log.e("map", "Cannot start place picker", e);
        }
    }


    public void saveInput() {
        if (this.mChoice == null) {
            return;
        }

        int priceType;
        double priceTotal;
        int priceQuantity;
        int priceFreeQuantity = -1;
        double priceDiscount = -1;

        switch (this.mChoice) {
            case Price.TYPE_SINGLE:
                priceType = Price.TYPE_SINGLE;
                priceTotal = Double.parseDouble(this.mBinding.inputSinglePrice.getEditText().getText().toString());
                priceQuantity = 1;
                break;

            case Price.TYPE_MULTIPLE:
                priceType = Price.TYPE_MULTIPLE;
                priceTotal = Double.parseDouble(this.mBinding.inputMultiplePrice.getEditText().getText().toString());
                priceQuantity = Integer.parseInt(this.mBinding.inputMultipleQuantity.getEditText().getText().toString());
                break;

            case Price.TYPE_BUY_X_GET_Y_FREE:
                priceType = Price.TYPE_BUY_X_GET_Y_FREE;
                priceTotal = Double.parseDouble(this.mBinding.inputFreePrice.getEditText().getText().toString());
                priceQuantity = Integer.parseInt(this.mBinding.inputFreeX.getEditText().getText().toString());
                priceFreeQuantity = Integer.parseInt(this.mBinding.inputFreeY.getEditText().getText().toString());
                break;

            case Price.TYPE_DISCOUNT_FOR_X:
                priceType = Price.TYPE_DISCOUNT_FOR_X;
                priceTotal = Double.parseDouble(this.mBinding.inputDiscountPrice.getEditText().getText().toString());
                priceQuantity = Integer.parseInt(this.mBinding.inputDiscountQuantity.getEditText().getText().toString());
                priceDiscount = Double.parseDouble(this.mBinding.inputDiscountPercentage.getEditText().getText().toString()) / 100;
                break;

            default:
                throw new RuntimeException("Unknow price type!");
        }


        if (!this.mStoreSelected) {
            return;
        }

        this.mViewModel.addPrice(this.mProductId,
                priceType, priceTotal, priceQuantity, priceFreeQuantity, priceDiscount,
                this.mStoreId, this.mStoreName, this.mStoreAddress, this.mStoreLongitude, this.mStoreLatitude)
                .observe(this, new Observer<long[]>() {
                    @Override
                    public void onChanged(@Nullable long[] rowIds) {
                        if (rowIds != null && AddProductPriceFragment.this.mInteractionListener != null) {
                            AddProductPriceFragment.this.mInteractionListener
                                    .onNewPriceAdded(AddProductPriceFragment.this, rowIds[0]);
                        }
                    }
                });



    }

}
