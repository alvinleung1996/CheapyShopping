package com.alvin.cheapyshopping.fragments.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.alvin.cheapyshopping.db.entities.ShoppingList;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cheng on 11/29/2017.
 */

public class ChooseShoppingListsDialog extends DialogFragment{
    public static final String DIALOG_OK = "com.alvin.cheapyshopping.ChooseShoppingListsDialog.DIALOG_OK";
    public static final String DIALOG_CANCEL = "com.alvin.cheapyshopping.ChooseShoppingListsDialog.DIALOG_CANCEL";
    public static final String EXTRA_SHOPPING_LISTS = "com.alvin.cheapyshopping.ChooseShoppingListsDialog.EXTRA_SHOPPING_LISTS";

    public interface InteractionListener {
        void onSelectShoppingListsConfirmed(List<ShoppingList> shoppingLists);

    }



    public static ChooseShoppingListsDialog newInstance(List<ShoppingList> shoppingLists){
        ChooseShoppingListsDialog dialogFragment = new ChooseShoppingListsDialog();

        // Convert shoppingLists to Jason
        String shoppingListsDataJson = new Gson().toJson(shoppingLists);

        Bundle args = new Bundle();
        args.putString(EXTRA_SHOPPING_LISTS, shoppingListsDataJson);
        dialogFragment.setArguments(args);
        return dialogFragment;
    }

    private ChooseShoppingListsDialog.InteractionListener mInteractionListener;

    private List<ShoppingList> mShoppingLists;
    private List<String> mShoppingListsNames = new ArrayList<>();
    final List<Integer> mSelectedItems = new ArrayList<>();


    public ChooseShoppingListsDialog() {}

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Convert Jason data back to List<ShoppingList>
        Type listType = new TypeToken<ArrayList<ShoppingList>>(){}.getType();
        String listShoppingFromJson = getArguments().getString(EXTRA_SHOPPING_LISTS);
        mShoppingLists = new Gson().fromJson(listShoppingFromJson, listType);

        //Add shopping list names
        for (int i = 0; i < mShoppingLists.size(); i++){
            mShoppingListsNames.add(mShoppingLists.get(i).getName());
        }

        String[] items = mShoppingListsNames.toArray(new String[0]);

        return new AlertDialog.Builder(getActivity())
                .setTitle("Save product to shopping list(s)")
                .setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int index, boolean isChecked) {
                        if (isChecked) {
                            // If the user checked the item, add it to the selected items
                            mSelectedItems.add(index);
                        } else if (mSelectedItems.contains(index)) {
                            // Else, if the item is already in the array, remove it
                            mSelectedItems.remove(Integer.valueOf(index));
                        }
                    }
                })
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int index) {
                        // Filter for checked shoppingList from ArrayList
                        List<ShoppingList> selectedShoppingLists = new ArrayList<>();
                        for(int i = 0; i < mSelectedItems.size(); i++){
                            selectedShoppingLists.add(mShoppingLists.get(mSelectedItems.get(i)));
                        }
                        onShoppingListsSelected(selectedShoppingLists);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dismiss();
                    }
                })
                .create();

    }

    public void setInteractionListener(ChooseShoppingListsDialog.InteractionListener listener) {
        this.mInteractionListener = listener;
    }

    private void onShoppingListsSelected(List<ShoppingList> shoppingLists) {
        if (this.mInteractionListener != null) {
            mInteractionListener.onSelectShoppingListsConfirmed(shoppingLists);
        }
    }

}