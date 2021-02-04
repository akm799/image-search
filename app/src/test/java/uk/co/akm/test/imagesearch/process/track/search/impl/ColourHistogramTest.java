package uk.co.akm.test.imagesearch.process.track.search.impl;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

import uk.co.akm.test.imagesearch.helper.MockBitmap;
import uk.co.akm.test.imagesearch.helper.MockBitmapFactory;
import uk.co.akm.test.imagesearch.process.model.window.Window;
import uk.co.akm.test.imagesearch.process.util.ColourHelper;

public class ColourHistogramTest {
    private static final int MAX_COLOUR_VALUE_INT = 255;
    private static final float MAX_COLOUR_VALUE = (float)MAX_COLOUR_VALUE_INT;

    private static final int SHIFT_LEFT = 1;
    private static final int SHIFT_RIGHT = 2;
    private static final int SHIFT_UP = 3;
    private static final int SHIFT_DOWN = 4;
    private static final int SHIFT_LEFT_UP = 5;
    private static final int SHIFT_LEFT_DOWN = 6;
    private static final int SHIFT_RIGHT_UP = 7;
    private static final int SHIFT_RIGHT_DOWN = 8;

    private final int nSideDivs = 51;
    private final int nSideDivsSq = nSideDivs * nSideDivs;
    private final float binWidth = MAX_COLOUR_VALUE/nSideDivs;

    @Test
    public void shouldComputeBinIndex() {
        Assert.assertEquals(nSideDivs - 1, findBinIndexForColour(0xFFFF0000));
        Assert.assertEquals(nSideDivs*(nSideDivs -1), findBinIndexForColour(0xFF00FF00));
        Assert.assertEquals(nSideDivs*nSideDivs*(nSideDivs -1), findBinIndexForColour(0xFF0000FF));
    }

    @Test
    public void shouldFillColourHistogram() {
        final int width = 10;
        final int height = 10;
        final MockBitmap image = MockBitmapFactory.randomMockBitmapInstance(width, height);
        final Window window = new Window(0, 0, width, height);
        final int[] colourHistogram = new int[nSideDivs*nSideDivsSq];
        fillColourHistogramForWindow(image, window, colourHistogram);

        // Check that histogram is not empty.
        int nNonZero = 0;
        for (int n : colourHistogram) {
            if (n > 0) {
                nNonZero++;
            }
        }
        Assert.assertTrue(nNonZero > 0);

        // Check that histogram was filled correctly
        for (int y=0 ; y<height ; y++) {
            for (int x=0 ; x<width ; x++) {
                final int rgb = image.getPixel(x, y);
                final int ri = findSideBinIndex(ColourHelper.getRed(rgb));
                final int gi = findSideBinIndex(ColourHelper.getGreen(rgb));
                final int bi = findSideBinIndex(ColourHelper.getBlue(rgb));
                final int binIndex = bi*nSideDivsSq + gi*nSideDivs + ri;
                colourHistogram[binIndex]--;
            }
        }
        for (int n : colourHistogram) {
            Assert.assertEquals(0, n);
        }
    }

    @Test
    public void shouldShiftColourHistogramLeft() {
        final int width = 10;
        final int height = 10;
        final MockBitmap image = MockBitmapFactory.randomMockBitmapInstance(width, height);

        final Window window = new Window(5, 5,4, 4);
        final int[] colourHistogram = new int[nSideDivs*nSideDivsSq];
        fillColourHistogramForWindow(image, window, colourHistogram);

        final Window leftShiftedWindow = new Window(window.xMin - 1, window.yMin, window.width, window.height);
        final int[] expectedColourHistogram = new int[nSideDivs*nSideDivsSq];
        fillColourHistogramForWindow(image, leftShiftedWindow, expectedColourHistogram);

        fillColourHistogramForShiftedWindow(image, window, SHIFT_LEFT, colourHistogram);
        Assert.assertArrayEquals(expectedColourHistogram, colourHistogram);
    }

    @Test
    public void shouldShiftColourHistogramUp() {
        final int width = 10;
        final int height = 10;
        final MockBitmap image = MockBitmapFactory.randomMockBitmapInstance(width, height);

        final Window window = new Window(5, 5,4, 4);
        final int[] colourHistogram = new int[nSideDivs*nSideDivsSq];
        fillColourHistogramForWindow(image, window, colourHistogram);

        final Window upShiftedWindow = new Window(window.xMin, window.yMin - 1, window.width, window.height);
        final int[] expectedColourHistogram = new int[nSideDivs*nSideDivsSq];
        fillColourHistogramForWindow(image, upShiftedWindow, expectedColourHistogram);

        fillColourHistogramForShiftedWindow(image, window, SHIFT_UP, colourHistogram);
        Assert.assertArrayEquals(expectedColourHistogram, colourHistogram);
    }

