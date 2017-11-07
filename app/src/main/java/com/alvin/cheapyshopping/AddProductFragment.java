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

/**
 * A simple {@link Fragment} subclass.
 */
public class AddProductFragment extends Fragment {

    private static final String EXTRA_STORE_ID = "com.alvin.cheapyshopping.AddProductFragment.EXTRA_STORE_ID";


    public static AddProductFragment newInstance(long storeId) {
        AddProductFragment fragment = new AddProductFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(EXTRA_STORE_ID, storeId);
        fragment.setArguments(bundle);
        return fragment;
    }


    public AddProductFragment() {
    }



    private long mStoreId;

    private EditText mProductNameInput;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = savedInstanceState != null ? savedInstanceState : this.getArguments();
        if (args != null) {
            this.mStoreId = args.getLong(EXTRA_STORE_ID);
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Button cancelButton = this.getView().findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddProductFragment.this.onCancelButtonClick(view);
            }
        });

        Button addButton = this.getView().findViewById(R.id.button_add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddProductFragment.this.onAddButtonClick(view);
            }
        });
    }


    private void onCancelButtonClick(View view) {
        this.getFragmentManager().popBackStack();
    }

    private void onAddButtonClick(View view) {
        String productName = this.mProductNameInput.getText().toString();
        ProductModel model = new ProductModel(this.getContext());
        model.name = productName;

        if (model.save()) {
            Toast.makeText(this.getContext(), "product saved!", Toast.LENGTH_SHORT).show();

            this.getFragmentManager().popBackStack(SelectProductFragment.FRAGMENT_TRANSACTION_NEW_PRODUCT, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            this.getFragmentManager().beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.fragment_container, AddPriceDataFragment.newInstance(this.mStoreId, model.productId))
                    .commit();
        }
    }
}
