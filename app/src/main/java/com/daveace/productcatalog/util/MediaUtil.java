package com.daveace.productcatalog.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.daveace.productcatalog.R;
import com.daveace.productcatalog.interfaces.Constant;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.daveace.productcatalog.interfaces.Constant.BUILD_VERSION;
import static com.daveace.productcatalog.interfaces.Constant.HONEYCOMB;
import static com.daveace.productcatalog.interfaces.Constant.KITKAT;
import static com.daveace.productcatalog.interfaces.Constant.MASHMALLOW;

public class MediaUtil {

    public static String resolveMediaPath(Context context, Uri uri) {

        String filePath = null;
        if (isPermissionGranted(context)) {

            if (BUILD_VERSION >= KITKAT) {

                filePath = getPathForVersionsAboveKitkat(context, uri, filePath);
            } else if (BUILD_VERSION >= HONEYCOMB && BUILD_VERSION < KITKAT) {

                filePath = getPathHoneyCombToJellyBean(context, uri, filePath);

            } else if (BUILD_VERSION < HONEYCOMB) {

                filePath = getPathBelowHoneyComb(context, uri);
            }
        }
        return filePath;
    }

    private static boolean isPermissionGranted(Context context) {
        if (BUILD_VERSION >= MASHMALLOW) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (permissionRequestIsDisplayed((Activity) context)) {
                    showDialog(context, Manifest.permission.READ_EXTERNAL_STORAGE);

                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            Constant.PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }

    private static String getPathForVersionsAboveKitkat(Context context, Uri uri, String filePath) {
        String fullDocumentId = DocumentsContract.getDocumentId(uri);
        String partDocId = fullDocumentId.split(":")[1];
        String[] column = {MediaStore.Images.Media.DATA};
        String selection = MediaStore.Images.Media._ID + "=?";
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, selection, new String[]{partDocId}, null);
        int columnIndex = cursor.getColumnIndex(column[0]);
        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        return filePath;
    }

    private static String getPathHoneyCombToJellyBean(Context context, Uri uri, String filePath) {
        String[] projection = {MediaStore.Images.Media.DATA};
        CursorLoader cursorLoader = new CursorLoader(context, uri, projection, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            filePath = cursor.getString(columnIndex);
        }
        return filePath;
    }

    private static String getPathBelowHoneyComb(Context context, Uri uri) {
        String filePath;
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection,
                null, null, null);
        int columnIndex = cursor.getColumnIndexOrThrow(projection[0]);
        cursor.moveToFirst();
        filePath = cursor.getString(columnIndex);
        return filePath;
    }

    private static void showDialog(Context context, String readExternalStoragePermission) {

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage("External storage " + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                (dialog, which) ->
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[]{readExternalStoragePermission},
                                Constant.PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE));
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    private static File createImageFile(Context context) throws IOException {
        String timeStamp = new SimpleDateFormat(Constant.FILE_NAME_FORMAT, Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File fileSystem = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        return File.createTempFile(imageFileName,
                Constant.IMAGE_FILE_TYPE,
                fileSystem);
    }

    private static boolean permissionRequestIsDisplayed(Activity context) {
        return ActivityCompat.shouldShowRequestPermissionRationale(context,
                Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    public static String takePhoto(Fragment fragment) {
        Context context = fragment.getContext();
        Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String imageFilePath = null;
        if (captureImage.resolveActivity(context.getPackageManager()) != null) {

            File imageFile = null;
            try {
                imageFile = createImageFile(context);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (imageFile != null) {
                Uri imageUri = FileProvider.getUriForFile(context, context.getString(R.string.authority), imageFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                imageFilePath = imageFile.getAbsolutePath();
                fragment.startActivityForResult(captureImage, Constant.REQUEST_IMAGE_CAPTURE);

            }
        }
        return imageFilePath;
    }

    public static String saveImage(Bitmap bitmap, Context ctx) {
        String imageFilePath = "";
        File imageFile = null;
        try {
            imageFile = createImageFile(ctx);
            if (imageFile != null) {
                Uri uri = FileProvider.getUriForFile(ctx, ctx.getString(R.string.authority), imageFile);
                FileOutputStream fout = new FileOutputStream(imageFile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fout);
                imageFilePath = imageFile.getAbsolutePath();
                fout.flush();
                fout.close();
            }
        } catch (IOException ex) {
        }
        return imageFilePath;
    }

    public static void displayImage(Context context, String imageFilePath, ImageView imageView) {
        File imageFile = new File(imageFilePath);
        if (imageFile.exists()) {
            Glide.with(context).load(imageFile).into(imageView);
        }
    }

    private static Bitmap getBitmapFromUri(Context context, Uri imageUri) {
        //must be executed in background thread
        Bitmap bitmap = null;
        ParcelFileDescriptor parcelFileDescriptor;
        try {
            parcelFileDescriptor = context.getContentResolver().openFileDescriptor(imageUri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}