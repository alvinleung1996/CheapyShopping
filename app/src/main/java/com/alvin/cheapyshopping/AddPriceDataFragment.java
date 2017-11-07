package com.alvin.cheapyshopping;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alvin.cheapyshopping.db.models.PriceModel;

import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddPriceDataFragment extends Fragment {

    public static final String EXTRA_PRODUCT_ID = "com.alvin.cheapyshopping.AddPriceDataFragment.EXTRA_PRODUCT_ID";
    public static final String EXTRA_STORE_ID = "com.alvin.cheapyshopping.AddPriceDataFragment.EXTRA_STORE_ID";


    private static final String CHOICE_SINGLE = "SINGLE";
    private static final String CHOICE_MULTIPLE = "MULTIPLE";
    private static final String CHOICE_FREE = "FREE";
    private static final String CHOICE_DISCOUNT = "DISCOUNT";


    public static AddPriceDataFragment newInstance(long storeId, long productId) {
        AddPriceDataFragment fragment = new AddPriceDataFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(EXTRA_STORE_ID, storeId);
        bundle.putLong(EXTRA_PRODUCT_ID, productId);
        fragment.setArguments(bundle);
        return fragment;
    }


    public AddPriceDataFragment() {
        // Required empty public constructor
    }


    private long mProductId;
    private long mStoreId;

    private TextView mStoreIdTextView;
    private TextView mProductIdTextView;

    private RadioButton mSinglePriceRadioButton;
    private RadioButton mMultiplePriceRadioButton;
    private RadioButton mBuyXGetYFreeRadioButton;
    private RadioButton mBuyXGetDiscountRadioButton;
    private RadioButton[] radioButtons;
    private String mChoice;

    private EditText mSinglePriceInput;

    private EditText mMultiplePriceInput;
    private EditText mMultipleQuantityInput;

    private EditText mFreePriceInput;
    private EditText mFreeQuantityInput;
    private EditText mFreeYInput;

    private EditText mDiscountPriceInput;
    private EditText mDiscountQuantityInput;
    private EditText mDiscountPercentageInput;

    private Button mCancelButton;
    private Button mAddButton;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = savedInstanceState != null ? savedInstanceState : this.getArguments();
        if (args != null) {
            this.mStoreId = args.getLong(EXTRA_STORE_ID);
            this.mProductId = args.getLong(EXTRA_PRODUCT_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_add_price_data, container, false);

        this.mStoreIdTextView = v.findViewById(R.id.text_store_id);
        this.mProductIdTextView = v.findViewById(R.id.text_product_id);

        this.mSinglePriceRadioButton = v.findViewById(R.id.radio_single_price);
        this.mMultiplePriceRadioButton = v.findViewById(R.id.radio_multiple_price);
        this.mBuyXGetYFreeRadioButton = v.findViewById(R.id.radio_free);
        this.mBuyXGetDiscountRadioButton = v.findViewById(R.id.radio_discount);
        this.mSinglePriceRadioButton.setTag(CHOICE_SINGLE);
        this.mMultiplePriceRadioButton.setTag(CHOICE_MULTIPLE);
        this.mBuyXGetYFreeRadioButton.setTag(CHOICE_FREE);
        this.mBuyXGetDiscountRadioButton.setTag(CHOICE_DISCOUNT);
        this.radioButtons = new RadioButton[] {
                this.mSinglePriceRadioButton,
                this.mMultiplePriceRadioButton,
                this.mBuyXGetYFreeRadioButton,
                this.mBuyXGetDiscountRadioButton
        };
        for (RadioButton button : this.radioButtons) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AddPriceDataFragment.this.onRadioButtonClick(view);
                }
            });
        }

        this.mSinglePriceInput = ((TextInputLayout) v.findViewById(R.id.input_single_price)).getEditText();

        this.mMultiplePriceInput = ((TextInputLayout) v.findViewById(R.id.input_multiple_price)).getEditText();
        this.mMultipleQuantityInput = ((TextInputLayout) v.findViewById(R.id.input_multiple_quantity)).getEditText();

        this.mFreePriceInput = ((TextInputLayout) v.findViewById(R.id.input_free_price)).getEditText();
        this.mFreeQuantityInput = ((TextInputLayout) v.findViewById(R.id.input_free_x)).getEditText();
        this.mFreeYInput = ((TextInputLayout) v.findViewById(R.id.input_free_y)).getEditText();

        this.mDiscountPriceInput = ((TextInputLayout) v.findViewById(R.id.input_discount_price)).getEditText();
        this.mDiscountQuantityInput = ((TextInputLayout) v.findViewById(R.id.input_discount_quantity)).getEditText();
        this.mDiscountPercentageInput = ((TextInputLayout) v.findViewById(R.id.input_discount_percentage)).getEditText();

        this.mCancelButton = v.findViewById(R.id.button_cancel);
        this.mAddButton = v.findViewById(R.id.button_add);
        this.mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddPriceDataFragment.this.onCancelButtonClick(view);
            }
        });
        this.mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddPriceDataFragment.this.onAddButtonClick(view);
            }
        });

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.mStoreIdTextView.setText(Long.toString(this.mStoreId));
        this.mProductIdTextView.setText(Long.toString(this.mProductId));
    }

    private void onRadioButtonClick(View view) {
        this.mChoice = (String) view.getTag();
        for (RadioButton button : this.radioButtons) {
            button.setChecked(button.getTag().equals(this.mChoice));
        }
    }


    private void onCancelButtonClick(View view) {
        this.getFragmentManager().popBackStack();
    }

    private void onAddButtonClick(View view) {
        if (this.mChoice == null) {
            return;
        }

        PriceModel model = new PriceModel(this.getContext());
        model.foreignProductId = this.mProductId;
        model.foreignStoreId = this.mStoreId;
        model.time = new Date();

        try {
            switch (this.mChoice) {
                case CHOICE_SINGLE:
                    model.type = PriceModel.TYPE_SINGLE;
                    model.price = Double.valueOf(this.mSinglePriceInput.getText().toString());
                    break;
                case CHOICE_MULTIPLE:
                    model.type = PriceModel.TYPE_MULTIPLE;
                    model.price = Double.valueOf(this.mMultiplePriceInput.getText().toString());
                    model.priceData0 = Integer.valueOf(this.mMultipleQuantityInput.getText().toString());
                    break;
                case CHOICE_FREE:
                    model.type = PriceModel.TYPE_BUY_X_GET_Y_FREE;
                    model.price = Double.valueOf(this.mFreePriceInput.getText().toString());
                    model.priceData0 = Integer.valueOf(this.mFreeQuantityInput.getText().toString());
                    model.priceData1 = Integer.valueOf(this.mFreeYInput.getText().toString());
                    break;
                case CHOICE_DISCOUNT:
                    model.type = PriceModel.TYPE_BUY_X_GET_DISCOUNT;
                    model.price = Double.valueOf(this.mDiscountPriceInput.getText().toString());
                    model.priceData0 = Integer.valueOf(this.mDiscountQuantityInput.getText().toString());
                    model.priceData1 = Integer.valueOf(this.mDiscountPercentageInput.getText().toString()) / 100;
                    break;
            }

            if (model.save()) {
                Toast.makeText(this.getContext(), "Save model!", Toast.LENGTH_SHORT).show();

                this.getActivity().finish();
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

}
