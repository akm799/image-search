package uk.co.akm.test.imagesearch.process.track.search.impl;


import org.junit.Assert;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Arrays;

import uk.co.akm.test.imagesearch.helper.MockBitmap;
import uk.co.akm.test.imagesearch.helper.MockBitmapFactory;
import uk.co.akm.test.imagesearch.process.model.window.Window;
import uk.co.akm.test.imagesearch.process.track.search.impl.map.ColourHistogram;
import uk.co.akm.test.imagesearch.process.track.search.impl.map.PixelMap;
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

    private static final int DX_INDEX = 0;
    private static final int DY_INDEX = 1;

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
        final int[] colourHistogram = colourHistogramInstance();
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
    public void shouldSerialize() {
        final int width = 10;
        final int height = 10;
        final MockBitmap image = MockBitmapFactory.randomMockBitmapInstance(width, height);
        final Window window = new Window(0, 0, width, height);
        final PixelMap pixelMap = toPixelMap(image);
        final int nBins = nSideDivs * nSideDivsSq;
        final ColourHistogram underTest = new ColourHistogram(nSideDivs);
        underTest.fillColourHistogramForWindow(pixelMap, window);

        final byte[] data = underTest.serialize();
        Assert.assertEquals(4 * nBins, data.length);
        final ColourHistogram deserialized = new ColourHistogram(data);
        Assert.assertEquals(underTest.getNSideDivs(), deserialized.getNSideDivs());
        for (int i=0 ; i<nBins ; i++) {
            Assert.assertEquals(underTest.getValueForBin(i), deserialized.getValueForBin(i));
        }
    }

    @Test
    public void shouldShiftColourHistogramLeft() {
        shiftColourHistogramTest(SHIFT_LEFT);
    }

    @Test
    public void shouldShiftColourHistogramRight() {
        shiftColourHistogramTest(SHIFT_RIGHT);
    }

    @Test
    public void shouldShiftColourHistogramUp() {
        shiftColourHistogramTest(SHIFT_UP);
    }

    @Test
    public void shouldShiftColourHistogramDown() {
        shiftColourHistogramTest(SHIFT_DOWN);
    }

    @Test
    public void shouldShiftColourHistogramLeftUp() {
        shiftColourHistogramTest(SHIFT_LEFT_UP);
    }

    @Test
    public void shouldShiftColourHistogramLeftDown() {
        shiftColourHistogramTest(SHIFT_LEFT_DOWN);
    }

    @Test
    public void shouldShiftColourHistogramRightUp() {
        shiftColourHistogramTest(SHIFT_RIGHT_UP);
    }

    @Test
    public void shouldShiftColourHistogramRightDown() {
        shiftColourHistogramTest(SHIFT_RIGHT_DOWN);
    }

    @Test
    public void shouldDiffColourHistograms() {
        final int width = 50;
        final int height = 50;

        final MockBitmap image1 = MockBitmapFactory.randomMockBitmapInstance(width, height);
        final MockBitmap image2 = MockBitmapFactory.randomMockBitmapInstance(width, height);

        final int[] colourHistogram1 = colourHistogramInstance();
        fillColourHistogramForWindow(image1, new Window(0, 0, width, height), colourHistogram1);

        final int[] colourHistogram2 = colourHistogramInstance();
        fillColourHistogramForWindow(image2, new Window(0, 0, width/2, height/2), colourHistogram2);

        final int diff = diffHistograms(colourHistogram1, colourHistogram2);
        Assert.assertTrue(diff > 0);

        final int diff2 = diffHistogramsMemoryInefficient(colourHistogram1, colourHistogram2);
        Assert.assertEquals(diff, diff2);
    }

    @Test
    public void shouldZeroDiffIdenticalColourHistograms() {
        final int width = 50;
        final int height = 50;
        final MockBitmap image = MockBitmapFactory.randomMockBitmapInstance(width, height);

        final int[] colourHistogram1 = colourHistogramInstance();
        fillColourHistogramForWindow(image, new Window(0, 0, width, height), colourHistogram1);

        final int[] colourHistogram2 = colourHistogramInstance();
        fillColourHistogramForWindow(image, new Window(0, 0, width, height), colourHistogram2);

        final int diff = diffHistograms(colourHistogram1, colourHistogram2);
        Assert.assertEquals(0, diff);

        final int diff2 = diffHistogramsMemoryInefficient(colourHistogram1, colourHistogram2);
        Assert.assertEquals(diff, diff2);
    }

    private void shiftColourHistogramTest(int shiftDirection) {
        final int[] dxy = getShift(shiftDirection);
        final int dx = dxy[DX_INDEX];
        final int dy = dxy[DY_INDEX];

        final int width = 15;
        final int height = 15;
        final MockBitmap image = MockBitmapFactory.randomMockBitmapInstance(width, height);

        final Window window = new Window(7, 7,4, 4);
        final int[] colourHistogram = colourHistogramInstance();
        fillColourHistogramForWindow(image, window, colourHistogram);

        final Window shiftedWindow = new Window(window.xMin + dx, window.yMin + dy, window.width, window.height);
        final int[] expectedColourHistogram = colourHistogramInstance();
        fillColourHistogramForWindow(image, shiftedWindow, expectedColourHistogram);

        fillColourHistogramForShiftedWindow(image, window, shiftDirection, colourHistogram);
        Assert.assertArrayEquals(expectedColourHistogram, colourHistogram);
    }

    private int[] getShift(int shiftDirection) {
        switch (shiftDirection) {
            case SHIFT_LEFT: return new int[]{-1, 0};

            case SHIFT_RIGHT: return new int[]{1, 0};

            case SHIFT_UP: return new int[]{0, -1};

            case SHIFT_DOWN: return new int[]{0, 1};

            case SHIFT_LEFT_UP: return new int[]{-1, -1};

            case SHIFT_LEFT_DOWN: return new int[]{-1, 1};

            case SHIFT_RIGHT_UP: return new int[]{1, -1};

            case SHIFT_RIGHT_DOWN: return new int[]{1, 1};

            default:
                throw new IllegalArgumentException("Unexpected shift direction: " + shiftDirection);
        }
    }

    private void fillColourHistogramForShiftedWindow(MockBitmap image, Window window, int shiftDirection, int[] colourHistogram) {
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

    private void fillColourHistogramForRightShiftedWindow(MockBitmap image, Window window, int[] colourHistogram) {
        final int xNew = window.xMax + 1;
        for (int j=window.yMin ; j<=window.yMax ; j++) {
            subtractFromColourHistogram(image, window.xMin, j, colourHistogram);
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

    private void fillColourHistogramForDownShiftedWindow(MockBitmap image, Window window, int[] colourHistogram) {
        final int yNew = window.yMax + 1;
        for (int i=window.xMin ; i<=window.xMax ; i++) {
            subtractFromColourHistogram(image, i, window.yMin, colourHistogram);
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

    private void fillColourHistogramForLeftDownShiftedWindow(MockBitmap image, Window window, int[] colourHistogram) {
        final int xNew = window.xMin - 1;
        final int yNew = window.yMax + 1;

        // Vertical lines
        final int yMin = window.yMin + 1;
        for (int j=yMin ; j<=window.yMax ; j++) {
            subtractFromColourHistogram(image, window.xMax, j, colourHistogram);
            addToColourHistogram(image, xNew, j, colourHistogram);
        }

        // Horizontal lines
        for (int i=window.xMin ; i<window.xMax ; i++) {
            subtractFromColourHistogram(image, i, window.yMin, colourHistogram);
            addToColourHistogram(image, i, yNew, colourHistogram);
        }

        // Corners (we process corners separately so we do not double-count them)
        subtractFromColourHistogram(image, window.xMax, window.yMin, colourHistogram);
        addToColourHistogram(image, xNew, yNew, colourHistogram);
    }

    private void fillColourHistogramForRightUpShiftedWindow(MockBitmap image, Window window, int[] colourHistogram) {
        final int xNew = window.xMax + 1;
        final int yNew = window.yMin - 1;

        // Vertical lines
        for (int j=window.yMin ; j<window.yMax ; j++) {
            subtractFromColourHistogram(image, window.xMin, j, colourHistogram);
            addToColourHistogram(image, xNew, j, colourHistogram);
        }

        // Horizontal lines
        final int xMin = window.xMin + 1;
        for (int i=xMin ; i<=window.xMax ; i++) {
            subtractFromColourHistogram(image, i, window.yMax, colourHistogram);
            addToColourHistogram(image, i, yNew, colourHistogram);
        }

        // Corners (we process corners separately so we do not double-count them)
        subtractFromColourHistogram(image, window.xMin, window.yMax, colourHistogram);
        addToColourHistogram(image, xNew, yNew, colourHistogram);
    }

    private void fillColourHistogramForRightDownShiftedWindow(MockBitmap image, Window window, int[] colourHistogram) {
        final int xNew = window.xMax + 1;
        final int yNew = window.yMax + 1;

        // Vertical lines
        final int yMin = window.yMin + 1;
        for (int j=yMin ; j<=window.yMax ; j++) {
            subtractFromColourHistogram(image, window.xMin, j, colourHistogram);
            addToColourHistogram(image, xNew, j, colourHistogram);
        }

        // Horizontal lines
        final int xMin = window.xMin + 1;
        for (int i=xMin ; i<=window.xMax ; i++) {
            subtractFromColourHistogram(image, i, window.yMin, colourHistogram);
            addToColourHistogram(image, i, yNew, colourHistogram);
        }

        // Corners (we process corners separately so we do not double-count them)
        subtractFromColourHistogram(image, window.xMin, window.yMin, colourHistogram);
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

    private int diffHistograms(int[] histogram1, int[] histogram2) {
        if (histogram1.length != histogram2.length) {
            throw new IllegalArgumentException("The input histograms do not have equal lengths (" + histogram1.length + " != " + histogram2.length + ").");
        }

        int diff = 0;
        for (int i=0 ; i<histogram1.length ; i++) {
            diff += Math.abs(histogram2[i] - histogram1[i]);
        }

        return diff;
    }

    private int diffHistogramsMemoryInefficient(int[] histogram1, int[] histogram2) {
        int diff = 0;
        final int[] diffHistogram = buildDiffHistogram(histogram1, histogram2);
        for (int i=0 ; i<histogram1.length ; i++) {
            diff += diffHistogram[i];
        }

        return diff;
    }

    private int[] buildDiffHistogram(int[] histogram1, int[] histogram2) {
        if (histogram1.length != histogram2.length) {
            throw new IllegalArgumentException("The input histograms do not have equal lengths (" + histogram1.length + " != " + histogram2.length + ").");
        }

        final int[] diffHistogram = new int[histogram1.length];
        for (int i=0 ; i<histogram1.length ; i++) {
            diffHistogram[i] = Math.abs(histogram2[i] - histogram1[i]);
        }

        return diffHistogram;
    }

    public PixelMap toPixelMap(MockBitmap image) {
        final int[] values = new int[image.getWidth() * image.getHeight()];

        int i = 0;
        for (int y=0 ; y<image.getHeight() ; y++) {
            for (int x=0 ; x<image.getWidth() ; x++) {
                values[i++] = getColourHistogramIndexForPixel(image, x, y);
            }
        }

        return new PixelMap(image.getWidth(), values);
    }

    private int[] colourHistogramInstance() {
        return new int[nSideDivs*nSideDivsSq];
    }
}