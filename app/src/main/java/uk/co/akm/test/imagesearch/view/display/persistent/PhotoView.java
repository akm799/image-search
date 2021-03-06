package uk.co.akm.test.imagesearch.view.display.persistent;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import uk.co.akm.test.imagesearch.photo.PhotoIO;
import uk.co.akm.test.imagesearch.photo.impl.PhotoIOImpl;

public class PhotoView extends View {
    private Bitmap photo;
    private String photoName;
    private Rect photoRectangle;

    private String restoredPhotoName;

    private final PhotoIO photoIO = new PhotoIOImpl();

    public PhotoView(Context context) {
        super(context);
        init();
    }

    public PhotoView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PhotoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public PhotoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setSaveEnabled(true); // Without this, the view state will not be saved.
    }

    public void setPhoto(String photoName) {
        final Bitmap photo = photoIO.readCapturedImage(getContext(), photoName);
        if (photo != null) {
            this.photo = rotateBitmap(photo);
            this.photoName = photoName; // Cannot display the photo yet because at this point our view dimensions may be zero.
            photo.recycle();
            invalidate();
        }
    }

    private Bitmap rotateBitmap(Bitmap bitmap) {
        final Matrix rotation = new Matrix();
        rotation.postRotate(90);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), rotation, true);
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();

        if (photoName == null) {
            return superState;
        } else {
            return new PhotoViewState(superState, photoName);
        }
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof PhotoViewState) {
            final PhotoViewState photoWindowState = (PhotoViewState)state;
            super.onRestoreInstanceState(photoWindowState.getSuperState());
            if (photoName == null) {
                restoredPhotoName = photoWindowState.getPhotoName(); // Cannot display the photo yet because at this point our view dimensions are zero.
            }
        } else {
            super.onRestoreInstanceState(state);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        // Must display the photo from the saved state (if any) here, where the view has, by now, non-zero dimensions.
        if (restoredPhotoName != null) {
            setPhoto(restoredPhotoName);
            restoredPhotoName = null;
        }
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

    public final void clear() {
        if (photoName != null) {
            photoIO.deleteCapturedImage(getContext(), photoName);
            photo.recycle();

            photo = null;
            photoName = null;
            photoRectangle = null;
            restoredPhotoName = null;
        }
    }
}
