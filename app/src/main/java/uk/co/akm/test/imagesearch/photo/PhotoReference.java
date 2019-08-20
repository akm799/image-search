package uk.co.akm.test.imagesearch.photo;

import android.content.Context;
import android.graphics.Bitmap;

public interface PhotoReference {

    Bitmap readCapturedImage(Context context, String photoName);
}
