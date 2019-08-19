package uk.co.akm.test.imagesearch.photo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;

public interface PhotoFunctions {

    void initiateImageCapture(Activity parent);

    boolean imageCaptured(int requestCode, int resultCode);

    Bitmap readCapturedImage(Context context);
}
