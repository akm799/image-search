package uk.co.akm.test.imagesearch.photo.impl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import uk.co.akm.test.imagesearch.photo.PhotoFunctions;

public final class FilePhotoFunctions implements PhotoFunctions  {
    private static final int REQUEST_CODE = 7257;

    private static final String IMAGE_FILE_NAME = "target";
    private static final String IMAGE_FILE_EXTENSION = ".jpg";
    private static final String IMAGE_FILE_PROVIDER_AUTHORITY = "uk.co.akm.test.imagesearch.fileprovider.photo";

    private static final String PHOTO_FILE_PATH_KEY = "photo.file.path_key";
    private static final String SHARED_PREFERENCES_FILE_KEY = "uk.co.akm.test.imagesearch.shared_preferences_storage_file";

    @Override
    public void initiateImageCapture(Activity parent) {
        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(parent.getPackageManager()) != null) {
            configureImageCaptureIntent(parent, intent);
            parent.startActivityForResult(intent, REQUEST_CODE);
        }
    }

    private void configureImageCaptureIntent(Context context, Intent intent) {
        final File photoFile = createImageFile(context);
        final Uri photoURI = FileProvider.getUriForFile(context, IMAGE_FILE_PROVIDER_AUTHORITY, photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

        writeStoredPhotoFilePath(context, photoFile.getAbsolutePath());
    }

    private void writeStoredPhotoFilePath(Context context, String photoFilePath) {
        final SharedPreferences prefs = sharedPreferences(context);
        prefs.edit().putString(PHOTO_FILE_PATH_KEY, photoFilePath).apply();
    }

    private File createImageFile(Context context) {
        final File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        try {
            return File.createTempFile(IMAGE_FILE_NAME, IMAGE_FILE_EXTENSION, storageDir);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    @Override
    public boolean imageCaptured(int requestCode, int resultCode) {
        return (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK);
    }

    @Override
    public Bitmap readCapturedImage(Context context, Intent data) {
        final Uri photoURI = buildStoredPhotoFileUri(context);

        try (InputStream is = context.getContentResolver().openInputStream(photoURI)) {
            return BitmapFactory.decodeStream(is);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    private Uri buildStoredPhotoFileUri(Context context) {
        final String photoFilePath = readStoredPhotoFilePath(context);
        final File photoFile = new File(photoFilePath);
        if (!photoFile.exists()) {
            throw new IllegalStateException("No photo file '" + photoFilePath + "' found.");
        }

        return FileProvider.getUriForFile(context, IMAGE_FILE_PROVIDER_AUTHORITY, photoFile);
    }

    private String readStoredPhotoFilePath(Context context) {
        final SharedPreferences prefs = sharedPreferences(context);
        final String photoFilePath = prefs.getString(PHOTO_FILE_PATH_KEY, null);
        if (photoFilePath == null) {
            throw new IllegalStateException("No photo file path stored with key: " + PHOTO_FILE_PATH_KEY);
        }

        return photoFilePath;
    }

    private SharedPreferences sharedPreferences(Context context) {
        return context.getSharedPreferences(SHARED_PREFERENCES_FILE_KEY, Context.MODE_PRIVATE);
    }
}