    @Test
    public void shouldShiftColourHistogramLeftUp() {
        final int width = 10;
        final int height = 10;
        final MockBitmap image = MockBitmapFactory.randomMockBitmapInstance(width, height);

        final Window window = new Window(5, 5,4, 4);
        final int[] colourHistogram = new int[nSideDivs*nSideDivsSq];
        fillColourHistogramForWindow(image, window, colourHistogram);

        final Window leftUpShiftedWindow = new Window(window.xMin - 1, window.yMin - 1, window.width, window.height);
        final int[] expectedColourHistogram = new int[nSideDivs*nSideDivsSq];
        fillColourHistogramForWindow(image, leftUpShiftedWindow, expectedColourHistogram);

        fillColourHistogramForShiftedWindow(image, window, SHIFT_LEFT_UP, colourHistogram);
        Assert.assertArrayEquals(expectedColourHistogram, colourHistogram);
    }

    private void fillColourHistogramForShiftedWindow(MockBitmap image, Window window, int shiftDirection, int[] colourHistogram) {
        switch (shiftDirection) {
            case SHIFT_LEFT:
                fillColourHistogramForLeftShiftedWindow(image, window, colourHistogram);
                break;

            case SHIFT_UP:
                fillColourHistogramForUpShiftedWindow(image, window, colourHistogram);
                break;

            case SHIFT_LEFT_UP:
                fillColourHistogramForLeftUpShiftedWindow(image, window, colourHistogram);
                break;

            default:
                throw new IllegalArgumentException("Unexpected shift direction: " + shiftDirection);
        }
    }

    private void fillColourHistogramForLeftShiftedWindow(MockBitmap image, Window window, int[] colourHistogram) {
        final int xNew = window.xMin - 1;
        for (int j=window.yMin ; j<=window.yMax ; j++) {
            subtractFromColourHistogram(image, window.xMax, j, colourHistogram);
            addToColourHistogram(image, xNew, j, colourHistogram);
        }
    }

    private void fillColourHistogramForUpShiftedWindow(MockBitmap image, Window window, int[] colourHistogram) {
        final int yNew = window.yMin - 1;
        for (int i=window.xMin ; i<=window.xMax ; i++) {
            subtractFromColourHistogram(image, i, window.yMax, colourHistogram);
            addToColourHistogram(image, i, yNew, colourHistogram);
        }
    }

    private void fillColourHistogramForLeftUpShiftedWindow(MockBitmap image, Window window, int[] colourHistogram) {
        final int xNew = window.xMin - 1;
        final int yNew = window.yMin - 1;

        // Vertical lines
        for (int j=window.yMin ; j<window.yMax ; j++) {
            subtractFromColourHistogram(image, window.xMax, j, colourHistogram);
            addToColourHistogram(image, xNew, j, colourHistogram);
        }

        // Horizontal lines
        for (int i=window.xMin ; i<window.xMax ; i++) {
            subtractFromColourHistogram(image, i, window.yMax, colourHistogram);
            addToColourHistogram(image, i, yNew, colourHistogram);
        }

        // Corners (we process corners separately so we do not double-count them)
        subtractFromColourHistogram(image, window.xMax, window.yMax, colourHistogram);
        addToColourHistogram(image, xNew, yNew, colourHistogram);
    }

    private void fillColourHistogramForWindow(MockBitmap image, Window window, int[] colourHistogram) {
        Arrays.fill(colourHistogram, 0);

        for (int j=window.yMin ; j<=window.yMax ; j++) {
            for (int i=window.xMin ; i<=window.xMax ; i++) {
                addToColourHistogram(image, i, j, colourHistogram);
            }
        }
    }

    private void addToColourHistogram(MockBitmap image, int x, int y, int[] colourHistogram) {
        final int binIndex = getColourHistogramIndexForPixel(image, x, y);
        colourHistogram[binIndex]++;
    }

    private void subtractFromColourHistogram(MockBitmap image, int x, int y, int[] colourHistogram) {
        final int binIndex = getColourHistogramIndexForPixel(image, x, y);
        colourHistogram[binIndex]--;
    }

    private int getColourHistogramIndexForPixel(MockBitmap image, int x, int y) {
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
