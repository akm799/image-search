package uk.co.akm.test.imagesearch.process;

import android.graphics.Bitmap;

public interface ImageOperator<I> {

    String getDescription();

    I processImage(Bitmap image);
}
