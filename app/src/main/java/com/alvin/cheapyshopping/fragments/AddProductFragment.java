package com.alvin.cheapyshopping.fragments;


import android.arch.core.util.Function;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.alvin.cheapyshopping.R;
import com.alvin.cheapyshopping.databinding.AddProductFragmentBinding;
import com.alvin.cheapyshopping.viewmodels.AddProductFragmentViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddProductFragment extends Fragment {

    public interface InteractionListener {

        void onDiscardOptionSelected(AddProductFragment fragment);

        void onNewProductAdded(AddProductFragment fragment, String productId);

    }


    private static final String ARGUMENT_CREATE_OPTIONS_MENU = "com.alvin.cheapyshopping.fragments.AddProductFragment.ARGUMENT_CREATE_OPTIONS_MENU";



    public static AddProductFragment newInstance(boolean createOptionsMenu) {
        AddProductFragment fragment = new AddProductFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARGUMENT_CREATE_OPTIONS_MENU, createOptionsMenu);
        fragment.setArguments(args);
        return fragment;
    }


    public AddProductFragment() {
        // Required empty public constructor
    }


    private AddProductFragmentViewModel mViewModel;

    private AddProductFragmentBinding mBinding;

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
        this.mBinding = AddProductFragmentBinding.inflate(inflater, container, false);
        return this.mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.mViewModel = ViewModelProviders.of(this).get(AddProductFragmentViewModel.class);
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.add_product_fragment_menu, menu);
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
        String productName = this.mBinding.inputLayoutProductName.getEditText().getText().toString();
        String productDesscription = this.mBinding.inputLayoutProductDescription.getEditText().getText().toString();

        boolean error = false;

        if (productName.isEmpty()) {
            this.mBinding.inputLayoutProductName.setError("enter a product name pls");
            error = true;
        } else {
            this.mBinding.inputLayoutProductName.setError(null);
        }
        if (productDesscription.isEmpty()) {
            this.mBinding.inputLayoutProductDescription.setError("enter a product description pls");
            error = true;
        } else {
            this.mBinding.inputLayoutProductDescription.setError(null);
        }

        if (error) {
            return;
        }

        this.mViewModel.addProduct(productName, productDesscription, new Function<String, Void>() {
            @Override
            public Void apply(String productId) {
                if (AddProductFragment.this.mInteractionListener != null) {
                    AddProductFragment.this.mInteractionListener.onNewProductAdded(AddProductFragment.this, productId);
                }
                return null;
            }
        });
    }
}
