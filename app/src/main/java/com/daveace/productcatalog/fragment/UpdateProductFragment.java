package com.daveace.productcatalog.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
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

import butterknife.ButterKnife;

import static android.app.Activity.RESULT_CANCELED;
import static com.daveace.productcatalog.interfaces.Constant.QRCODE;
import static com.daveace.productcatalog.interfaces.Constant.UPDATE_PRODUCT_CODE_PATH;
import static com.daveace.productcatalog.interfaces.Constant.UPDATE_PRODUCT_ID;
import static com.daveace.productcatalog.interfaces.Constant.UPDATE_PRODUCT_IMAGE_PATH;
import static com.daveace.productcatalog.interfaces.Constant.UPDATE_PRODUCT_NAME;

public class UpdateProductFragment extends Fragment {

    private ImageView codeImageView;

    private ImageView productImageView;

    private Spinner codeTypeSpinner;

    private String productImagePath;

    private EditText productNameEditText;

    private String captureImagePath;

    private String codeImagePath;

    private String productName;

    private int productId;

    private RealmDatabaseHelper realmDatabaseHelper;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_update_product, container, false);
        ButterKnife.bind(view, Objects.requireNonNull(getActivity()));
        initView(view);
        realmDatabaseHelper = RealmDatabaseHelper.getInstance();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realmDatabaseHelper.close();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Constant.REQUEST_IMAGE_CAPTURE) {
            MediaUtil.displayImage(getActivity(), captureImagePath, productImageView);
        } else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(getActivity(), getString(R.string.camera_cancel), Toast.LENGTH_LONG).show();
        }
    }

    private void initView(View view) {

        productImageView = view.findViewById(R.id.product_image);
        codeImageView = view.findViewById(R.id.code_image);
        codeTypeSpinner = view.findViewById(R.id.code_type);
        Button bindButton = view.findViewById(R.id.bind_button);
        Button updateButton = view.findViewById(R.id.update);
        FloatingActionButton captureButton = view.findViewById(R.id.capture);
        productNameEditText = view.findViewById(R.id.product_name);

        bindButton.setOnClickListener(theView -> bindCode());
        captureButton.setOnClickListener(theView -> captureImage());
        updateButton.setOnClickListener(theView -> update());

        if (getArguments() != null) {

            Bundle bundle = getArguments();
            productId = bundle.getInt(UPDATE_PRODUCT_ID);
            productName = bundle.getString(UPDATE_PRODUCT_NAME);
            productImagePath = bundle.getString(UPDATE_PRODUCT_IMAGE_PATH);
            codeImagePath = bundle.getString(UPDATE_PRODUCT_CODE_PATH);
            MediaUtil.displayImage(getActivity(), productImagePath, productImageView);
            MediaUtil.displayImage(getActivity(), codeImagePath, codeImageView);
            productNameEditText.setText(productName);
        }
    }

    public void bindCode() {

        String selectedItem = codeTypeSpinner.getSelectedItem().toString();
        Bitmap bitmap;
        if (selectedItem.equals(QRCODE)) {
            bitmap = ScanCodeUtil.bindCode(codeImageView.getHeight(), codeImageView.getWidth(),
                    BarcodeFormat.QR_CODE, codeImageView, getActivity());
        } else {
            bitmap = ScanCodeUtil.bindCode(codeImageView.getHeight(), codeImageView.getWidth(),
                    BarcodeFormat.UPC_E, codeImageView, getActivity());
        }
        codeImagePath = MediaUtil.saveImage(bitmap, getActivity());
    }

    public void update() {
        Product product = new Product();
        productName = productNameEditText.getText().toString();
        product.setId(productId);
        product.setName(productName);
        product.setImageUrl(captureImagePath != null ? captureImagePath : productImagePath);
        product.setCodeUrl(codeImagePath);
        realmDatabaseHelper.updateProduct(product, getActivity());
        FragmentUtil.replaceFragment(Objects.requireNonNull(getActivity()).getSupportFragmentManager(),
                new CatalogFragment(), null, false);
    }

    public void captureImage() {
        captureImagePath = MediaUtil.takePhoto(this);
    }
}
