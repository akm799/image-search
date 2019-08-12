package uk.co.akm.test.imagesearch.photo.impl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;

import java.io.IOException;
import java.io.InputStream;

import uk.co.akm.test.imagesearch.photo.PhotoFunctions;

public final class PhotoFunctionsImpl implements PhotoFunctions  {
    private static final int REQUEST_CODE = 7259;

    @Override
    public void initiateImageCapture(Activity parent) {
        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(parent.getPackageManager()) != null) {
            parent.startActivityForResult(intent, REQUEST_CODE);
        }
    }

    @Override
    public boolean imageCaptured(int requestCode, int resultCode) {
        return (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK);
    }

    @Override
    public Bitmap readCapturedImage(Context context, Intent data) {
        return (Bitmap)data.getExtras().get("data");
    }
}
