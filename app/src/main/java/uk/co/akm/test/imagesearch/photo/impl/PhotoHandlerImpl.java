package uk.co.akm.test.imagesearch.photo.impl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import uk.co.akm.test.imagesearch.photo.PhotoHandler;
import uk.co.akm.test.imagesearch.photo.PhotoIO;

public final class PhotoHandlerImpl implements PhotoHandler {
    private static final int REQUEST_CODE = 7257;

    private final PhotoIO photoIO = new PhotoIOImpl();

    @Override
    public void initiateImageCapture(Activity parent, String photoName) {
        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(parent.getPackageManager()) != null) {
            configureImageCaptureIntent(parent, intent, photoName);
            parent.startActivityForResult(intent, REQUEST_CODE);
        }
    }

    private void configureImageCaptureIntent(Context context, Intent intent, String photoName) {
        final Uri photoURI = FilePhotoFunctions.createPhotoFileUriForStoring(context, photoName);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
    }

    @Override
    public boolean imageCaptured(int requestCode, int resultCode) {
        return (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK);
    }

    @Override
    public Bitmap readCapturedImage(Context context, String photoName) {
        return photoIO.readCapturedImage(context, photoName);
    }

    @Override
    public boolean deleteCapturedImage(Context context, String photoName) {
        return photoIO.deleteCapturedImage(context, photoName);
    }

    @Override
    public boolean writeImage(Context context, Bitmap bitmap, String photoName) {
        return photoIO.writeImage(context, bitmap, photoName);
    }
}
