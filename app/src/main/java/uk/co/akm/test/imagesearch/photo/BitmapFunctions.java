package uk.co.akm.test.imagesearch.photo;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class BitmapFunctions {

    public static Bitmap quarterRotateClockwise(Bitmap bitmap, boolean recycleInput) {
        return rotateClockwise(bitmap, 90, recycleInput);
    }

    public static Bitmap rotateClockwise(Bitmap bitmap, float degrees, boolean recycleInput) {
        final Matrix rotation = new Matrix();
        rotation.postRotate(degrees);

        final Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), rotation, true);
        if (recycleInput) {
            bitmap.recycle();
        }

        return rotatedBitmap;
    }

    private BitmapFunctions() {}
}
