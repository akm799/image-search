package uk.co.akm.test.imagesearch.photo.impl;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import uk.co.akm.test.imagesearch.store.Store;


class FilePhotoFunctions {
    private static final String TAG = "FilePhotoFunctions";

    private static final String IMAGE_FILE_EXTENSION = ".jpg";
    private static final String IMAGE_FILE_PROVIDER_AUTHORITY = "uk.co.akm.test.imagesearch.fileprovider.photo";

    private static final String PHOTO_FILE_PATH_KEY = "photo.file.path_key:";

    static final Bitmap.CompressFormat COMPRESS_FORMAT = Bitmap.CompressFormat.JPEG;

    static Uri createPhotoFileUriForStoring(Context context, String photoName) {
        final File photoFile = createImageFile(context, photoName);
        final Uri photoURI = FileProvider.getUriForFile(context, IMAGE_FILE_PROVIDER_AUTHORITY, photoFile);
        writeStoredPhotoFilePath(context, photoName, photoFile.getAbsolutePath());

        return photoURI;
    }

    private static File createImageFile(Context context, String photoName) {
        final File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        try {
            return File.createTempFile(photoName, IMAGE_FILE_EXTENSION, storageDir);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    private static void writeStoredPhotoFilePath(Context context, String photoName, String photoFilePath) {
        Store.put(context, buildPhotoKey(photoName), photoFilePath);
    }

    static Uri buildStoredPhotoFileUri(Context context, String photoName) {
        final String photoFilePath = readStoredPhotoFilePath(context, photoName);
        if (photoFilePath == null) {
            return null;
        }

        final File photoFile = new File(photoFilePath);
        if (!photoFile.exists()) {
            Log.e(TAG, "No photo file '" + photoFilePath + "' found.");
            return null;
        }

        return FileProvider.getUriForFile(context, IMAGE_FILE_PROVIDER_AUTHORITY, photoFile);
    }

    static boolean deleteStoredPhotoFile(Context context, String photoName) {
        final String photoFilePath = readStoredPhotoFilePath(context, photoName);
        if (photoFilePath == null) {
            return false;
        }

        final File photoFile = new File(photoFilePath);
        if (!photoFile.exists()) {
            Log.e(TAG, "No photo file '" + photoFilePath + "' found to be deleted.");
            return false;
        }

        if (photoFile.delete()) {
            deleteStoredPhotoFilePath(context, photoName);
            return true;
        } else {
            return false;
        }
    }

    private static String readStoredPhotoFilePath(Context context, String photoName) {
        final String photoKey = buildPhotoKey(photoName);
        final String photoFilePath = Store.get(context, photoKey);
        if (photoFilePath == null) {
            Log.e(TAG, "No photo file path stored for photo with name " + photoName + " under key: " + photoKey);
        }

        return photoFilePath;
    }

    private static void deleteStoredPhotoFilePath(Context context, String photoName) {
        final String photoKey = buildPhotoKey(photoName);
        Store.remove(context, photoKey);
    }

    private static String buildPhotoKey(String photoName) {
        return (PHOTO_FILE_PATH_KEY + photoName);
    }

    private FilePhotoFunctions() {}
}
