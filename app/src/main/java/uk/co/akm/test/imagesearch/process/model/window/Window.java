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

    public Window(Window window) {
        this.width = window.width;
        this.height = window.height;
        this.xMin = window.xMin;
        this.xMax = window.xMax;
        this.yMin = window.yMin;
        this.yMax = window.yMax;
    }

    public Window(Rectangle rectangle) {
        width = rectangle.width;
        height = rectangle.height;
        xMin = rectangle.x;
        xMax = rectangle.x + rectangle.width - 1;
        yMin = rectangle.y;
        yMax = rectangle.y + rectangle.height - 1;
    }

    @Override
    public String toString() {
        return "[topLeft=(" + xMin + ", " + yMin + "), width=" + width + ", height=" + height + "]";
    }
}
