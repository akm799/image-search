package uk.co.akm.test.imagesearch.view.combined;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import uk.co.akm.test.imagesearch.R;
import uk.co.akm.test.imagesearch.photo.PhotoIO;
import uk.co.akm.test.imagesearch.photo.impl.PhotoIOImpl;
import uk.co.akm.test.imagesearch.view.combined.window.InternalWindow;

public class PhotoWindowView extends View {
    private static final float DEFAULT_BORDER_WIDTH = 0f;
    private static final float DEFAULT_WINDOW_BORDER_WIDTH = 2f;
    private static final int DEFAULT_BORDER_COLOUR = Color.BLACK;
    private static final int DEFAULT_WINDOW_BORDER_COLOUR = Color.RED;
    private static final float DEFAULT_WINDOW_INITIAL_SIDE_FRACTION = 0.1f;

    private Paint windowPaint;
    private Paint borderPaint;
    private float windowInitialSideFraction;

    private InternalWindow window;
    private PhotoWindowState savedWindowState;

    private Bitmap photo;
    private String photoName;
    private Rect photoRectangle;

    private String restoredPhotoName;

    private final PhotoIO photoIO = new PhotoIOImpl();

    public PhotoWindowView(Context context) {
        super(context);
        init();
    }

    public PhotoWindowView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PhotoWindowView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public PhotoWindowView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init() {
        windowPaint = buildStrokePaint(DEFAULT_WINDOW_BORDER_WIDTH, DEFAULT_WINDOW_BORDER_COLOUR);
        setSaveEnabled(true); // Without this, the view state will not be saved.
    }

    private void init(Context context, AttributeSet attrs) {
        final TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DynamicWindowView, 0, 0);
        init(attributes);
        setSaveEnabled(true); // Without this, the view state will not be saved.
    }

    private void init(TypedArray attributes) {
        windowInitialSideFraction = attributes.getFraction(R.styleable.DynamicWindowView_windowInitSideFraction, 1, 1, DEFAULT_WINDOW_INITIAL_SIDE_FRACTION);
        borderPaint = buildStrokePaint(attributes, R.styleable.DynamicWindowView_borderWidth, DEFAULT_BORDER_WIDTH, R.styleable.DynamicWindowView_borderColour, DEFAULT_BORDER_COLOUR);
        windowPaint = buildStrokePaint(attributes, R.styleable.DynamicWindowView_windowBorderWidth, DEFAULT_WINDOW_BORDER_WIDTH, R.styleable.DynamicWindowView_windowBorderColour, DEFAULT_WINDOW_BORDER_COLOUR);
    }

    private Paint buildStrokePaint(TypedArray attributes, int strokeWidthIndex, float stokeWidthDefault, int colourIndex, int colourDefault) {
        final int colour = attributes.getColor(colourIndex, colourDefault);
        final float strokeWidth = attributes.getDimension(strokeWidthIndex, stokeWidthDefault);

        return buildStrokePaint(strokeWidth, colour);
    }

    private Paint buildStrokePaint(float strokeWidth, int colour) {
        if (strokeWidth == 0) {
            return null;
        }

        final Paint paint = new Paint();
        paint.setColor(colour);
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(Paint.Style.STROKE); // So that we can draw empty rectangles.

        return paint;
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

        if (photoName == null && window == null) {
            return superState;
        } else if (photoName == null) {
            return new PhotoWindowState(superState, window.getState(this));
        } else if (window == null) {
            return new PhotoWindowState(superState, photoName);
        } else {
            return new PhotoWindowState(superState, window.getState(this), photoName);
        }
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof PhotoWindowState) {
            final PhotoWindowState savedWindowState = (PhotoWindowState) state;
            super.onRestoreInstanceState(savedWindowState.getSuperState());

            if (photoName == null) {
                restoredPhotoName = savedWindowState.getPhotoName(); // Cannot display the photo yet because at this point our view dimensions are zero.
            }

            if (window == null) {
                this.savedWindowState = savedWindowState; // Cannot create the window from the window state yet because at this point our view dimensions are zero.
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

        // Must restore the window from the saved state (if any) here, where the view has, by now, non-zero dimensions.
        if (savedWindowState != null) {
            window = savedWindowState.toInternalWindow(this);
            savedWindowState = null;
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        drawPhoto(canvas);
        drawDynamicWindow(canvas);
        drawBorder(canvas);
    }

    private void drawPhoto(Canvas canvas) {
        if (photo != null) {
            if (photoRectangle == null) {
                photoRectangle = PhotoRectangleFunctions.buildDestinationRectangle(this, photo);
            }

            canvas.drawBitmap(photo, null, photoRectangle, null);
        }
    }

    private void drawDynamicWindow(Canvas canvas) {
        if (window != null) {
            window.draw(canvas, windowPaint);
        }
    }

    private void drawBorder(Canvas canvas) {
        if (borderPaint != null) {
            canvas.drawRect(0, 0, getWidth(), getHeight(), borderPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isEnabled()) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    stopTracking();
                    break;

                case MotionEvent.ACTION_DOWN:
                    createWindow(event);
                    break;

                case MotionEvent.ACTION_MOVE:
                    onChangePositionOrSizeTouchEvent(event);
            }

            return true;
        } else {
            return super.onTouchEvent(event);
        }
    }

    private void stopTracking() {
        if (window != null) {
            window.stopTracking();
        }
    }

    private void createWindow(MotionEvent event) {
        if (window == null && InternalWindow.canInitializeWindow(this, event, windowInitialSideFraction)) {
            window = new InternalWindow(this, event, windowInitialSideFraction);
            invalidate();
        }
    }

    private void onChangePositionOrSizeTouchEvent(MotionEvent event) {
        if (window != null) {
            window.onChangePositionOrSizeTouchEvent(event);
        }
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

    public final boolean saveInternalWindowBitmap(Context context, String imageName) {
        if (window == null) {
            return false;
        }

        final Bitmap windowBitmap = createInternalWindowBitmap(window);

        return photoIO.writeImage(context, windowBitmap, imageName);
    }

    private Bitmap createInternalWindowBitmap(InternalWindow window) {
        final int x = Math.round(window.getWindowLeft()) - photoRectangle.left;
        final int y = Math.round(window.getWindowTop()) - photoRectangle.top;
        final int width = (int)window.getWindowWidth();
        final int height = (int)window.getWindowHeight();

Log.d("TEMP", "view=(" + getWidth() + ", " + getHeight() + ")");
Log.d("TEMP", "large_window=(" + photoRectangle.left + ", " + photoRectangle.top + ") (" + photoRectangle.width() + ", " + photoRectangle.height() + ")");
Log.d("TEMP", "large_bitmap=(" + 0 + ", " + 0 + ") (" + photo.getWidth() + ", " + photo.getHeight() + ")");
Log.d("TEMP", "small_window=(" + window.getWindowLeft() + ", " + window.getWindowTop() + ") (" + window.getWindowWidth() + ", " + window.getWindowHeight() + ")");
Log.d("TEMP", "small_bitmap=(" + x + ", " + y + ") (" + width + ", " + height + ")");
        return Bitmap.createBitmap(photo, x, y, width, height);
    }
}
