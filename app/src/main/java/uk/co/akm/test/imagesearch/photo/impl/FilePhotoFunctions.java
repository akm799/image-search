package uk.co.akm.test.imagesearch.photo.impl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import uk.co.akm.test.imagesearch.photo.PhotoFunctions;
import uk.co.akm.test.imagesearch.photo.PhotoReference;

public final class FilePhotoFunctions implements PhotoFunctions  {
    private static final int REQUEST_CODE = 7257;

    private final PhotoReference photoReference = new ParcelablePhotoReference();

    @Override
    public void initiateImageCapture(Activity parent, String photoName) {
        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(parent.getPackageManager()) != null) {
            configureImageCaptureIntent(parent, intent, photoName);
            parent.startActivityForResult(intent, REQUEST_CODE);
        }
    }

    private void configureImageCaptureIntent(Context context, Intent intent, String photoName) {
        final Uri photoURI = FilePhotoHelper.createPhotoFileUriForStoring(context, photoName);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
    }

    @Override
    public boolean imageCaptured(int requestCode, int resultCode) {
        return (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK);
    }

    @Override
    public Bitmap readCapturedImage(Context context, String photoName) {
        return photoReference.readCapturedImage(context, photoName);
    }
}
