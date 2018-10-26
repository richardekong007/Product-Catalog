package com.daveace.productcatalog.interfaces;


import android.os.Build;

public interface Constant {

    int BUILD_VERSION = Build.VERSION.SDK_INT;

    int MASHMALLOW = Build.VERSION_CODES.M;

    int KITKAT = Build.VERSION_CODES.KITKAT;

    int HONEYCOMB = Build.VERSION_CODES.HONEYCOMB;

    int REQUEST_IMAGE_CAPTURE = 101;

    int PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 102;

    int MIN = 10000000;

    int MAX = 99999999;


    String IMAGE_FILE_TYPE = ".jpg";

    String FILE_NAME_FORMAT = "yyyyMMdd_HHmmss";

    String CHARSET = "UTF-8";

    String BARCODE = "Barcode";

    String QRCODE = "QR-Code";

    String CODE_NOT_FOUND = "Not found";

    String UPDATE_PRODUCT_ID = "UPDATE_PRODUCT_ID";

    String UPDATE_PRODUCT_IMAGE_PATH = "UPDATE_PRODUCT_IMAGE_PATH";

    String UPDATE_PRODUCT_CODE_PATH = "UPDATE_PRODUCT_CODE_PATH";

    String UPDATE_PRODUCT_NAME = "UPDATE_PRODUCT_NAME";
}