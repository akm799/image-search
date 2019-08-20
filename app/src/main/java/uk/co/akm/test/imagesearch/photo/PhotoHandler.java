package uk.co.akm.test.imagesearch.photo;

import android.app.Activity;

public interface PhotoHandler extends PhotoReader {

    void initiateImageCapture(Activity parent, String photoName);

    boolean imageCaptured(int requestCode, int resultCode);
}
