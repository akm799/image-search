package uk.co.akm.test.imagesearch.process.model.window;


import android.support.constraint.solver.widgets.Rectangle;

/**
 * Utility representation of a rectangular image window.
 *
 * Created by Thanos Mavroidis on 06/07/2019.
 */
public class Window {
    public final int width;
    public final int height;
    public final int xMin;
    public final int xMax;
    public final int yMin;
    public final int yMax;

    public final int left;
    public final int top;

    public Window(int xMin, int yMin, int width, int height) {
        this.width = width;
        this.height = height;
        this.xMin = xMin;
        this.xMax = xMin + width - 1;
        this.yMin = yMin;
        this.yMax = yMin + height - 1;

        this.left = xMin;
        this.top = yMin;
    }

    public Window(Window window) {
        this.width = window.width;
        this.height = window.height;
        this.xMin = window.xMin;
        this.xMax = window.xMax;
        this.yMin = window.yMin;
        this.yMax = window.yMax;

        left = xMin;
        top = yMin;
    }

    public Window(Rectangle rectangle) {
        width = rectangle.width;
        height = rectangle.height;
        xMin = rectangle.x;
        xMax = rectangle.x + rectangle.width - 1;
        yMin = rectangle.y;
        yMax = rectangle.y + rectangle.height - 1;

        left = xMin;
        top = yMin;
    }

    public Window shift(int dx, int dy) {
        return new Window(xMin + dx, yMin + dy, width, height);
    }

    @Override
    public String toString() {
        return "[leftTop=(" + left + ", " + top + "), width=" + width + ", height=" + height + "]";
    }
}
