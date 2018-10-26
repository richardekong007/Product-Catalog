package com.daveace.productcatalog.model.database;

import android.content.Context;
import android.widget.Toast;

import com.daveace.productcatalog.R;
import com.daveace.productcatalog.model.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmResults;

public class RealmDatabaseHelper {

    private Realm realmInstance;


    public RealmDatabaseHelper(Realm realmInstance) {
        this.realmInstance = realmInstance;
    }

    public void addProduct(Product product, Context ctx) {
        if (product != null) {
            realmInstance.executeTransactionAsync(realm -> {
                        Number index = realm.where(Product.class).max("id");
                        int nextIndex = (index == null) ? 1 : index.intValue() + 1;
                        product.setId(nextIndex);
                        realm.insert(product);
                    }, () -> Toast.makeText(ctx, "Added successfully", Toast.LENGTH_LONG).show()
                    , error -> Toast.makeText(ctx, error.getMessage(), Toast.LENGTH_LONG).show());
        }
    }

    public List<Product> getProducts() {

        RealmResults<Product> productRealmResults = realmInstance.where(Product.class).findAllAsync();
        return new ArrayList<>(productRealmResults);
    }

    public void updateProduct(Product product, Context ctx) {
        realmInstance.executeTransactionAsync(realm -> {
                    if (product != null) {
                        realm.insertOrUpdate(product);
                    }
                }, () -> Toast.makeText(ctx, ctx.getString(R.string.update_success_message), Toast.LENGTH_LONG).show()
                , error -> Toast.makeText(ctx, error.getMessage(), Toast.LENGTH_LONG).show());
    }


    public void deleteProduct(int id, Context ctx) {
        final RealmResults<Product> products = realmInstance
                .where(Product.class)
                .findAll();
        realmInstance.executeTransactionAsync(realm -> {
                    Product product = products.get(id);
                    Objects.requireNonNull(product).deleteFromRealm();
                }, () -> Toast.makeText(ctx, R.string.product_deletion_message, Toast.LENGTH_LONG).show(),
                error -> Toast.makeText(ctx, error.getMessage(), Toast.LENGTH_LONG).show());
    }

    public void close() {
        if (realmInstance != null) {
            realmInstance.close();
        }
    }
}