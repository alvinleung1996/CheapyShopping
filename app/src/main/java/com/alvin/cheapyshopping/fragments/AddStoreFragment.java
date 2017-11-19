package com.alvin.cheapyshopping.fragments;


import android.arch.lifecycle.ViewModelProviders;
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
import com.alvin.cheapyshopping.databinding.AddStoreFragmentBinding;
import com.alvin.cheapyshopping.olddb.models.StoreModel;
import com.alvin.cheapyshopping.viewmodels.AddStoreFragmentViewModel;

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

        long storeId = this.mViewModel.addStore(storeName, storeLocation);
        this.mInteractionListener.onNewStoreAdded(this, storeId);
    }
}
