package com.daveace.productcatalog.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.daveace.productcatalog.Fragment.AddFragment;
import com.daveace.productcatalog.Fragment.CatalogFragment;
import com.daveace.productcatalog.R;
import com.daveace.productcatalog.model.database.RealmDatabaseHelper;
import com.daveace.productcatalog.util.FragmentUtil;


public class MainActivity extends AppCompatActivity {

    RealmDatabaseHelper realmDatabaseHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        realmDatabaseHelper = RealmDatabaseHelper.getInstance();
        loadFragment();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realmDatabaseHelper.close();
    }

    private void loadFragment() {
        if (noProductFound()) {
            FragmentUtil.replaceFragment(getSupportFragmentManager(),
                    new AddFragment(), null, false);
        } else {
            FragmentUtil.replaceFragment(getSupportFragmentManager(),
                    new CatalogFragment(), null, false);
        }
    }

    private boolean noProductFound() {
        return realmDatabaseHelper.getProducts().isEmpty();
    }
}