package uk.co.akm.test.imagesearch.photo;

import android.content.Context;
import android.graphics.Bitmap;

public interface PhotoReader {

    Bitmap readCapturedImage(Context context, String photoName);
}