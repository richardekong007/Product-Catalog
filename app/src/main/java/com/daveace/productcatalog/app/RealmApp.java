package com.daveace.productcatalog.app;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class RealmApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);

        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
                .name("product.realm")
                .schemaVersion(1)
                .build();
        Realm.setDefaultConfiguration(realmConfig);
    }
}
