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

    private static final float NO_VALUE = -1f;


    private float wLeft = NO_VALUE;
    private float wTop = NO_VALUE;
    private float wWidth = NO_VALUE;
    private float wHeight = NO_VALUE;

    private float xPos = NO_VALUE;
    private float yPos = NO_VALUE;

    private float xMin = NO_VALUE;
    private float xMax = NO_VALUE;
    private float yMin = NO_VALUE;
    private float yMax = NO_VALUE;

    private Paint windowPaint;
    private Paint borderPaint;
    private float windowInitialSideFraction;

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

        if (wLeft != NO_VALUE) {
            drawWindow(canvas);
        }
    }

    private void drawBorder(Canvas canvas) {
        canvas.drawRect(0, 0, getWidth(), getHeight(), borderPaint);
    }

    private void drawWindow(Canvas canvas) {
        canvas.drawRect(wLeft, wTop, wLeft + wWidth, wTop + wHeight, windowPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isEnabled()) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    clearTracker();
                    break;

                case MotionEvent.ACTION_DOWN:
                    processTouch(event);
                    break;

                case MotionEvent.ACTION_MOVE:
                    processMove(event);
            }

            return true;
        } else {
            return super.onTouchEvent(event);
        }
    }

    private void clearTracker() {
        clearSizeTracker();
        clearPositionTracker();
    }

    private void clearPositionTracker() {
        xPos = NO_VALUE;
        yPos = NO_VALUE;
    }

    private void clearSizeTracker() {
        xMin = NO_VALUE;
        xMax = NO_VALUE;
        yMin = NO_VALUE;
        yMax = NO_VALUE;
    }

    private void processTouch(MotionEvent event) {
        if (wLeft == NO_VALUE) {
            initWindow(event.getX(), event.getY());
        }
    }

    private void initWindow(float x, float y) {
        final float side = windowInitialSideFraction*Math.min(getWidth(), getHeight());
        final float halfSide = side/2;
        final float initLeft = x - halfSide;
        final float initTop = y - halfSide;

        if (inRange(initLeft, side, getWidth()) && inRange(initTop, side, getHeight())) {
            wWidth = side;
            wHeight = side;
            wLeft = initLeft;
            wTop = initTop;
            invalidate();
        }
    }

    private void processMove(MotionEvent event) {
        switch (event.getPointerCount()) {
            case 1:
                processPositionChangeMove(event);
                break;

            case 2:
                processSizeChangeMove(event);
                break;
        }
    }

    private void processPositionChangeMove(MotionEvent event) {
        final float xPosNew = event.getX();
        final float yPosNew = event.getY();

        if (xPos != NO_VALUE) {
            changePosition(xPosNew, yPosNew);
        }

        xPos = xPosNew;
        yPos = yPosNew;
    }

    private void changePosition(float xPosNew, float yPosNew) {
        final float dx = xPosNew - xPos;
        final float dy = yPosNew - yPos;

        final float newLeft = wLeft + dx;
        final float newTop = wTop + dy;

        if (inRange(newLeft, wWidth, getWidth()) && inRange(newTop, wHeight, getHeight())) {
            wLeft = newLeft;
            wTop = newTop;
            invalidate();
        }
    }

    private void processSizeChangeMove(MotionEvent event) {
        final float x1 = event.getX(0);
        final float x2 = event.getX(1);
        final float dx = Math.abs(x2 - x1);

        final float y1 = event.getY(0);
        final float y2 = event.getY(1);
        final float dy = Math.abs(y2 - y1);

        if (dx > dy) {
            processWidthChangeMove(x1, x2);
        } else {
            processHeightChangeMove(y1, y2);
        }
    }

    private void processWidthChangeMove(float x1, float x2) {
        final float xMinNew = Math.min(x1, x2);
        final float xMaxNew = Math.max(x1, x2);

        if (xMin != NO_VALUE) {
            changeWidth(xMinNew, xMaxNew);
        }

        xMin = xMinNew;
        xMax = xMaxNew;
    }

    private void changeWidth(float xMinNew, float xMaxNew) {
        final float dx = (xMaxNew - xMinNew) - (xMax - xMin);
        final float newWidth = wWidth + dx;
        if (newWidth > 0) {
            final float newLeft = wLeft - dx/2;
            if (inRange(newLeft, newWidth, getWidth())) {
                wLeft = newLeft;
                wWidth = newWidth;
                invalidate();
            }
        }
    }

    private void processHeightChangeMove(float y1, float y2) {
        final float yMinNew = Math.min(y1, y2);
        final float yMaxNew = Math.max(y1, y2);

        if (yMin != NO_VALUE) {
            changeHeight(yMinNew, yMaxNew);
        }

        yMin = yMinNew;
        yMax = yMaxNew;
    }

    private void changeHeight(float yMinNew, float yMaxNew) {
        final float dy = (yMaxNew - yMinNew) - (yMax - yMin);
        final float newHeight = wHeight + dy;
        if (newHeight > 0) {
            final float newTop = wTop - dy/2;
            if (inRange(newTop, newHeight, getHeight())) {
                wTop = newTop;
                wHeight = newHeight;
                invalidate();
            }
        }
    }

    private boolean inRange(float start, float size, int maxSize) {
        return start > 0 && start + size < maxSize;
    }
}
