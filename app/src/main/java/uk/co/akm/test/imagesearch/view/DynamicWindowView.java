package uk.co.akm.test.imagesearch.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DynamicWindowView extends View {
    private static final float NO_VALUE = -1f;

    private final float sizeFraction = 0.1f;
    private final Paint paint = new Paint();

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

    public DynamicWindowView(Context context) {
        super(context);
        init();
    }

    public DynamicWindowView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DynamicWindowView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public DynamicWindowView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        paint.setColor(Color.RED);
        paint.setStrokeWidth(2f);
        paint.setStyle(Paint.Style.STROKE); // So that we can draw empty rectangles.
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (wLeft == NO_VALUE) {
            initWindow();
        }

        drawWindow(canvas);
    }

    private void initWindow() {
        final float f = 1f - sizeFraction;
        wLeft = f*getWidth()/2;
        wTop = f*getHeight()/2;

        final float side = sizeFraction*Math.min(getWidth(), getHeight());
        wWidth = side;
        wHeight = side;
    }

    private void drawWindow(Canvas canvas) {
        canvas.drawRect(wLeft, wTop, wLeft + wWidth, wTop + wHeight, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isEnabled()) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_DOWN:
                    clearTracker();
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
            final float dx = xPosNew - xPos;
            final float dy = yPosNew - yPos;

            final float newLeft = wLeft + dx;
            final float newTop = wTop + dy;
            if (newLeft > 0 && newTop > 0 && newLeft + wWidth < getWidth() && newTop + wHeight < getHeight()) {
                wLeft = newLeft;
                wTop = newTop;
                invalidate();
            }
        }

        xPos = xPosNew;
        yPos = yPosNew;
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
            final float dx = (xMaxNew - xMinNew) - (xMax - xMin);
            final float newWidth = wWidth + dx;
            if (newWidth > 0 && wLeft + newWidth < getWidth()) {
                wWidth = newWidth;
                invalidate();
            }
        }

        xMin = xMinNew;
        xMax = xMaxNew;
    }

    private void processHeightChangeMove(float y1, float y2) {
        final float yMinNew = Math.min(y1, y2);
        final float yMaxNew = Math.max(y1, y2);

        if (yMin != NO_VALUE) {
            final float dy = (yMaxNew - yMinNew) - (yMax - yMin);
            final float newHeight = wHeight + dy;
            if (newHeight > 0 && wTop + newHeight < getHeight()) {
                wHeight = newHeight;
                invalidate();
            }
        }

        yMin = yMinNew;
        yMax = yMaxNew;
    }
}
