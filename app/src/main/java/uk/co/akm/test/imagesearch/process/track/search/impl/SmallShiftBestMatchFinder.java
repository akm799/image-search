package uk.co.akm.test.imagesearch.process.track.search.impl;


import android.graphics.Bitmap;

import java.util.Arrays;

import uk.co.akm.test.imagesearch.process.model.window.Window;
import uk.co.akm.test.imagesearch.process.track.search.BestMatchFinder;
import uk.co.akm.test.imagesearch.process.util.ColourHelper;

/**
 * Created by Thanos Mavroidis on 04/01/2021.
 */
public final class SmallShiftBestMatchFinder implements BestMatchFinder {
    private static final int SHIFT_LEFT = 1;
    private static final int SHIFT_RIGHT = 2;
    private static final int SHIFT_UP = 3;
    private static final int SHIFT_DOWN = 4;
    private static final int SHIFT_LEFT_UP = 5;
    private static final int SHIFT_LEFT_DOWN = 6;
    private static final int SHIFT_RIGHT_UP = 7;
    private static final int SHIFT_RIGHT_DOWN = 8;

    private static final int MAX_COLOUR_VALUE_INT = 255;
    private static final float MAX_COLOUR_VALUE = (float)MAX_COLOUR_VALUE_INT;

    private final int nSideDivs = 51;
    private final int nSideDivsSq = nSideDivs * nSideDivs;
    private final float binWidth = MAX_COLOUR_VALUE/nSideDivs;
    private final int[] colourHistogram = new int[nSideDivs*nSideDivsSq];
    private final int[] testColourHistogram = new int[nSideDivs*nSideDivsSq];

    private Window trackingWindow;

