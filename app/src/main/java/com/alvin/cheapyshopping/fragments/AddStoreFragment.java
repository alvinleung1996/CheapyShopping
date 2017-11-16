package com.alvin.cheapyshopping.fragments;


import android.content.Context;
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

    public static interface AddStoreFragmentListener {

        public void onDiscardNewStoreOptionSelected(AddStoreFragment fragment);

        public void onNewStoreAdded(AddStoreFragment fragment, StoreModel model);

    }


    private static final String ARGUMENT_CREATE_OPTIONS_MENU = "com.alvin.cheapyshopping.fragments.AddStoreFragment.ARGUMENT_CREATE_OPTIONS_MENU";


    public static AddStoreFragment newInstance() {
        return newInstance(true);
    }

    public static AddStoreFragment newInstance(boolean createOptionsMenu) {
        AddStoreFragment fragment = new AddStoreFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARGUMENT_CREATE_OPTIONS_MENU, createOptionsMenu);
        fragment.setArguments(args);
        return new AddStoreFragment();
    }


    public AddStoreFragment() {
        // Required empty public constructor
    }


    private EditText mStoreNameInput;
    private EditText mStoreLocationInput;

    private AddStoreFragmentListener mAddStoreFragmentListener;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AddStoreFragmentListener) {
            this.mAddStoreFragmentListener = (AddStoreFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement AddStoreFragmentListener");
        }
    }

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
        View v = inflater.inflate(R.layout.fragment_add_store, container, false);
        this.mStoreNameInput = ((TextInputLayout) v.findViewById(R.id.input_store_name)).getEditText();
        this.mStoreLocationInput = ((TextInputLayout) v.findViewById(R.id.input_store_location)).getEditText();
        return v;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.mAddStoreFragmentListener = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.add_store_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_discard:
                this.onDiscardOptionItemSelected(item);
                return true;
            case R.id.menu_item_save:
                this.onSaveOptionItemSelected(item);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void onDiscardOptionItemSelected(MenuItem item) {
        if (this.mAddStoreFragmentListener != null) {
            this.mAddStoreFragmentListener.onDiscardNewStoreOptionSelected(this);
        }
    }

    private void onSaveOptionItemSelected(MenuItem item) {
        this.saveInput();
    }

    public void saveInput() {
        String storeName = this.mStoreNameInput.getText().toString();
        String storeLocation = this.mStoreLocationInput.getText().toString();

        StoreModel model = new StoreModel(this.getContext());
        model.name = storeName;
        model.location = storeLocation;
        boolean saved = model.save();

        if (saved && this.mAddStoreFragmentListener != null) {
            this.mAddStoreFragmentListener.onNewStoreAdded(this, model);
        }
    }
}
