package uk.co.akm.test.imagesearch.photo.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;
import java.io.IOException;


public class FilePhotoFunctions {
    private static final String TAG = "FilePhotoFunctions";

    private static final String IMAGE_FILE_EXTENSION = ".jpg";
    private static final String IMAGE_FILE_PROVIDER_AUTHORITY = "uk.co.akm.test.imagesearch.fileprovider.photo";

    private static final String PHOTO_FILE_PATH_KEY = "photo.file.path_key:";
    private static final String SHARED_PREFERENCES_FILE_KEY = "uk.co.akm.test.imagesearch.shared_preferences_storage_file";

    public static Uri createPhotoFileUriForStoring(Context context, String photoName) {
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
        final SharedPreferences prefs = sharedPreferences(context);
        prefs.edit().putString(buildPhotoKey(photoName), photoFilePath).apply();
    }

    public static Uri buildStoredPhotoFileUri(Context context, String photoName) {
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

    private static String readStoredPhotoFilePath(Context context, String photoName) {
        final String photoKey = buildPhotoKey(photoName);
        final SharedPreferences prefs = sharedPreferences(context);
        final String photoFilePath = prefs.getString(photoKey, null);
        if (photoFilePath == null) {
            Log.e(TAG, "No photo file path stored for photo with name " + photoName + " under key: " + photoKey);
        }

        return photoFilePath;
    }

    private static String buildPhotoKey(String photoName) {
        return (PHOTO_FILE_PATH_KEY + photoName);
    }

    private static SharedPreferences sharedPreferences(Context context) {
        return context.getSharedPreferences(SHARED_PREFERENCES_FILE_KEY, Context.MODE_PRIVATE);
    }

    private FilePhotoFunctions() {}
}
