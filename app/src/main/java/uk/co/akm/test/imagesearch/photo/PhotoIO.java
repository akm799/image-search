package uk.co.akm.test.imagesearch.photo;

import android.content.Context;
import android.graphics.Bitmap;

public interface PhotoIO {

    Bitmap readCapturedImage(Context context, String photoName);

    boolean deleteCapturedImage(Context context, String photoName);

    boolean writeImage(Context context, Bitmap bitmap, String photoName);
}
