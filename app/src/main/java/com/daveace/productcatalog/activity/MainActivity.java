package com.daveace.productcatalog.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.daveace.productcatalog.Fragment.AddFragment;
import com.daveace.productcatalog.Fragment.CatalogFragment;
import com.daveace.productcatalog.R;
import com.daveace.productcatalog.model.database.RealmDatabaseHelper;
import com.daveace.productcatalog.util.FragmentUtil;

import io.realm.Realm;


public class MainActivity extends AppCompatActivity {

    private Realm realmDb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        realmDb = Realm.getDefaultInstance();
        loadFragment();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realmDb != null) {
            realmDb.close();
        }
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
        RealmDatabaseHelper helper = new RealmDatabaseHelper(realmDb);
        return helper.getProducts().isEmpty();
    }
}