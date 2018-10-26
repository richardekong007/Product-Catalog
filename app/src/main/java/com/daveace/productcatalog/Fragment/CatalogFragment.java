package com.daveace.productcatalog.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daveace.productcatalog.Adapter.CatalogAdapter;
import com.daveace.productcatalog.R;
import com.daveace.productcatalog.interfaces.CatalogItemLongClickListener;
import com.daveace.productcatalog.interfaces.ScanListener;
import com.daveace.productcatalog.model.Product;
import com.daveace.productcatalog.model.database.RealmDatabaseHelper;
import com.daveace.productcatalog.util.FragmentUtil;
import com.daveace.productcatalog.util.ScanCodeUtil;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.daveace.productcatalog.interfaces.Constant.UPDATE_PRODUCT_CODE_PATH;
import static com.daveace.productcatalog.interfaces.Constant.UPDATE_PRODUCT_ID;
import static com.daveace.productcatalog.interfaces.Constant.UPDATE_PRODUCT_IMAGE_PATH;
import static com.daveace.productcatalog.interfaces.Constant.UPDATE_PRODUCT_NAME;

public class CatalogFragment extends Fragment implements CatalogItemLongClickListener, ScanListener {


    @BindView(R.id.products)
    RecyclerView recyclerView;

    private static final int SPAN_COUNT = 2;

    private List<Product> products;

    private BarcodeDetector barcodeDetector;

    private RealmDatabaseHelper realmDatabaseHelper;

    private CatalogAdapter catalogAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View catalogView = inflater.inflate(R.layout.fragment_catalog, container, false);
        ButterKnife.bind(this, catalogView);
        setHasOptionsMenu(true);
        realmDatabaseHelper = RealmDatabaseHelper.getInstance();
        barcodeDetector = new BarcodeDetector.Builder(getActivity())
                .setBarcodeFormats(Barcode.QR_CODE | Barcode.UPC_E)
                .build();
        products = realmDatabaseHelper.getProducts();
        setupCatalogProductAdaptor();
        return catalogView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_add_action, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.add_menu_item) {
            FragmentUtil.replaceFragment(Objects.requireNonNull(getActivity()).getSupportFragmentManager(),
                    new AddFragment(), null, false);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setOnItemLongClick(Product product, View view) {
        showPopupMenu(product, view);
    }

    @Override
    public void onScanClick(ImageView imageView, TextView textView) {
        String scanedValue = ScanCodeUtil.scan(barcodeDetector, imageView);
        textView.setText(scanedValue);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realmDatabaseHelper.close();
    }

    private void setupCatalogProductAdaptor() {

        catalogAdapter = new CatalogAdapter(products);
        catalogAdapter.setScanListener(this);
        catalogAdapter.setLongClickListener(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), SPAN_COUNT);
        recyclerView.setAdapter(catalogAdapter);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
    }

    private void showPopupMenu(Product product, View view) {

        PopupMenu popupMenu = new PopupMenu(Objects.requireNonNull(getContext()), view);
        popupMenu.inflate(R.menu.menu_catalog_action);
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.edit_item:
                    //show UpdateDialog
                    Bundle updateBundle = new Bundle();
                    updateBundle.putInt(UPDATE_PRODUCT_ID, product.getId());
                    updateBundle.putString(UPDATE_PRODUCT_NAME, product.getName());
                    updateBundle.putString(UPDATE_PRODUCT_IMAGE_PATH, product.getImageUrl());
                    updateBundle.putString(UPDATE_PRODUCT_CODE_PATH, product.getCodeUrl());
                    FragmentUtil.replaceFragment(Objects.requireNonNull(getActivity()).getSupportFragmentManager(),
                            new UpdateProductFragment(), updateBundle, false);

                    break;
                case R.id.delete_item:
                    //delete product with productId
                    realmDatabaseHelper.deleteProduct(product.getId(), getActivity());
                    catalogAdapter.notifyDataSetChanged();
                    break;
            }
            return false;
        });
        popupMenu.show();
    }
}