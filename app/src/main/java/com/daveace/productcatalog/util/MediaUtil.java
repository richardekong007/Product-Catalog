package com.daveace.productcatalog.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.daveace.productcatalog.R;
import com.daveace.productcatalog.interfaces.Constant;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class MediaUtil {


    private static File createImageFile(Context context) throws IOException {
        String timeStamp = new SimpleDateFormat(Constant.FILE_NAME_FORMAT,
                Locale.getDefault())
                .format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File fileSystem = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        return File.createTempFile(imageFileName,
                Constant.IMAGE_FILE_TYPE,
                fileSystem);
    }

    public static String takePhoto(Fragment fragment) {
        Context context = fragment.getContext();
        Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String imageFilePath = null;
        if (captureImage.resolveActivity(Objects.requireNonNull(context)
                .getPackageManager()) != null) {

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
        File imageFile;
        try {
            imageFile = createImageFile(ctx);
            Uri uri = FileProvider.getUriForFile(ctx, ctx.getString(R.string.authority), imageFile);
            FileOutputStream fout = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fout);
            imageFilePath = imageFile.getAbsolutePath();
            fout.flush();
            fout.close();
        } catch (IOException ex) {
            Log.e("IO Error:", ex.getMessage());
        }
        return imageFilePath;
    }

    public static void displayImage(Context context, String imageFilePath, ImageView imageView) {
        File imageFile = new File(imageFilePath);
        if (imageFile.exists()) {
            Glide.with(context).load(imageFile).into(imageView);
        }
    }
}