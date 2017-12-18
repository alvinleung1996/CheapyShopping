package com.alvin.cheapyshopping.fragments.dialogs;

import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.alvin.cheapyshopping.databinding.ModifyShoppingListProductRelationDialogBinding;
import com.alvin.cheapyshopping.utils.Promise;
import com.alvin.cheapyshopping.viewmodels.ModifyShoppingListProductRelationDialogFragmentViewModel;

/**
 * Created by Alvin on 2/12/2017.
 */

public class ModifyShoppingListProductRelationDialogFragment extends BottomSheetDialogFragment {

    private static final String ARGUMENT_SHOPPING_LIST_ID
            = "com.alvin.cheapyshopping.fragments.dialogs.ModifyShoppingListProductRelationDialogFragment.ARGUMENT_SHOPPING_LIST_ID";
    private static final String ARGUMENT_PRODUCT_ID
            = "com.alvin.cheapyshopping.fragments.dialogs.ModifyShoppingListProductRelationDialogFragment.ARGUMENT_PRODUCT_ID";


    public interface InteractionListener {

        void onModificationCancelled();

        void onRelationModified();

        void onRelationRemoved();

    }


    public static ModifyShoppingListProductRelationDialogFragment newInstance(String shoppingListId,
                                                                              String productId) {
        ModifyShoppingListProductRelationDialogFragment fragment
                = new ModifyShoppingListProductRelationDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARGUMENT_SHOPPING_LIST_ID, shoppingListId);
        args.putString(ARGUMENT_PRODUCT_ID, productId);
        fragment.setArguments(args);
        return fragment;
    }


    private ModifyShoppingListProductRelationDialogFragmentViewModel mViewModel;
    private BottomSheetBehavior mBehavior;
    private ModifyShoppingListProductRelationDialogBinding mBinding;

    private InteractionListener mInteractionListener;


    public ModifyShoppingListProductRelationDialogFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (container == null) {
            // container is null, and I don't know why
            // But a container is required so that the layout parameter can be inflated
            // The inflated view will be inflated to R.id.design_bottom_sheet,
            // which is a FrameLayout.
            // Here in case the container is null, just createReference a mock R.id.design_bottom_sheet container,
            // i.e. a FrameLayout instance.
            container = new FrameLayout(inflater.getContext());
        }
        this.mBinding = ModifyShoppingListProductRelationDialogBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.mViewModel = ViewModelProviders.of(this)
                .get(ModifyShoppingListProductRelationDialogFragmentViewModel.class);

        Bundle args = this.getArguments();
        String shoppingListId = null;
        String productId = null;
        if (args != null) {
            shoppingListId = args.getString(ARGUMENT_SHOPPING_LIST_ID);
            productId = args.getString(ARGUMENT_PRODUCT_ID);
        }
        if (shoppingListId == null || productId == null) {
            throw new RuntimeException("Missing Arguments");
        }
        this.mViewModel.setShoppingListProductRelationId(shoppingListId, productId);

        View bottomSheet = this.getDialog().findViewById(android.support.design.R.id.design_bottom_sheet);

        // Wait for view to layout
        bottomSheet.post(() -> {
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomSheet.getLayoutParams();
            int parentWidth = ((ViewGroup) bottomSheet.getParent()).getWidth();
            int maxWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 360, this.getResources().getDisplayMetrics());
            layoutParams.width = parentWidth > maxWidth ? maxWidth : ViewGroup.LayoutParams.MATCH_PARENT;
            bottomSheet.setLayoutParams(layoutParams);
        });

        this.mBehavior = BottomSheetBehavior.from(bottomSheet);
        this.mBehavior.setSkipCollapsed(true);

        this.mViewModel.getProduct().observe(this, this.mBinding::setProduct);

        this.mViewModel.getQuantity().observe(this, v -> this.mBinding.setQuantity(v));
        this.mBinding.setOnQuantityChangedListener((picker, oldValue, newValue) ->
                this.mViewModel.setQuantity(newValue));

        this.mBinding.setOnCancelButtonClickListener(v -> this.onCancelButtonClick((Button) v));
        this.mBinding.setOnOkButtonClickListener(v -> this.onOkButtonClick((Button) v));

        this.mBinding.setOnRemoveButtonClickListener(v -> this.onRemoveButtonClick((Button) v));
    }

    @Override
    public void onStart() {
        super.onStart();
        this.mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        this.notifyInteractionListenerCancelled();
    }



    public void setInteractionListener(InteractionListener listener) {
        this.mInteractionListener = listener;
    }

    private void notifyInteractionListenerCancelled() {
        if (this.mInteractionListener != null) {
            this.mInteractionListener.onModificationCancelled();
        }
    }

    private void notifyInteractionListenerModified() {
        if (this.mInteractionListener != null) {
            this.mInteractionListener.onRelationModified();
        }
    }

    private void notifyInteractionListenerRemoved() {
        if (this.mInteractionListener != null) {
            this.mInteractionListener.onRelationRemoved();
        }
    }



    private void onCancelButtonClick(Button button) {
        this.getDialog().cancel();
    }

    private void onOkButtonClick(Button button) {
        this.mViewModel.updateShoppingListProductRelation().onResolved(this, v -> {
            this.notifyInteractionListenerModified();
            this.getDialog().dismiss();
            return null;
        });
    }

    private void onRemoveButtonClick(Button button) {
        this.mViewModel.deleteShoppingListProductRelation().onResolved(this, v -> {
            this.notifyInteractionListenerRemoved();
            this.getDialog().dismiss();
            return null;
        });
    }

}
