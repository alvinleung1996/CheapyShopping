package com.alvin.cheapyshopping;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import com.alvin.cheapyshopping.utils.LocationManager;

/**
 * Created by Alvin on 29/11/2017.
 */

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case LocationManager.REQUEST_LOCATION_SETTING:
                LocationManager.getInstance(this).onLocationRequestResult(requestCode, resultCode, data);
                break;
        }
    }
}
