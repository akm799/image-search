package uk.co.akm.test.imagesearch.process;

import android.graphics.Bitmap;

public interface ImageProcessor {
    String getDescription();

    Bitmap processImage(Bitmap image);
}
