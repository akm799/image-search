package uk.co.akm.test.imagesearch.photo.impl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;

import uk.co.akm.test.imagesearch.photo.PhotoFunctions;

public final class PhotoFunctionsImpl implements PhotoFunctions  {
    private static final int REQUEST_CODE = 7259;

    @Override
    public void initiateImageCapture(Activity parent) {
        final Intent intent = buildImageCaptureIntent();
        parent.startActivityForResult(intent, REQUEST_CODE);
    }

    private Intent buildImageCaptureIntent() {
        final Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        return intent;
    }

    @Override
    public boolean imageCaptured(int requestCode, int resultCode) {
        return (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK);
    }

    @Override
    public Bitmap readCapturedImage(Context context, Intent data) {
        try (InputStream is = context.getContentResolver().openInputStream(data.getData())) {
            return BitmapFactory.decodeStream(is);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }
}
