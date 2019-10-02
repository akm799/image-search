package uk.co.akm.test.imagesearch.photo.impl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import uk.co.akm.test.imagesearch.photo.PhotoReader;

public final class PhotoReaderImpl implements PhotoReader {

    @Override
    public Bitmap readCapturedImage(Context context, String photoName) {
        final Uri photoURI = FilePhotoFunctions.buildStoredPhotoFileUri(context, photoName);
        if (photoURI == null) {
            return null;
        }

        try (InputStream is = context.getContentResolver().openInputStream(photoURI)) {
            return BitmapFactory.decodeStream(is);
        } catch (IOException ioe) {
            Log.e(getClass().getName(), "I/O error when reading image from photo file URI: " + photoURI, ioe);
            return null;
        }
    }

    @Override
    public boolean deleteCapturedImage(Context context, String photoName) {
        return FilePhotoFunctions.deleteStoredPhotoFile(context, photoName);
    }

    public boolean writeImage(Context context, Bitmap bitmap, String photoName) {
        final Uri photoURI = FilePhotoFunctions.createPhotoFileUriForStoring(context, photoName);

        try (OutputStream os = context.getContentResolver().openOutputStream(photoURI)) {
            return bitmap.compress(FilePhotoFunctions.COMPRESS_FORMAT, 100, os);
        } catch (IOException ioe) {
            Log.e(getClass().getName(), "I/O error when writing image to photo file with URI: " + photoURI, ioe);
            deleteEmptyPhotoFileForName(context, photoName);

            return false;
        }
    }

    private void deleteEmptyPhotoFileForName(Context context, String photoName) {
        try {
            FilePhotoFunctions.deleteStoredPhotoFile(context, photoName);
        } catch (Exception e) {
            Log.e(getClass().getName(), "Error when deleting empty photo file for photo-name: " + photoName, e);
        }
    }
}
