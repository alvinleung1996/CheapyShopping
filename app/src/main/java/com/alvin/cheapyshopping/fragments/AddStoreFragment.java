package com.alvin.cheapyshopping.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.alvin.cheapyshopping.R;
import com.alvin.cheapyshopping.db.models.StoreModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddStoreFragment extends Fragment {

    public interface InteractionListener {

        void onDiscardOptionSelected(AddStoreFragment fragment);

        void onNewStoreAdded(AddStoreFragment fragment, StoreModel store);

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


    private TextInputLayout mStoreNameInputLayout;
    private EditText mStoreNameInput;
    private TextInputLayout mStoreLocationInputLayout;
    private EditText mStoreLocationInput;

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
        View view = inflater.inflate(R.layout.fragment_add_store, container, false);
        this.mStoreNameInputLayout = view.findViewById(R.id.input_layout_store_name);
        this.mStoreNameInput = this.mStoreNameInputLayout.getEditText();
        this.mStoreLocationInputLayout = view.findViewById(R.id.input_layout_store_location);
        this.mStoreLocationInput = this.mStoreLocationInputLayout.getEditText();
        return view;
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

    public void saveInput() {
        String storeName = this.mStoreNameInput.getText().toString();
        String storeLocation = this.mStoreLocationInput.getText().toString();

        boolean error = false;

        if (storeName.isEmpty()) {
            this.mStoreNameInputLayout.setError("enter a store name pls");
            error = true;
        } else {
            this.mStoreNameInputLayout.setError(null);
        }
        if (storeLocation.isEmpty()) {
            this.mStoreLocationInputLayout.setError("enter a store location pls");
            error = true;
        } else {
            this.mStoreLocationInputLayout.setError(null);
        }

        if (error) {
            return;
        }

        StoreModel model = new StoreModel(this.getContext());
        model.name = storeName;
        model.location = storeLocation;
        boolean saved = model.save();

        if (saved && this.mInteractionListener != null) {
            this.mInteractionListener.onNewStoreAdded(this, model);
        }
    }
}
