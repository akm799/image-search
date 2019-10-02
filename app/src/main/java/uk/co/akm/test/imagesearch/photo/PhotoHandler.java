package uk.co.akm.test.imagesearch.photo;

import android.app.Activity;

public interface PhotoHandler extends PhotoIO {

    void initiateImageCapture(Activity parent, String photoName);

    boolean imageCaptured(int requestCode, int resultCode);
}
