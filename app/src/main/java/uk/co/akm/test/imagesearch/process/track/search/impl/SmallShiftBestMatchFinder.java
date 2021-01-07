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
    private static final int[] SHIFT_DIRECTIONS = {SHIFT_LEFT, SHIFT_RIGHT, SHIFT_UP, SHIFT_DOWN, SHIFT_LEFT_UP, SHIFT_LEFT_DOWN, SHIFT_RIGHT_UP, SHIFT_RIGHT_DOWN};
    private static final int SHIFT_NO_DIRECTION = 0;
    private static final ShiftResult SHIFT_NO_IMPROVEMENT = new ShiftResult();

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

    private Window findBestMatchWindow(Bitmap targetImage, Window targetWindow) {
        Window window = targetWindow;
        ShiftResult shiftResult = new ShiftResult(SHIFT_NO_DIRECTION);
        while (shiftResult.improved) {
            shiftResult = diffColourHistogramForShiftedWindows(targetImage, window);
            if (shiftResult.improved) {
                fillColourHistogramForShiftedWindow(targetImage, window, colourHistogram, shiftResult.shiftDirection);
                window = shiftWindow(window, shiftResult.shiftDirection);
            }
        }

        return window;
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
        final int binIndex = getColourHistogramIndexForPixel(image, x, y);
        colourHistogram[binIndex]++;
    }

    private void removeFromColourHistogram(Bitmap image, int x, int y, int[] colourHistogram) {
        final int binIndex = getColourHistogramIndexForPixel(image, x, y);
        colourHistogram[binIndex]--;
    }

    private int findSideBinIndex(int rgbComponent) {
        if (rgbComponent == MAX_COLOUR_VALUE_INT) { // Include the 255 value in the last bin.
            return nSideDivs - 1;
        } else {
            return (int) (rgbComponent/binWidth);
        }
    }

    private ShiftResult diffColourHistogramForShiftedWindows(Bitmap image, Window window) {
        final int unShiftedDiff = diffColourHistogramForWindow(image, window);

        int bestShiftDirection = 0;
        int minDiff = unShiftedDiff;
        for (int shiftDirection : SHIFT_DIRECTIONS) {
            final int diff = diffColourHistogramForShiftedWindow(image, window, shiftDirection, unShiftedDiff);
            if (diff < minDiff) {
                minDiff = diff;
                bestShiftDirection = shiftDirection;
            }
        }

        if (minDiff < unShiftedDiff) {
            return new ShiftResult(bestShiftDirection);
        } else {
            return SHIFT_NO_IMPROVEMENT;
        }
    }

    private int diffColourHistogramForShiftedWindow(Bitmap image, Window window, int shiftDirection, int unShiftedDiff) {
        switch (shiftDirection) {
            case SHIFT_LEFT: return diffColourHistogramForLeftShiftedWindow(image, window, unShiftedDiff);
            case SHIFT_RIGHT: return diffColourHistogramForRightShiftedWindow(image, window, unShiftedDiff);
            case SHIFT_UP: return diffColourHistogramForUpShiftedWindow(image, window, unShiftedDiff);
            case SHIFT_DOWN: return diffColourHistogramForDownShiftedWindow(image, window, unShiftedDiff);
            case SHIFT_LEFT_UP: return diffColourHistogramForLeftUpShiftedWindow(image, window, unShiftedDiff);
            case SHIFT_LEFT_DOWN: return diffColourHistogramForLeftDownShiftedWindow(image, window, unShiftedDiff);
            case SHIFT_RIGHT_UP: return diffColourHistogramForRightUpShiftedWindow(image, window, unShiftedDiff);
            case SHIFT_RIGHT_DOWN: return diffColourHistogramForRightDownShiftedWindow(image, window, unShiftedDiff);

            default: throw new IllegalArgumentException("Illegal shift direction: " + shiftDirection);
        }
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

    private int diffColourHistogramForLeftShiftedWindow(Bitmap image, Window window, int unShiftedDiff) {
        int diff = unShiftedDiff;
        final int newXMin = window.xMin - 1;
        for (int j=window.yMin ; j<=window.yMax ; j++) {
            diff -= getColourHistogramDifferenceForPixel(image, window.xMax, j);
            diff += getColourHistogramDifferenceForPixel(image, newXMin, j);
        }

        return diff;
    }

    private int diffColourHistogramForRightShiftedWindow(Bitmap image, Window window, int unShiftedDiff) {
        int diff = unShiftedDiff;
        final int newXMax = window.xMax + 1;
        for (int j=window.yMin ; j<=window.yMax ; j++) {
            diff -= getColourHistogramDifferenceForPixel(image, window.xMin, j);
            diff += getColourHistogramDifferenceForPixel(image, newXMax, j);
        }

        return diff;
    }

    private int diffColourHistogramForUpShiftedWindow(Bitmap image, Window window, int unShiftedDiff) {
        int diff = unShiftedDiff;
        final int newYMin = window.yMin - 1;
        for (int i=window.xMin ; i<=window.xMax ; i++) {
            diff -= getColourHistogramDifferenceForPixel(image, i, window.yMax);
            diff += getColourHistogramDifferenceForPixel(image, i, newYMin);
        }

        return diff;
    }

    private int diffColourHistogramForDownShiftedWindow(Bitmap image, Window window, int unShiftedDiff) {
        int diff = unShiftedDiff;
        final int newYMax = window.yMax + 1;
        for (int i=window.xMin ; i<=window.xMax ; i++) {
            diff -= getColourHistogramDifferenceForPixel(image, i, window.yMin);
            diff += getColourHistogramDifferenceForPixel(image, i, newYMax);
        }

        return diff;
    }

    private int diffColourHistogramForLeftUpShiftedWindow(Bitmap image, Window window, int unShiftedDiff) {
        final int newXMin = window.xMin - 1;
        final int newXMax = window.xMax - 1;
        final int newYMin = window.yMin - 1;
        final int newYMax = window.yMax - 1;

        return diffColourHistogramForDiagonallyShiftedWindow(image, window, newXMin, newYMin, newXMax, newYMax, unShiftedDiff);
    }

    private int diffColourHistogramForLeftDownShiftedWindow(Bitmap image, Window window, int unShiftedDiff) {
        final int newXMin = window.xMin - 1;
        final int newXMax = window.xMax - 1;
        final int newYMin = window.yMin + 1;
        final int newYMax = window.yMax + 1;

        return diffColourHistogramForDiagonallyShiftedWindow(image, window, newXMin, newYMin, newXMax, newYMax, unShiftedDiff);
    }

    private int diffColourHistogramForRightUpShiftedWindow(Bitmap image, Window window, int unShiftedDiff) {
        final int newXMin = window.xMin + 1;
        final int newXMax = window.xMax + 1;
        final int newYMin = window.yMin - 1;
        final int newYMax = window.yMax - 1;

        return diffColourHistogramForDiagonallyShiftedWindow(image, window, newXMin, newYMin, newXMax, newYMax, unShiftedDiff);
    }

    private int diffColourHistogramForRightDownShiftedWindow(Bitmap image, Window window, int unShiftedDiff) {
        final int newXMin = window.xMin + 1;
        final int newXMax = window.xMax + 1;
        final int newYMin = window.yMin + 1;
        final int newYMax = window.yMax + 1;

        return diffColourHistogramForDiagonallyShiftedWindow(image, window, newXMin, newYMin, newXMax, newYMax, unShiftedDiff);
    }

    private int diffColourHistogramForDiagonallyShiftedWindow(Bitmap image, Window window, int newXMin, int newYMin, int newXMax, int newYMax, int unShiftedDiff) {
        final int oldDiff = diffOldBorderLinesFromColourHistogram(image, window);
        final int newDiff = diffNewBorderLinesFromColourHistogram(image, newXMin, newYMin, newXMax, newYMax);

        return unShiftedDiff - oldDiff + newDiff;
    }

    private int diffNewBorderLinesFromColourHistogram(Bitmap image, int newXMin, int newYMin, int newXMax, int newYMax) {
        int diff = 0;

        // New vertical border lines
        for (int j=newYMin ; j<=newYMax ; j++) {
            diff += getColourHistogramDifferenceForPixel(image, newXMin, j) + getColourHistogramDifferenceForPixel(image, newXMax, j);
        }

        // New horizontal border lines
        for (int i=newXMin ; i<=newXMax ; i++) {
            diff += getColourHistogramDifferenceForPixel(image, i, newYMin) + getColourHistogramDifferenceForPixel(image, i, newYMax);
        }

        return diff;
    }

    private int diffOldBorderLinesFromColourHistogram(Bitmap image, Window window) {
        int diff = 0;

        // Old vertical lines
        for (int j=window.yMin ; j<=window.yMax ; j++) {
            diff += getColourHistogramDifferenceForPixel(image, window.xMin, j) + getColourHistogramDifferenceForPixel(image, window.xMax, j);
        }

        // Old horizontal lines
        for (int i=window.xMin ; i<=window.xMax ; i++) {
            diff += getColourHistogramDifferenceForPixel(image, i, window.yMin) + getColourHistogramDifferenceForPixel(image, i, window.yMax);
        }

        return diff;
    }

    private int getColourHistogramDifferenceForPixel(Bitmap image, int x, int y) {
        final int index = getColourHistogramIndexForPixel(image, x, y);

        return Math.abs(testColourHistogram[index] - colourHistogram[index]);
    }

    private int getColourHistogramIndexForPixel(Bitmap image, int x, int y) {
        final int rgb = image.getPixel(x, y);

        return findBinIndexForColour(rgb);
    }

    private int findBinIndexForColour(int rgb) {
        final int rIndex = findSideBinIndex(ColourHelper.getRed(rgb));
        final int gIndex = findSideBinIndex(ColourHelper.getGreen(rgb));
        final int bIndex = findSideBinIndex(ColourHelper.getBlue(rgb));

        return bIndex*nSideDivsSq + gIndex*nSideDivs + rIndex;
    }

    private Window shiftWindow(Window window, int shiftDirection) {
        int dx, dy;

        switch (shiftDirection) {
            case SHIFT_LEFT:
                dx = -1;
                dy = 0;
                break;

            case SHIFT_RIGHT:
                dx = 1;
                dy = 0;
                break;

            case SHIFT_UP:
                dx = 0;
                dy = -1;
                break;

            case SHIFT_DOWN:
                dx = 0;
                dy = 1;
                break;

            case SHIFT_LEFT_UP:
                dx = -1;
                dy = -1;
                break;

            case SHIFT_LEFT_DOWN:
                dx = -1;
                dy = 1;
                break;

            case SHIFT_RIGHT_UP:
                dx = 1;
                dy = -1;
                break;

            case SHIFT_RIGHT_DOWN:
                dx = 1;
                dy = 1;
                break;

            default: throw new IllegalArgumentException("Illegal shift direction: " + shiftDirection);
        }

        return window.shift(dx, dy);
    }

    private static class ShiftResult {
        public final boolean improved;
        public final int shiftDirection;

        ShiftResult() {
            improved = false;
            shiftDirection = 0;
        }

        ShiftResult(int shiftDirection) {
            this.improved = true;
            this.shiftDirection = shiftDirection;
        }
    }
}

