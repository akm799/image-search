package uk.co.akm.test.imagesearch.process.track.search.impl;


import android.graphics.Bitmap;
import android.util.Log;

import java.util.Arrays;

import uk.co.akm.test.imagesearch.process.model.window.Window;
import uk.co.akm.test.imagesearch.process.track.search.BestMatchFinder;
import uk.co.akm.test.imagesearch.process.util.ColourHelper;

/**
 * Created by Thanos Mavroidis on 29/07/2019.
 */
public final class BasicBestMatchFinder implements BestMatchFinder {
    private static final int MAX_COLOUR_VALUE_INT = 255;
    private static final float MAX_COLOUR_VALUE = (float)MAX_COLOUR_VALUE_INT;

    private final int nSideDivs = 51;
    private final int nSideDivsSq = nSideDivs * nSideDivs;
    private final float binWidth = MAX_COLOUR_VALUE/nSideDivs;;
    private final int[] colourHistogram = new int[nSideDivs*nSideDivsSq];
    private final int[] testColourHistogram = new int[nSideDivs*nSideDivsSq];

    @Override
    public Window findBestMatch(Bitmap targetImage, Window targetWindow, Bitmap image) {
        fillColourHistogramForWindow(targetImage, targetWindow);

        return findBestMatchWindow(targetImage, targetWindow);
    }

    private void fillColourHistogramForWindow(Bitmap image, Window window) {
        fillColourHistogramForWindow(image, window, colourHistogram);
    }

    private void fillColourHistogramForWindow(Bitmap image, Window window, int[] colourHistogram) {
        Arrays.fill(colourHistogram, 0);

        for (int j=window.yMin ; j<=window.yMax ; j++) {
            for (int i=window.xMin ; i<=window.xMax ; i++) {
                final int rgb = image.getPixel(i, j);
                final int binIndex = findBinIndexForColour(rgb);
                colourHistogram[binIndex]++;
            }
        }
    }

    private int findBinIndexForColour(int rgb) {
        final int rIndex = findSideBinIndex(ColourHelper.getRed(rgb));
        final int gIndex = findSideBinIndex(ColourHelper.getGreen(rgb));
        final int bIndex = findSideBinIndex(ColourHelper.getBlue(rgb));

        return bIndex*nSideDivsSq + gIndex*nSideDivs + rIndex;
    }

    private int findSideBinIndex(int rgbComponent) {
        if (rgbComponent == MAX_COLOUR_VALUE_INT) { // Include the 255 value in the last bin.
            return nSideDivs - 1;
        } else {
            return (int) (rgbComponent/binWidth);
        }
    }

    private Window findBestMatchWindow(Bitmap targetImage, Window targetWindow) {
        int xMin = 0;
        int yMin = 0;
        int minDiff = Integer.MAX_VALUE;
Log.d("TEMP", ">>>>>>>>>>>>>>> Searching for best match [" + targetImage.getWidth() + ", " + targetImage.getHeight() + "]...");
        for (int j=0 ; j<targetImage.getHeight() - targetWindow.height ; j += targetWindow.height) {
Log.d("TEMP", ">>>>>>>>>> row index: " + j);
            for (int i=0 ; i<targetImage.getWidth() - targetWindow.width ; i += targetWindow.width) {
Log.d("TEMP", ">>>>>>> column index: " + i);
                final Window testWindow = new Window(i, j, targetWindow.width, targetWindow.height);
                final int diff = diffColourHistogramForWindow(targetImage, testWindow);
                if (diff < minDiff) {
                    minDiff = diff;
                    xMin = i;
                    yMin = j;
                }
            }
        }
Log.d("TEMP", ">>>>>>>>>>>>>>>>>>> Best match: (" + xMin + ", " + yMin + ")");

        return new Window(xMin, yMin, targetWindow.width, targetWindow.height);
    }

    private int diffColourHistogramForWindow(Bitmap image, Window window) {
        fillColourHistogramForWindow(image, window, testColourHistogram);

        int diff = 0;
        for (int i=0 ; i<colourHistogram.length ; i++) {
            diff += Math.abs(testColourHistogram[i] - colourHistogram[i]);
        }

        return diff;
    }
}
