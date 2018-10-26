package com.daveace.productcatalog.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.util.SparseArray;
import android.widget.ImageView;

import com.daveace.productcatalog.R;
import com.daveace.productcatalog.interfaces.Constant;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.HashMap;
import java.util.Map;

import static com.daveace.productcatalog.interfaces.Constant.CHARSET;
import static com.daveace.productcatalog.interfaces.Constant.MAX;
import static com.daveace.productcatalog.interfaces.Constant.MIN;

public class ScanCodeUtil {

    public static Bitmap bindCode(int height, int width, BarcodeFormat format, ImageView imageView, Context ctx) {
        Bitmap bitmap;
        switch (format) {
            case QR_CODE:
                bitmap = createQRCode(height, width, ctx);
                break;
            default:
                bitmap = createBarcode(height, width, ctx);
                break;
        }
        imageView.setImageBitmap(bitmap);
        return bitmap;
    }

    public static String scan(BarcodeDetector detector, ImageView imageView) {

        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
        SparseArray<Barcode> barcodes = detector.detect(frame);
        Barcode barcode;
        String code;
        try {
            barcode = barcodes.valueAt(0);
            code = barcode.displayValue;
        } catch (Exception ex) {
            Log.e("Scan Error", ex.getMessage());
            code = Constant.CODE_NOT_FOUND;
        }
        return code;
    }

    private static Bitmap createQRCode(int qrHeight, int qrWidth, Context ctx) {
        Bitmap bitmap = null;
        //setting size of qr code
        int smallestDimension = qrWidth < qrHeight ? qrWidth : qrHeight;
        String qrData = generateCodes(MIN, MAX);
        Map<EncodeHintType, ErrorCorrectionLevel> hintMap = new HashMap<>();
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        try {
            //generate the qrcode using the Bit matrix
            BitMatrix matrix = new MultiFormatWriter().encode(new String(qrData.getBytes(CHARSET), CHARSET),
                    BarcodeFormat.QR_CODE, smallestDimension, smallestDimension, hintMap);
            //convert the bit matrix to bitmap
            bitmap = convertToBitmap(matrix, ctx);
        } catch (Exception ex) {
            Log.e("Qr generation Exception", ex.getMessage());
        }
        return bitmap;
    }

    private static Bitmap createBarcode(int barcodeHeight, int barcodeWidth, Context ctx) {
        Bitmap bitmap = null;
        String code = generateCodes(MIN, MAX);
        try {

            Map<EncodeHintType, ErrorCorrectionLevel> hintMap = new HashMap<>();
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            BitMatrix matrix = new MultiFormatWriter().encode(new String(code.getBytes(CHARSET), CHARSET),
                    BarcodeFormat.UPC_E, barcodeWidth, barcodeHeight, hintMap);
            //covert the bit matrix to bitmap
            bitmap = convertToBitmap(matrix, ctx);
        } catch (Exception ex) {
            Log.e("Barcode gen exception", ex.getMessage());
        }

        return bitmap;
    }

    private static Bitmap convertToBitmap(BitMatrix matrix, Context ctx) {

        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = matrix.get(x, y) ? ResourcesCompat.getColor(ctx.getResources(), R.color.black, null) : -1;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    private static String generateCodes(int min, int max) {
        return String.valueOf((int) ((Math.random() * ((max - min) + 1) + min)));
    }
}