package com.alvin.cheapyshopping;

import android.arch.core.util.Function;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.alvin.cheapyshopping.databinding.AddShoppingListActivityBinding;
import com.alvin.cheapyshopping.room.entities.ShoppingList;
import com.alvin.cheapyshopping.viewmodels.AddShoppingListActivityViewModel;
import com.alvin.cheapyshopping.viewmodels.AddShoppingListProductActivityViewModel;

public class AddShoppingListActivity extends AppCompatActivity {

    public static final String EXTRA_SHOPPING_LIST_ID = "com.alvin.cheapyshopping.AddShoppingListActivity.EXTRA_SHOPPING_LIST_ID";

    private AddShoppingListActivityViewModel mViewModel;

    private AddShoppingListActivityBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.mViewModel = ViewModelProviders.of(this).get(AddShoppingListActivityViewModel.class);

        this.mBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_shopping_list);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.add_discard_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_add:
                this.saveInput();
                return true;
            case R.id.item_discard:
                this.finishActivityWithShoppingListId(null);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveInput() {
        String name = this.mBinding.inputName.getEditText().getText().toString();

        boolean valid = true;
        if (name.isEmpty()) {
            this.mBinding.inputName.setError("enter a list plz");
            valid = false;
        }

        if (!valid) return;

        this.mViewModel.addShoppingList(name, new Function<long[], Void>() {
            @Override
            public Void apply(long[] shoppingListsIds) {
                AddShoppingListActivity.this.finishActivityWithShoppingListId(shoppingListsIds[0]);
                return null;
            }
        });
    }

    private void finishActivityWithShoppingListId(Long shoppingListId) {
        if (shoppingListId != null) {
            Intent intent = new Intent();
            intent.putExtra(EXTRA_SHOPPING_LIST_ID, shoppingListId);
            this.setResult(RESULT_OK, intent);
        }
        this.finish();
    }
}
