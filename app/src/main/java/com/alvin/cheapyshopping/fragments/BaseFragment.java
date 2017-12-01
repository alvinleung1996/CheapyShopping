package com.alvin.cheapyshopping.fragments;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.alvin.cheapyshopping.utils.PermissionHelper;

/**
 * Created by Alvin on 1/12/2017.
 */

public abstract class BaseFragment extends Fragment {

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionHelper.getsInstance(this.getContext()).onRequestPermissionResult(requestCode, permissions, grantResults);
    }
}
