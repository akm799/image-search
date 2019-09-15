package uk.co.akm.test.imagesearch.view.combined.window;


import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

/**
 * Represents the state of a rectangular window within a parent view and has methods that move and
 * change the size of this window (always within the parent view) in response to touch events in the
 * parent view.
 */
public final class InternalWindow {
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

    private final View parent;

    public static boolean canInitializeWindow(View parent, MotionEvent event, float windowInitialSideFraction) {
        return canInitializeWindow(parent, event.getX(), event.getY(), windowInitialSideFraction);
    }

    private static boolean canInitializeWindow(View parent, float x, float y, float windowInitialSideFraction) {
        final float side = windowInitialSideFraction*Math.min(parent.getWidth(), parent.getHeight());
        final float halfSide = side/2;
        final float initLeft = x - halfSide;
        final float initTop = y - halfSide;

        return RangeFunctions.inRange(initLeft, side, parent.getWidth(), initTop, side, parent.getHeight());
    }

    InternalWindow(View parent, float wLeft, float wTop, float wWidth, float wHeight) {
        this.parent = parent;
        this.wLeft = wLeft;
        this.wTop = wTop;
        this.wWidth = wWidth;
        this.wHeight = wHeight;
    }

    public InternalWindow(View parent, MotionEvent event, float windowInitialSideFraction) {
        this.parent = parent;
        initWindow(event.getX(), event.getY(), windowInitialSideFraction);
    }

    private void initWindow(float x, float y, float windowInitialSideFraction) {
        final float side = windowInitialSideFraction*Math.min(parent.getWidth(), parent.getHeight());
        final float halfSide = side/2;
        final float initLeft = x - halfSide;
        final float initTop = y - halfSide;

        if (RangeFunctions.inRange(initLeft, side, parent.getWidth(), initTop, side, parent.getHeight())) {
            wWidth = side;
            wHeight = side;
            wLeft = initLeft;
            wTop = initTop;
        }
    }

    public InternalWindowState getState(View parent) {
        return new InternalWindowState(parent, wLeft, wTop, wWidth, wHeight);
    }

    public void draw(Canvas canvas, Paint paint) {
        canvas.drawRect(wLeft, wTop, wLeft + wWidth, wTop + wHeight, paint);
    }

    public void stopTracking() {
        xPos = NO_VALUE;
        yPos = NO_VALUE;

        xMin = NO_VALUE;
        xMax = NO_VALUE;
        yMin = NO_VALUE;
        yMax = NO_VALUE;
    }

    public void onChangePositionOrSizeTouchEvent(MotionEvent event) {
        switch (event.getPointerCount()) {
            case 1:
                onPositionChangeTouchEvent(event);
                break;

            case 2:
                onSizeChangeTouchEvent(event);
                break;
        }
    }

    private void onPositionChangeTouchEvent(MotionEvent event) {
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

        if (RangeFunctions.inRange(newLeft, wWidth, parent.getWidth(), newTop, wHeight, parent.getHeight())) {
            wLeft = newLeft;
            wTop = newTop;
            parent.invalidate();
        }
    }

    private void onSizeChangeTouchEvent(MotionEvent event) {
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
            if (RangeFunctions.inRange(newLeft, newWidth, parent.getWidth())) {
                wLeft = newLeft;
                wWidth = newWidth;
                parent.invalidate();
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
            if (RangeFunctions.inRange(newTop, newHeight, parent.getHeight())) {
                wTop = newTop;
                wHeight = newHeight;
                parent.invalidate();
            }
        }
    }

    @Override
    public String toString() {
        return ("(left, top)=(" + wLeft + ", " + wTop + ") [width, height]=[" + wWidth + ", " + wHeight + "] in ["+ parent.getWidth() + ", " + parent.getHeight() + "].");
    }
}
