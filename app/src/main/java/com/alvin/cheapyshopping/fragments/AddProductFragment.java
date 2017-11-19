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
import com.alvin.cheapyshopping.olddb.models.ProductModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddProductFragment extends Fragment {

    public static interface AddProductFragmentListener {

        public void onDiscardNewProductOptionSelected(AddProductFragment fragment);

        public void onNewProductAdded(AddProductFragment fragment, ProductModel model);

    }


    private static final String ARGUMENT_CREATE_OPTIONS_MENU = "com.alvin.cheapyshopping.fragments.AddProductFragment.ARGUMENT_CREATE_OPTIONS_MENU";


    public static AddProductFragment newInstance() {
        return newInstance(true);
    }

    public static AddProductFragment newInstance(boolean createOptionsMenu) {
        AddProductFragment fragment = new AddProductFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARGUMENT_CREATE_OPTIONS_MENU, createOptionsMenu);
        fragment.setArguments(args);
        return fragment;
    }


    public AddProductFragment() {
    }


    private EditText mProductNameInput;

    private AddProductFragmentListener mAddProductFragmentListener;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AddProductFragmentListener) {
            this.mAddProductFragmentListener = (AddProductFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement AddProductFragmentListener");
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
        View v = inflater.inflate(R.layout.fragment_add_product, container, false);
        this.mProductNameInput = ((TextInputLayout) v.findViewById(R.id.input_product_name)).getEditText();
        return v;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.mAddProductFragmentListener = null;
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


    private void onDiscardOptionItemSelected(MenuItem item) {
        if (this.mAddProductFragmentListener != null) {
            this.mAddProductFragmentListener.onDiscardNewProductOptionSelected(this);
        }
    }

    private void onSaveOptionItemSelected(MenuItem item) {
        this.saveInput();
    }

    public void saveInput() {
        String productName = this.mProductNameInput.getText().toString();
        ProductModel model = new ProductModel(this.getContext());
        model.name = productName;
        boolean saved = model.save();

        if (saved && this.mAddProductFragmentListener != null) {
            this.mAddProductFragmentListener.onNewProductAdded(this, model);
        }
    }
}
