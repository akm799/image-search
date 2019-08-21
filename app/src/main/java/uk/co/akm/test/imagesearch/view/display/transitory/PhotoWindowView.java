package uk.co.akm.test.imagesearch.view.display.transitory;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class PhotoWindowView extends View {
    private Bitmap photo;
    private Rect photoRectangle;

    public PhotoWindowView(Context context) {
        super(context);
    }

    public PhotoWindowView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PhotoWindowView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PhotoWindowView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setPhoto(Bitmap photo) {
        this.photo = rotateBitmap(photo);
        invalidate();
    }

    private Bitmap rotateBitmap(Bitmap bitmap) {
        final Matrix rotation = new Matrix();
        rotation.postRotate(90);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), rotation, true);
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (photo != null) {
            if (photoRectangle == null) {
                photoRectangle = buildDestinationRectangle(photo);
            }

            canvas.drawBitmap(photo, null, photoRectangle, null);
        }
    }

    private Rect buildDestinationRectangle(Bitmap bitmap) {
        final float bitmapAspectRatio = (float)bitmap.getWidth()/bitmap.getHeight();

        final Rect widthMaintained = maintainWidth(bitmapAspectRatio);
        if (widthMaintained != null) {
            return widthMaintained;
        }

        return maintainHeight(bitmapAspectRatio);
    }

    private Rect maintainWidth(float aspectRatio) {
        final int w = getWidth();
        final int h = Math.round(w/aspectRatio);
        if (h > getHeight()) {
            return null;
        }

        final int topMargin = Math.round((getHeight() - h) / 2f);

        return new Rect(0, topMargin, getWidth(), getHeight() - topMargin);
    }

    private Rect maintainHeight(float aspectRatio) {
        final int h = getHeight();
        final int w = Math.round(h*aspectRatio);
        if (w > getWidth()) {
            return null;
        }

        final int leftMargin = Math.round((getWidth() - w) / 2f);

        return new Rect(leftMargin, 0, getWidth() - leftMargin, getHeight());
    }
}
