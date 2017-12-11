package com.alvin.cheapyshopping.fragments.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.alvin.cheapyshopping.databinding.RankUpgradeDialogBinding;

/**
 * Created by cheng on 12/9/2017.
 */

public class RankUpgradeDialog extends DialogFragment {
    public static final String EXTRA_RANK_NEW = "com.alvin.cheapyshopping.RankUpgradeDialog.EXTRA_RANK_NEW";

    public static RankUpgradeDialog newInstance(int rank) {
        RankUpgradeDialog dialogFragment = new RankUpgradeDialog();
        Bundle args = new Bundle();
        args.putInt(EXTRA_RANK_NEW, rank);
        dialogFragment.setArguments(args);

        return dialogFragment;
    }

    private RankUpgradeDialogBinding mBinding;

    private int mNewRank;

    public RankUpgradeDialog(){}

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mNewRank = getArguments().getInt(EXTRA_RANK_NEW);

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(mBinding.getRoot())
                .create();

        String textRank = "Rank " + mNewRank;
        mBinding.textRank.setText(textRank);

        return dialog;
    }
}
