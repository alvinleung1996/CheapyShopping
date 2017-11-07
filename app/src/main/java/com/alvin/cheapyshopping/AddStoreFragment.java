package com.alvin.cheapyshopping;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alvin.cheapyshopping.db.models.ProductModel;
import com.alvin.cheapyshopping.db.models.StoreModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddStoreFragment extends Fragment {


    public AddStoreFragment() {
        // Required empty public constructor
    }

    public static AddStoreFragment newInstance() {
        return new AddStoreFragment();
    }


    private EditText mStoreNameInput;
    private EditText mStoreLocationInput;


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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Button cancelButton = this.getView().findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddStoreFragment.this.onCancelButtonClick(view);
            }
        });

        Button addButton = this.getView().findViewById(R.id.button_add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddStoreFragment.this.onAddButtonClick(view);
            }
        });
    }



    private void onCancelButtonClick(View view) {
        this.getFragmentManager().popBackStack();
    }

    private void onAddButtonClick(View view) {
        String storeName = this.mStoreNameInput.getText().toString();
        String storeLocation = this.mStoreLocationInput.getText().toString();

        StoreModel model = new StoreModel(this.getContext());
        model.name = storeName;
        model.location = storeLocation;

        if (model.save()) {
            Toast.makeText(this.getContext(), "Store saved!", Toast.LENGTH_SHORT).show();

            this.getFragmentManager().popBackStack(SelectStoreFragment.FRAGMENT_TRANSACTION_NEW_STORE, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            this.getFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, SelectProductFragment.newInstance(model.storeId))
                    .addToBackStack(null).commit();
        }
    }
}