    public SmallShiftBestMatchFinder(Window initialWindow) {
        trackingWindow = new Window(initialWindow);
    }

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
                addToColourHistogram(image, i, j, colourHistogram);
            }
        }
    }

    private void fillColourHistogramForShiftedWindow(Bitmap image, Window window, int[] colourHistogram, int shiftDirection) {
        switch (shiftDirection) {
            case SHIFT_LEFT:
                fillColourHistogramForLeftShiftedWindow(image, window, colourHistogram);
                break;

            case SHIFT_RIGHT:
                fillColourHistogramForRightShiftedWindow(image, window, colourHistogram);
                break;

            case SHIFT_UP:
                fillColourHistogramForUpShiftedWindow(image, window, colourHistogram);
                break;

            case SHIFT_DOWN:
                fillColourHistogramForDownShiftedWindow(image, window, colourHistogram);
                break;

            case SHIFT_LEFT_UP:
                fillColourHistogramForLeftUpShiftedWindow(image, window, colourHistogram);
                break;

            case SHIFT_LEFT_DOWN:
                fillColourHistogramForLeftDownShiftedWindow(image, window, colourHistogram);
                break;

            case SHIFT_RIGHT_UP:
                fillColourHistogramForRightUpShiftedWindow(image, window, colourHistogram);
                break;

            case SHIFT_RIGHT_DOWN:
                fillColourHistogramForRightDownShiftedWindow(image, window, colourHistogram);
                break;

            default:
                throw new IllegalArgumentException("Illegal shift direction: " + shiftDirection);
        }
    }

    private void fillColourHistogramForLeftShiftedWindow(Bitmap image, Window window, int[] colourHistogram) {
        if (window.xMin == 0) {
            return;
        }

        final int newXMin = window.xMin - 1;
        for (int j=window.yMin ; j<=window.yMax ; j++) {
            removeFromColourHistogram(image, window.xMax, j, colourHistogram);
            addToColourHistogram(image, newXMin, j, colourHistogram);
        }
    }

    private void fillColourHistogramForRightShiftedWindow(Bitmap image, Window window, int[] colourHistogram) {
        if (window.xMax == image.getWidth() - 1) {
            return;
        }

        final int newXMax = window.xMax + 1;
        for (int j=window.yMin ; j<=window.yMax ; j++) {
            removeFromColourHistogram(image, window.xMin, j, colourHistogram);
            addToColourHistogram(image, newXMax, j, colourHistogram);
        }
    }

    private void fillColourHistogramForUpShiftedWindow(Bitmap image, Window window, int[] colourHistogram) {
        if (window.yMin == 0) {
            return;
        }

        final int newYMin = window.yMin - 1;
        for (int i=window.xMin ; i<=window.xMax ; i++) {
            removeFromColourHistogram(image, i, window.yMax, colourHistogram);
            addToColourHistogram(image, i, newYMin, colourHistogram);
        }
    }

    private void fillColourHistogramForDownShiftedWindow(Bitmap image, Window window, int[] colourHistogram) {
        if (window.yMax == image.getHeight() -1) {
            return;
        }

        final int newYMax = window.yMax + 1;
        for (int i=window.xMin ; i<=window.xMax ; i++) {
            removeFromColourHistogram(image, i, window.yMin, colourHistogram);
            addToColourHistogram(image, i, newYMax, colourHistogram);
        }
    }

    private void fillColourHistogramForLeftUpShiftedWindow(Bitmap image, Window window, int[] colourHistogram) {
        if (window.xMin == 0 || window.yMin == 0) {
            return;
        }

        final int newXMin = window.xMin - 1;
        final int newXMax = window.xMax - 1;
        final int newYMin = window.yMin - 1;
        final int newYMax = window.yMax - 1;
        fillColourHistogramForDiagonallyShiftedWindow(image, window, newXMin, newYMin, newXMax, newYMax, colourHistogram);
    }

    private void fillColourHistogramForLeftDownShiftedWindow(Bitmap image, Window window, int[] colourHistogram) {
        if (window.xMin == 0 || window.yMax == image.getHeight() - 1) {
            return;
        }

        final int newXMin = window.xMin - 1;
        final int newXMax = window.xMax - 1;
        final int newYMin = window.yMin + 1;
        final int newYMax = window.yMax + 1;
        fillColourHistogramForDiagonallyShiftedWindow(image, window, newXMin, newYMin, newXMax, newYMax, colourHistogram);
    }

    private void fillColourHistogramForRightUpShiftedWindow(Bitmap image, Window window, int[] colourHistogram) {
        if (window.xMax == image.getWidth() - 1 || window.yMin == 0) {
            return;
        }

        final int newXMin = window.xMin + 1;
        final int newXMax = window.xMax + 1;
        final int newYMin = window.yMin - 1;
        final int newYMax = window.yMax - 1;
        fillColourHistogramForDiagonallyShiftedWindow(image, window, newXMin, newYMin, newXMax, newYMax, colourHistogram);
    }

    private void fillColourHistogramForRightDownShiftedWindow(Bitmap image, Window window, int[] colourHistogram) {
        if (window.xMax == image.getWidth() - 1 || window.yMax == image.getHeight() - 1) {
            return;
        }

        final int newXMin = window.xMin + 1;
        final int newXMax = window.xMax + 1;
        final int newYMin = window.yMin + 1;
        final int newYMax = window.yMax + 1;
        fillColourHistogramForDiagonallyShiftedWindow(image, window, newXMin, newYMin, newXMax, newYMax, colourHistogram);
    }

    private void fillColourHistogramForDiagonallyShiftedWindow(Bitmap image, Window window, int newXMin, int newYMin, int newXMax, int newYMax, int[] colourHistogram) {
        removeBorderLinesFromColourHistogram(image, window, colourHistogram);
        addNewBorderLinesFromColourHistogram(image, newXMin, newYMin, newXMax, newYMax, colourHistogram);
    }

    private void addNewBorderLinesFromColourHistogram(Bitmap image, int newXMin, int newYMin, int newXMax, int newYMax, int[] colourHistogram) {
        // New vertical border lines
        for (int j=newYMin ; j<=newYMax ; j++) {
            addToColourHistogram(image, newXMin, j, colourHistogram);
            addToColourHistogram(image, newXMax, j, colourHistogram);
        }

        // New horizontal border lines
        for (int i=newXMin ; i<=newXMax ; i++) {
            addToColourHistogram(image, i, newYMin, colourHistogram);
            addToColourHistogram(image, i, newYMax, colourHistogram);
        }
    }

    private void removeBorderLinesFromColourHistogram(Bitmap image, Window window, int[] colourHistogram) {
        // Old vertical lines
        for (int j=window.yMin ; j<=window.yMax ; j++) {
            removeFromColourHistogram(image, window.xMin, j, colourHistogram);
            removeFromColourHistogram(image, window.xMax, j, colourHistogram);
        }

        // Old horizontal lines
        for (int i=window.xMin ; i<=window.xMax ; i++) {
            removeFromColourHistogram(image, i, window.yMin, colourHistogram);
            removeFromColourHistogram(image, i, window.yMax, colourHistogram);
        }
    }

    private void addToColourHistogram(Bitmap image, int x, int y, int[] colourHistogram) {
        final int rgb = image.getPixel(x, y);
        final int binIndex = findBinIndexForColour(rgb);
        colourHistogram[binIndex]++;
    }

    private void removeFromColourHistogram(Bitmap image, int x, int y, int[] colourHistogram) {
        final int rgb = image.getPixel(x, y);
        final int binIndex = findBinIndexForColour(rgb);
        colourHistogram[binIndex]--;
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
        //TODO
        return null;
    }

    private int diffColourHistogramForWindow(Bitmap image, Window window) {
        fillColourHistogramForWindow(image, window, testColourHistogram);

        return diffColourHistograms(testColourHistogram, colourHistogram);
    }

    private int diffColourHistograms(int[] testColourHistogram, int[] colourHistogram) {
        int diff = 0;
        for (int i=0 ; i<colourHistogram.length ; i++) {
            diff += Math.abs(testColourHistogram[i] - colourHistogram[i]);
        }

        return diff;
    }
}
