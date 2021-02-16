package uk.co.akm.test.imagesearch.process.track.search.impl;

import android.graphics.Bitmap;

import java.util.Arrays;

import uk.co.akm.test.imagesearch.process.model.window.Window;
import uk.co.akm.test.imagesearch.process.util.ColourHelper;

final class ColourHistogram {
    private static final int MAX_COLOUR_VALUE_INT = 255;
    private static final float MAX_COLOUR_VALUE = (float)MAX_COLOUR_VALUE_INT;

    static final int SHIFT_LEFT = 1;
    static final int SHIFT_RIGHT = 2;
    static final int SHIFT_UP = 3;
    static final int SHIFT_DOWN = 4;
    static final int SHIFT_LEFT_UP = 5;
    static final int SHIFT_LEFT_DOWN = 6;
    static final int SHIFT_RIGHT_UP = 7;
    static final int SHIFT_RIGHT_DOWN = 8;
    static final int[] SHIFT_DIRECTIONS = {SHIFT_LEFT, SHIFT_RIGHT, SHIFT_UP, SHIFT_DOWN, SHIFT_LEFT_UP, SHIFT_LEFT_DOWN, SHIFT_RIGHT_UP, SHIFT_RIGHT_DOWN};

    private final int nSideDivs;
    private final int nSideDivsSq;
    private final float binWidth;
    private final int[] bins;

    ColourHistogram(int nSideDivs) {
        this.nSideDivs = nSideDivs;
        this.nSideDivsSq = nSideDivs * nSideDivs;
        this.binWidth = MAX_COLOUR_VALUE / nSideDivs;
        this.bins = new int[nSideDivs * nSideDivsSq];
    }

    ColourHistogram(ColourHistogram data) {
        this(data.nSideDivs);

        System.arraycopy(data.bins, 0, bins, 0, bins.length);
    }

    int diff(ColourHistogram other) {
        if (nSideDivs != other.nSideDivs) {
            throw new IllegalArgumentException("Cannot 'diff' unequal colour histograms: this.nSideDivs=" + nSideDivs + " but other.nSideDivs=" + other.nSideDivs);
        }

        int diff = 0;
        for (int i=0 ; i<bins.length ; i++) {
            diff += Math.abs(bins[i] - other.bins[i]);
        }

        return diff;
    }

    void fillColourHistogramForWindow(Bitmap image, Window window) {
        Arrays.fill(bins, 0);

        for (int j=window.yMin ; j<=window.yMax ; j++) {
            for (int i=window.xMin ; i<=window.xMax ; i++) {
                addToColourHistogram(image, i, j);
            }
        }
    }

    void fillColourHistogramForShiftedWindow(Bitmap image, Window window, int shiftDirection) {
        switch (shiftDirection) {
            case SHIFT_LEFT:
                fillColourHistogramForLeftShiftedWindow(image, window);
                break;

            case SHIFT_RIGHT:
                fillColourHistogramForRightShiftedWindow(image, window);
                break;

            case SHIFT_UP:
                fillColourHistogramForUpShiftedWindow(image, window);
                break;

            case SHIFT_DOWN:
                fillColourHistogramForDownShiftedWindow(image, window);
                break;

            case SHIFT_LEFT_UP:
                fillColourHistogramForLeftUpShiftedWindow(image, window);
                break;

            case SHIFT_LEFT_DOWN:
                fillColourHistogramForLeftDownShiftedWindow(image, window);
                break;

            case SHIFT_RIGHT_UP:
                fillColourHistogramForRightUpShiftedWindow(image, window);
                break;

            case SHIFT_RIGHT_DOWN:
                fillColourHistogramForRightDownShiftedWindow(image, window);
                break;

            default:
                throw new IllegalArgumentException("Unexpected shift direction: " + shiftDirection);
        }
    }

    private void fillColourHistogramForLeftShiftedWindow(Bitmap image, Window window) {
        final int xNew = window.xMin - 1;
        for (int j=window.yMin ; j<=window.yMax ; j++) {
            subtractFromColourHistogram(image, window.xMax, j);
            addToColourHistogram(image, xNew, j);
        }
    }

    private void fillColourHistogramForRightShiftedWindow(Bitmap image, Window window) {
        final int xNew = window.xMax + 1;
        for (int j=window.yMin ; j<=window.yMax ; j++) {
            subtractFromColourHistogram(image, window.xMin, j);
            addToColourHistogram(image, xNew, j);
        }
    }

