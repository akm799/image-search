package uk.co.akm.test.imagesearch.process.track.search;

import android.graphics.Bitmap;
import android.support.constraint.solver.widgets.Rectangle;

import java.util.Iterator;

import uk.co.akm.test.imagesearch.process.model.window.Window;


/**
 * An iterator that iterates though all adjacent, (mostly) non-overlapping, windows that fully cover an image.
 * To create this iterator we specify an image and a window within it. The iterator will the allows as to
 * iterate over over all windows of the same size that fully cover the image. These windows have exactly the
 * same size as the input window are adjacent and, in most cases, are non-overlapping. Some windows near the
 * image boundaries may overlap since the dimension of the input image and window does not allow an exact
 * multiple of windows to fit exactly within the image.
 *
 * Created by Thanos Mavroidis on 28/07/2019.
 */
public final class WindowsIterator implements Iterator<Window> {
    private final int imageWidth;
    private final int imageHeight;

    private final int windowWidth;
    private final int windowHeight;

    private final int nWidths;
    private final int nWindows;

    private final Rectangle rectangle = new Rectangle();

    private int windowsCounter = 0;

    /**
     * @param image the image we want fully covered
     * @param window The iterator will return windows of this exact size (width and height). All windows returned by
     *               this iterator are adjacent (mostly) non-overlapping and fully cover the input image.
     * @throws IllegalArgumentException if the input window is not fully contained by the input image
     */
    public WindowsIterator(Bitmap image, Window window) {
        checkImageContainsWindow(image, window);

        imageWidth = image.getWidth();
        imageHeight = image.getHeight();

        windowWidth = window.width;
        windowHeight = window.height;

        nWidths = partsIn(image.getWidth(), window.width);
        final int nHeights = partsIn(image.getHeight(), window.height);
        nWindows = nWidths*nHeights;

        rectangle.width = windowWidth;
        rectangle.height = windowHeight;
    }

    private void checkImageContainsWindow(Bitmap image, Window window) {
        if (window.xMin < 0 || window.width > image.getWidth() || window.yMin < 0 || window.height > image.getWidth()) {
            throw new IllegalArgumentException("Input image with dimensions (" + image.getWidth() + ", " + image.getHeight() + ") does not fully contain the input window " + window + ".");
        }
    }

    @Override
    public boolean hasNext() {
        return windowsCounter < nWindows;
    }

    @Override
    public Window next() {
        final int xIndex = windowsCounter%nWidths;
        final int yIndex = windowsCounter/nWidths;

        final int left = xIndex*windowWidth;
        final int top = yIndex*windowHeight;

        final int safeLeft = (left + windowWidth <= imageWidth ? left : imageWidth - windowWidth);
        final int safeTop = (top + windowHeight <= imageHeight ? top : imageHeight - windowHeight);

        rectangle.x = safeLeft;
        rectangle.y = safeTop;

        windowsCounter++;

        return new Window(rectangle);
    }

    private int partsIn(int large, int small) {
        return (large/small + (large%small == 0 ? 0 : 1));
    }
}
