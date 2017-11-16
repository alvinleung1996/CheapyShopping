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
import android.widget.RadioButton;
import android.widget.TextView;

import com.alvin.cheapyshopping.R;
import com.alvin.cheapyshopping.db.models.PriceModel;

import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddPriceFragment extends Fragment {

    public static interface AddPriceFragmentListener {

        public void onDiscardNewPriceOptionSelected(AddPriceFragment fragment);

        public void onNewPriceAdded(AddPriceFragment fragment, PriceModel model);

    }

    public static final String ARGUMENT_PRODUCT_ID = "com.alvin.cheapyshopping.fragments.AddPriceFragment.ARGUMENT_PRODUCT_ID";
    public static final String ARGUMENT_STORE_ID = "com.alvin.cheapyshopping.fragments.AddPriceFragment.ARGUMENT_STORE_ID";
    public static final String ARGUMENT_CREATE_OPTIONS_MENU = "com.alvin.cheapyshopping.fragments.AddPriceFragment.ARGUMENT_CREATE_OPTIONS_MENU";


    private static final String CHOICE_SINGLE = "SINGLE";
    private static final String CHOICE_MULTIPLE = "MULTIPLE";
    private static final String CHOICE_FREE = "FREE";
    private static final String CHOICE_DISCOUNT = "DISCOUNT";


    public static AddPriceFragment newInstance(long storeId, long productId) {
        return newInstance(storeId, productId, true);
    }

    public static AddPriceFragment newInstance(long storeId, long productId, boolean createOptionsMenu) {
        AddPriceFragment fragment = new AddPriceFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(ARGUMENT_STORE_ID, storeId);
        bundle.putLong(ARGUMENT_PRODUCT_ID, productId);
        bundle.putBoolean(ARGUMENT_CREATE_OPTIONS_MENU, createOptionsMenu);
        fragment.setArguments(bundle);
        return fragment;
    }


    public AddPriceFragment() {
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

    private AddPriceFragmentListener mAddPriceFragmentListener;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AddPriceFragmentListener) {
            this.mAddPriceFragmentListener = (AddPriceFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement AddPriceFragmentListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = this.getArguments();
        if (args != null) {
            this.mStoreId = args.getLong(ARGUMENT_STORE_ID);
            this.mProductId = args.getLong(ARGUMENT_PRODUCT_ID);
            this.setHasOptionsMenu(args.getBoolean(ARGUMENT_CREATE_OPTIONS_MENU, true));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_add_price, container, false);

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
                    AddPriceFragment.this.onRadioButtonClick(view);
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

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO: Remove these debug code
        this.mStoreIdTextView.setText("StoreId: " + Long.toString(this.mStoreId));
        this.mProductIdTextView.setText("ProductId: " + Long.toString(this.mProductId));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.mAddPriceFragmentListener = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.add_price_fragment_menu, menu);
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


    private void onRadioButtonClick(View view) {
        this.mChoice = (String) view.getTag();
        for (RadioButton button : this.radioButtons) {
            button.setChecked(button.getTag().equals(this.mChoice));
        }
    }

    private void onDiscardOptionItemSelected(MenuItem item) {
        if (this.mAddPriceFragmentListener != null) {
            this.mAddPriceFragmentListener.onDiscardNewPriceOptionSelected(this);
        }
    }

    private void onSaveOptionItemSelected(MenuItem item) {
        this.saveInput();
    }


    public void saveInput() {
        if (this.mChoice == null) {
            return;
        }

        PriceModel model = new PriceModel(this.getContext());
        model.foreignProductId = this.mProductId;
        model.foreignStoreId = this.mStoreId;
        model.time = new Date();

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

        boolean saved = model.save();

        if (saved && this.mAddPriceFragmentListener != null) {
            this.mAddPriceFragmentListener.onNewPriceAdded(this, model);
        }
    }

}
