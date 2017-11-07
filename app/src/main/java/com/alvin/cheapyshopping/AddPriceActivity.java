package com.alvin.cheapyshopping;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class AddPriceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_price);

        if (savedInstanceState == null) {
            this.getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, SelectStoreFragment.newInstance())
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (this.getSupportFragmentManager().getBackStackEntryCount() > 0) {
            this.getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}
