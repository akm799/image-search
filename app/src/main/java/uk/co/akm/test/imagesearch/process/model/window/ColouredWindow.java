package uk.co.akm.test.imagesearch.process.model.window;

import android.support.constraint.solver.widgets.Rectangle;

public final class ColouredWindow extends Window {
    public final int rgb;

    public ColouredWindow(Window window, int rgb) {
        super(window);

        this.rgb = rgb;
    }

    public ColouredWindow(Rectangle rectangle, int rgb) {
        super(rectangle);

        this.rgb = rgb;
    }
}
