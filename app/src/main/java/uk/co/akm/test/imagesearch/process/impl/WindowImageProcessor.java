package uk.co.akm.test.imagesearch.process.impl;

import android.graphics.Bitmap;
import android.support.constraint.solver.widgets.Rectangle;

import java.util.ArrayList;
import java.util.Collection;

import uk.co.akm.test.imagesearch.process.ImageProcessor;
import uk.co.akm.test.imagesearch.process.model.window.ColouredWindow;
import uk.co.akm.test.imagesearch.process.model.window.Window;

/**
 * Returns an output image with one or more rectangular windows
 * superimposed on the input image.
 *
 * Created by Thanos Mavroidis on 01/11/2019.
 */
public final class WindowImageProcessor implements ImageProcessor {
    private final Collection<ColouredWindow> windows = new ArrayList<>();

    /**
     * @param window the window to superimpose when processing the image
     */
    public WindowImageProcessor(ColouredWindow window) {
        this.windows.add(window);
    }

    /**
     * @param windows the windows to superimpose when processing the image
     */
    public WindowImageProcessor(Collection<ColouredWindow> windows) {
        this.windows.addAll(windows);
    }

    @Override
    public String getDescription() {
        return "Superimposed windows on image.";
    }

    @Override
    public Bitmap processImage(Bitmap image) {
        checkAllWindowsAreWithinTheImage(image);
        return drawBitmapWithWindows(image, windows);
    }

    private Bitmap drawBitmapWithWindows(Bitmap input, Collection<ColouredWindow> windows) {
        final Bitmap output = Bitmap.createBitmap(input);
        drawWindows(output, windows);

        return output;
    }

    private void drawWindows(Bitmap bitmap, Collection<ColouredWindow> windows) {
        for (ColouredWindow window : windows) {
            drawWindow(bitmap, window);
        }
    }

    private void drawWindow(Bitmap bitmap, ColouredWindow window) {
        final int rgb = window.rgb;

        // Horizontal window lines
        for (int i=window.xMin ; i<=window.xMax ; i++) {
            bitmap.setPixel(i, window.yMin, rgb);
            bitmap.setPixel(i, window.yMax, rgb);
        }

        // Vertical window lines
        for (int j=window.yMin ; j<=window.yMax ; j++) {
            bitmap.setPixel(window.xMin, j, rgb);
            bitmap.setPixel(window.xMax, j, rgb);
        }
    }

    private void checkAllWindowsAreWithinTheImage(Bitmap image) {
        final Rectangle imageWindowRectangle = new Rectangle();
        imageWindowRectangle.setBounds(0, 0, image.getWidth(), image.getHeight());

        final Window imageWindow = new Window(imageWindowRectangle);
        for (Window window : windows) {
            checkWindowIsWithinTheImage(window, imageWindow);
        }
    }

    private void checkWindowIsWithinTheImage(Window window, Window image) {
        if (window.xMin < image.xMin || window.xMax > image.xMax || window.yMin < image.yMin || window.yMax > image.yMax) {
            throw new IllegalArgumentException("Window " + window + " is not entirely within the image window " + image + ".");
        }
    }
}
