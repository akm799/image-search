package uk.co.akm.test.imagesearch.view.display;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class PhotoWindowView extends View {
    private Bitmap photo;

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
        this.photo = photo;
        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (photo != null) {
            final Rect dest = buildDestinationRectangle(photo);
            canvas.drawBitmap(photo, null, dest, null);
        }
    }

    private Rect buildDestinationRectangle(Bitmap bitmap) {
        final float bitmapAspectRatio = (float)bitmap.getWidth()/bitmap.getHeight();

        float height = Math.round(getWidth()*bitmapAspectRatio);
        if (height <= getHeight()) {
            final int topMargin = Math.round((getHeight() - height) / 2);

            return new Rect(0, topMargin, getWidth(), getHeight() - topMargin);
        } else {
            float width = Math.round(getHeight()/bitmapAspectRatio);
            final int leftMargin = Math.round((getWidth() - width) / 2);

            return new Rect(leftMargin, 0, getWidth() - leftMargin, getHeight());
        }
    }
}
