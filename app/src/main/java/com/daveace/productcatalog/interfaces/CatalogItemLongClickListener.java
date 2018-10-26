package com.daveace.productcatalog.interfaces;

import android.view.View;

import com.daveace.productcatalog.model.Product;

public interface CatalogItemLongClickListener {
    void setOnItemLongClick(Product product, View view);
}
