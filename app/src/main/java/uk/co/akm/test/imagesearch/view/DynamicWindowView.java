package uk.co.akm.test.imagesearch.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import uk.co.akm.test.imagesearch.R;

/**
 * This is a utility view that allows the user to define a window inside the view area.
 */
public class DynamicWindowView extends View {
    private static final float DEFAULT_BORDER_WIDTH = 0f;
    private static final float DEFAULT_WINDOW_BORDER_WIDTH = 2f;
    private static final int DEFAULT_BORDER_COLOUR = Color.BLACK;
    private static final int DEFAULT_WINDOW_BORDER_COLOUR = Color.RED;
    private static final float DEFAULT_WINDOW_INITIAL_SIDE_FRACTION = 0.1f;

    private Paint windowPaint;
    private Paint borderPaint;
    private float windowInitialSideFraction;

    private InternalWindow window;

    public DynamicWindowView(Context context) {
        super(context);
        init();
    }

    public DynamicWindowView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public DynamicWindowView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public DynamicWindowView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init() {
        windowPaint = buildStrokePaint(DEFAULT_WINDOW_BORDER_WIDTH, DEFAULT_WINDOW_BORDER_COLOUR);
    }

    private void init(Context context, AttributeSet attrs) {
        final TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DynamicWindowView, 0, 0);
        init(attributes);
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

    @Override
    public void onDraw(Canvas canvas) {
        if (borderPaint != null) {
            drawBorder(canvas);
        }

        if (window != null) {
            window.draw(canvas, windowPaint);
        }
    }

    private void drawBorder(Canvas canvas) {
        canvas.drawRect(0, 0, getWidth(), getHeight(), borderPaint);
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
        }
    }

    private void onChangePositionOrSizeTouchEvent(MotionEvent event) {
        if (window != null) {
            window.onChangePositionOrSizeTouchEvent(event);
        }
    }
}