    private void fillColourHistogramForUpShiftedWindow(Bitmap image, Window window) {
        final int yNew = window.yMin - 1;
        for (int i=window.xMin ; i<=window.xMax ; i++) {
            subtractFromColourHistogram(image, i, window.yMax);
            addToColourHistogram(image, i, yNew);
        }
    }

    private void fillColourHistogramForDownShiftedWindow(Bitmap image, Window window) {
        final int yNew = window.yMax + 1;
        for (int i=window.xMin ; i<=window.xMax ; i++) {
            subtractFromColourHistogram(image, i, window.yMin);
            addToColourHistogram(image, i, yNew);
        }
    }

    private void fillColourHistogramForLeftUpShiftedWindow(Bitmap image, Window window) {
        final int xNew = window.xMin - 1;
        final int yNew = window.yMin - 1;

        // Vertical lines
        for (int j=window.yMin ; j<window.yMax ; j++) {
            subtractFromColourHistogram(image, window.xMax, j);
            addToColourHistogram(image, xNew, j);
        }

        // Horizontal lines
        for (int i=window.xMin ; i<window.xMax ; i++) {
            subtractFromColourHistogram(image, i, window.yMax);
            addToColourHistogram(image, i, yNew);
        }

        // Corners (we process corners separately so we do not double-count them)
        subtractFromColourHistogram(image, window.xMax, window.yMax);
        addToColourHistogram(image, xNew, yNew);
    }

    private void fillColourHistogramForLeftDownShiftedWindow(Bitmap image, Window window) {
        final int xNew = window.xMin - 1;
        final int yNew = window.yMax + 1;

        // Vertical lines
        final int yMin = window.yMin + 1;
        for (int j=yMin ; j<=window.yMax ; j++) {
            subtractFromColourHistogram(image, window.xMax, j);
            addToColourHistogram(image, xNew, j);
        }

        // Horizontal lines
        for (int i=window.xMin ; i<window.xMax ; i++) {
            subtractFromColourHistogram(image, i, window.yMin);
            addToColourHistogram(image, i, yNew);
        }

        // Corners (we process corners separately so we do not double-count them)
        subtractFromColourHistogram(image, window.xMax, window.yMin);
        addToColourHistogram(image, xNew, yNew);
    }

    private void fillColourHistogramForRightUpShiftedWindow(Bitmap image, Window window) {
        final int xNew = window.xMax + 1;
        final int yNew = window.yMin - 1;

        // Vertical lines
        for (int j=window.yMin ; j<window.yMax ; j++) {
            subtractFromColourHistogram(image, window.xMin, j);
            addToColourHistogram(image, xNew, j);
        }

        // Horizontal lines
        final int xMin = window.xMin + 1;
        for (int i=xMin ; i<=window.xMax ; i++) {
            subtractFromColourHistogram(image, i, window.yMax);
            addToColourHistogram(image, i, yNew);
        }

        // Corners (we process corners separately so we do not double-count them)
        subtractFromColourHistogram(image, window.xMin, window.yMax);
        addToColourHistogram(image, xNew, yNew);
    }

    private void fillColourHistogramForRightDownShiftedWindow(Bitmap image, Window window) {
        final int xNew = window.xMax + 1;
        final int yNew = window.yMax + 1;

        // Vertical lines
        final int yMin = window.yMin + 1;
        for (int j=yMin ; j<=window.yMax ; j++) {
            subtractFromColourHistogram(image, window.xMin, j);
            addToColourHistogram(image, xNew, j);
        }

        // Horizontal lines
        final int xMin = window.xMin + 1;
        for (int i=xMin ; i<=window.xMax ; i++) {
            subtractFromColourHistogram(image, i, window.yMin);
            addToColourHistogram(image, i, yNew);
        }

        // Corners (we process corners separately so we do not double-count them)
        subtractFromColourHistogram(image, window.xMin, window.yMin);
        addToColourHistogram(image, xNew, yNew);
    }

    private void addToColourHistogram(Bitmap image, int x, int y) {
        final int binIndex = getColourHistogramIndexForPixel(image, x, y);
        bins[binIndex]++;
    }

    private void subtractFromColourHistogram(Bitmap image, int x, int y) {
        final int binIndex = getColourHistogramIndexForPixel(image, x, y);
        bins[binIndex]--;
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

    private int findSideBinIndex(int rgbComponent) {
        if (rgbComponent == MAX_COLOUR_VALUE_INT) { // Include the 255 value in the last bin.
            return nSideDivs - 1;
        } else {
            return (int) (rgbComponent/binWidth);
        }
    }
}
