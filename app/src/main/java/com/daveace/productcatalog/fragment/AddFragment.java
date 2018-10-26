package com.daveace.productcatalog.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.daveace.productcatalog.R;
import com.daveace.productcatalog.interfaces.Constant;
import com.daveace.productcatalog.model.Product;
import com.daveace.productcatalog.model.database.RealmDatabaseHelper;
import com.daveace.productcatalog.util.FragmentUtil;
import com.daveace.productcatalog.util.MediaUtil;
import com.daveace.productcatalog.util.ScanCodeUtil;
import com.google.zxing.BarcodeFormat;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_CANCELED;

public class AddFragment extends Fragment {

    @BindView(R.id.product_image)
    ImageView productImageView;

    @BindView(R.id.code_image)
    ImageView codeImageView;

    @BindView(R.id.product_name)
    EditText productEditText;

    @BindView(R.id.code_type)
    Spinner codeSpinner;

    @BindView(R.id.bind_button)
    Button bindButton;

    @BindView(R.id.add)
    FloatingActionButton addActionButton;

    @BindView(R.id.capture)
    FloatingActionButton captureButton;

    private String capturedImagePath;

    private String codeImagePath;

    private RealmDatabaseHelper realmDatabaseHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);
        ButterKnife.bind(this, view);
        realmDatabaseHelper = RealmDatabaseHelper.getInstance();
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_catalog, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.catalog:
                FragmentUtil.replaceFragment(Objects.requireNonNull(getActivity()).getSupportFragmentManager(),
                        new CatalogFragment(), null, true);
        }
        return super.onOptionsItemSelected(item);

    }

    @OnClick(R.id.capture)
    public void onCaptureButtonClick() {
        capturedImagePath = MediaUtil.takePhoto(this);
    }

    @OnClick(R.id.bind_button)
    public void onBindCodeClick() {
        Bitmap bitmap;
        String selectedItem = codeSpinner.getSelectedItem().toString();
        if (selectedItem.equals(Constant.BARCODE)) {
            bitmap = ScanCodeUtil.bindCode(codeImageView.getHeight(), codeImageView.getWidth(),
                    BarcodeFormat.UPC_E, codeImageView, getActivity());
        } else {
            bitmap = ScanCodeUtil.bindCode(codeImageView.getHeight(), codeImageView.getWidth(),
                    BarcodeFormat.QR_CODE, codeImageView, getActivity());
        }
        codeImagePath = MediaUtil.saveImage(bitmap, getActivity());
    }

    @OnClick(R.id.add)
    public void onAddClick() {
        Product product;
        if ((productEditText.getText() != null) && (capturedImagePath != null)) {
            product = new Product();
            product.setName(productEditText.getText().toString().trim());
            product.setImageUrl(capturedImagePath);
            product.setCodeUrl(codeImagePath);
            realmDatabaseHelper.addProduct(product, getActivity());
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constant.REQUEST_IMAGE_CAPTURE) {

            MediaUtil.displayImage(getActivity(), capturedImagePath, productImageView);

        } else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(getActivity(), getString(R.string.camera_cancel), Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        realmDatabaseHelper.close();
    }
}