package uk.co.akm.test.imagesearch.view.combined;


import android.graphics.Bitmap;
import android.graphics.Rect;
import android.view.View;

final class PhotoRectangleFunctions {

    static Rect buildDestinationRectangle(View frame, Bitmap bitmap) {
        final float bitmapAspectRatio = (float)bitmap.getWidth()/bitmap.getHeight();

        final Rect widthMaintained = maintainWidth(frame, bitmapAspectRatio);
        if (widthMaintained != null) {
            return widthMaintained;
        }

        return maintainHeight(frame, bitmapAspectRatio);
    }

    private static Rect maintainWidth(View frame, float aspectRatio) {
        final int w = frame.getWidth();
        final int h = Math.round(w/aspectRatio);
        if (h > frame.getHeight()) {
            return null;
        }

        final int topMargin = Math.round((frame.getHeight() - h) / 2f);

        return new Rect(0, topMargin, w, frame.getHeight() - topMargin);
    }

    private static Rect maintainHeight(View frame, float aspectRatio) {
        final int h = frame.getHeight();
        final int w = Math.round(h*aspectRatio);
        if (w > frame.getWidth()) {
            return null;
        }

        final int leftMargin = Math.round((frame.getWidth() - w) / 2f);

        return new Rect(leftMargin, 0, frame.getWidth() - leftMargin, h);
    }

    private PhotoRectangleFunctions() {}
}
