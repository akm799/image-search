package uk.co.akm.test.imagesearch.process.track.search.impl;


import android.graphics.Bitmap;

import uk.co.akm.test.imagesearch.process.model.window.Window;
import uk.co.akm.test.imagesearch.process.track.search.BestMatchFinder;
import uk.co.akm.test.imagesearch.process.track.search.impl.map.ColourHistogram;
import uk.co.akm.test.imagesearch.process.track.search.impl.map.PixelMap;

/**
 * Brute force best match finder based on colour tracking. This finder will create multiple windows
 * on the test image (equal to the target window size) and then compare their colour histogram
 * difference with the target window. The best match will be the window with the minimum colour
 * histogram difference.
 *
 * Created by Thanos Mavroidis on 18/02/2021.
 */
public final class BasicBestMatchFinder2 implements BestMatchFinder {
    private final int nSideDivs = 51;
    private final ColourHistogram colourHistogram = new ColourHistogram(nSideDivs);
    private final ColourHistogram testColourHistogram = new ColourHistogram(nSideDivs);

    @Override
    public Window findBestMatch(Bitmap targetImage, Window targetWindow, Bitmap image) {
        final PixelMap targetImageMap = colourHistogram.toPixelMap(targetImage);
        colourHistogram.fillColourHistogramForWindow(targetImageMap, targetWindow);

        return findBestMatchWindow(targetImageMap, targetWindow);
    }

    private Window findBestMatchWindow(PixelMap image, Window targetWindow) {
        int xMin = 0;
        int yMin = 0;
        int minDiff = Integer.MAX_VALUE;
        for (int j=0 ; j<image.getHeight() - targetWindow.height ; j += targetWindow.height) {
            for (int i=0 ; i<image.getWidth() - targetWindow.width ; i += targetWindow.width) {
                final Window testWindow = new Window(i, j, targetWindow.width, targetWindow.height);
                final int diff = diffColourHistogramForWindow(image, testWindow);
                if (diff < minDiff) {
                    minDiff = diff;
                    xMin = i;
                    yMin = j;
                }
            }
        }

        return new Window(xMin, yMin, targetWindow.width, targetWindow.height);
    }

    private int diffColourHistogramForWindow(PixelMap image, Window window) {
        testColourHistogram.fillColourHistogramForWindow(image, window);

        return colourHistogram.diff(testColourHistogram);
    }
}
