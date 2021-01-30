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
