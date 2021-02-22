package uk.co.akm.test.imagesearch.process.track.search.impl;


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
public final class BasicBestMatchFinder implements BestMatchFinder {
    private final ColourHistogram colourHistogram;
    private final ColourHistogram testColourHistogram;

    private final int windowWidth;
    private final int windowHeight;

    public BasicBestMatchFinder(ColourHistogram colourHistogram, int windowWidth, int windowHeight) {
        this.colourHistogram = colourHistogram;
        this.testColourHistogram = new ColourHistogram(colourHistogram.getNSideDivs());
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
    }

    @Override
    public Window findBestMatch(PixelMap image) {
        int xMin = 0;
        int yMin = 0;
        int minDiff = Integer.MAX_VALUE;
        for (int j=0 ; j<image.getHeight() - windowHeight ; j += windowHeight) {
            for (int i=0 ; i<image.getWidth() - windowWidth ; i += windowWidth) {
                final Window testWindow = new Window(i, j, windowWidth, windowHeight);
                final int diff = diffColourHistogramForWindow(image, testWindow);
                if (diff < minDiff) {
                    minDiff = diff;
                    xMin = i;
                    yMin = j;
                }
            }
        }

        return new Window(xMin, yMin, windowWidth, windowHeight);
    }

    private int diffColourHistogramForWindow(PixelMap image, Window window) {
        testColourHistogram.fillColourHistogramForWindow(image, window);

        return colourHistogram.diff(testColourHistogram);
    }
}
