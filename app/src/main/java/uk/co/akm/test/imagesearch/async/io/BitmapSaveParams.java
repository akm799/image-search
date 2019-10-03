package uk.co.akm.test.imagesearch.async.io;

import android.graphics.Bitmap;

final class BitmapSaveParams {
    final Bitmap bitmap;
    final String photoName;

    BitmapSaveParams(Bitmap bitmap, String photoName) {
        this.bitmap = bitmap;
        this.photoName = photoName;
    }
}
