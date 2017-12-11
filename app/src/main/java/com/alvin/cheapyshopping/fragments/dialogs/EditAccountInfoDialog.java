package com.alvin.cheapyshopping.fragments.dialogs;

import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.alvin.cheapyshopping.databinding.EditAccountInfoDialogBinding;
import com.alvin.cheapyshopping.db.entities.Account;
import com.alvin.cheapyshopping.viewmodels.EditAccountInfoDialogViewModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * Created by cheng on 12/9/2017.
 */

public class EditAccountInfoDialog extends DialogFragment{
    public static final String DIALOG_OK = "com.alvin.cheapyshopping.EditAccountInfoDialog.DIALOG_OK";
    public static final String DIALOG_CANCEL = "com.alvin.cheapyshopping.EditAccountInfoDialog.DIALOG_CANCEL";
    public static final String EXTRA_ACCOUNT = "com.alvin.cheapyshopping.EditAccountInfoDialog.EXTRA_ACCOUNT";

    public static EditAccountInfoDialog newInstance(Account account) {
        EditAccountInfoDialog dialogFragment = new EditAccountInfoDialog();

        // Convert product to Jason
        String accountDataJason = new Gson().toJson(account);

        Bundle args = new Bundle();
        args.putString(EXTRA_ACCOUNT, accountDataJason);
        dialogFragment.setArguments(args);

        return dialogFragment;
    }

    private EditAccountInfoDialogBinding mBinding;
    private EditAccountInfoDialogViewModel mViewModel;

    private Account mAccount;
    private Account mEditedAccount;

    public EditAccountInfoDialog() {}

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mViewModel = ViewModelProviders.of(this).get(EditAccountInfoDialogViewModel.class);

        // Convert Jason data back to Product
        Type type = new TypeToken<Account>(){}.getType();
        String productFromJason = getArguments().getString(EXTRA_ACCOUNT);
        mAccount = new Gson().fromJson(productFromJason, type);

        mBinding = EditAccountInfoDialogBinding.inflate(getActivity().getLayoutInflater(), null);

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(mBinding.getRoot())
                .create();

        mBinding.inputTextAccountName.setText(mAccount.getAccountName());
        mBinding.inputTextAccountEmail.setText(mAccount.getAccountEmail());
        mBinding.inputTextAccountMobile.setText(mAccount.getAccountMobile());

        mBinding.textCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        mBinding.textOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditedAccount = mAccount;
                mEditedAccount.setAccountName(mBinding.inputTextAccountName.getText().toString());
                mEditedAccount.setAccountEmail(mBinding.inputTextAccountEmail.getText().toString());
                mEditedAccount.setAccountMobile(mBinding.inputTextAccountMobile.getText().toString());
                ConfirmDialog confirmDialog = ConfirmDialog.newInstance("Confirm account editing?");
                confirmDialog.setInteractionListener(new ConfirmDialog.InteractionListener() {
                    @Override
                    public void onOKAction() {
                        mViewModel.updateAccount(mEditedAccount);
                        dialog.dismiss();
                        confirmDialog.dismiss();
                    }

                    @Override
                    public void onCancelAction() {
                        confirmDialog.dismiss();
                    }
                });
                confirmDialog.show(getFragmentManager(),null);
            }
        });

        return dialog;
    }
}
