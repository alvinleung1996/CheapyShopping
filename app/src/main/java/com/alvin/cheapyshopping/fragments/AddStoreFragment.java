package com.alvin.cheapyshopping.fragments;


import android.arch.core.util.Function;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alvin.cheapyshopping.R;
import com.alvin.cheapyshopping.databinding.AddStoreFragmentBinding;
import com.alvin.cheapyshopping.viewmodels.AddStoreFragmentViewModel;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddStoreFragment extends Fragment {

    public interface InteractionListener {

        void onDiscardOptionSelected(AddStoreFragment fragment);

        void onNewStoreAdded(AddStoreFragment fragment, long storeId);

    }


    private static final String ARGUMENT_CREATE_OPTIONS_MENU = "com.alvin.cheapyshopping.fragments.AddStoreFragment.ARGUMENT_CREATE_OPTIONS_MENU";



    public static AddStoreFragment newInstance(boolean createOptionsMenu) {
        AddStoreFragment fragment = new AddStoreFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARGUMENT_CREATE_OPTIONS_MENU, createOptionsMenu);
        fragment.setArguments(args);
        return fragment;
    }


    public AddStoreFragment() {
        // Required empty public constructor
    }


    private AddStoreFragmentViewModel mViewModel;

    private AddStoreFragmentBinding mBinding;

    private InteractionListener mInteractionListener;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = this.getArguments();
        if (args != null) {
            this.setHasOptionsMenu(args.getBoolean(ARGUMENT_CREATE_OPTIONS_MENU, true));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.mBinding = AddStoreFragmentBinding.inflate(inflater, container, false);
        return this.mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.mViewModel = ViewModelProviders.of(this).get(AddStoreFragmentViewModel.class);

        this.mBinding.setOnAddFromPlaceApiButtonClick(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddStoreFragment.this.onAddFromPlaceAPIButtonClick(view);
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_PLACE_PICKER:
                this.onPlacePickerResult(requestCode, resultCode, data);
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.add_store_fragment_menu, menu);
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


    public void setInteractionListener(InteractionListener listener) {
        this.mInteractionListener = listener;
    }


    private void onDiscardOptionItemSelected(MenuItem item) {
        if (this.mInteractionListener != null) {
            this.mInteractionListener.onDiscardOptionSelected(this);
        }
    }

    private void onSaveOptionItemSelected(MenuItem item) {
        this.saveInput();
    }



    private static final int REQUEST_PLACE_PICKER = 1;

    private void onAddFromPlaceAPIButtonClick(View view) {
        try {
            Intent intent = new PlacePicker.IntentBuilder().build(this.getActivity());
            this.startActivityForResult(intent, REQUEST_PLACE_PICKER);
        } catch (GooglePlayServicesRepairableException e) {
            Log.e("add store", "Play service exception", e);
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.e("add store", "No Play Service", e);
        }
    }

    private void onPlacePickerResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Place place = PlacePicker.getPlace(this.getContext(), data);
            String name = place.getName().toString();
            String address = place.getAddress().toString();
            String placeId = place.getId();
            double longitude = place.getLatLng().longitude;
            double latitude = place.getLatLng().latitude;

            Toast.makeText(this.getContext(), name+", "+longitude+", "+latitude, Toast.LENGTH_SHORT).show();

            this.mViewModel.addStore(name, address, placeId, longitude, latitude).observe(this, new Observer<Long>() {
                @Override
                public void onChanged(@Nullable Long storeId) {
                    if (AddStoreFragment.this.mInteractionListener != null) {
                        AddStoreFragment.this.mInteractionListener.onNewStoreAdded(AddStoreFragment.this, storeId);
                    }
                }
            });
        }
    }



    public void saveInput() {
        String storeName = this.mBinding.inputLayoutStoreName.getEditText().getText().toString();
        String storeLocation = this.mBinding.inputLayoutStoreLocation.getEditText().getText().toString();

        boolean error = false;

        if (storeName.isEmpty()) {
            this.mBinding.inputLayoutStoreName.setError("enter a store name pls");
            error = true;
        } else {
            this.mBinding.inputLayoutStoreName.setError(null);
        }
        if (storeLocation.isEmpty()) {
            this.mBinding.inputLayoutStoreLocation.setError("enter a store location pls");
            error = true;
        } else {
            this.mBinding.inputLayoutStoreLocation.setError(null);
        }

        if (error) {
            return;
        }

        Toast.makeText(this.getContext(), "The input will be removed soon and the input is not working now", Toast.LENGTH_SHORT).show();

//        this.mViewModel.addStore(storeName, storeLocation, new Function<long[], Void>() {
//            @Override
//            public Void apply(long[] storeIds) {
//                if (AddStoreFragment.this.mInteractionListener != null) {
//                    AddStoreFragment.this.mInteractionListener.onNewStoreAdded(AddStoreFragment.this, storeIds[0]);
//                }
//                return null;
//            }
//        });
    }
}
