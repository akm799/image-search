package uk.co.akm.test.imagesearch.process.track.search.impl;

import android.graphics.Bitmap;

import java.util.Arrays;

import uk.co.akm.test.imagesearch.process.model.window.Window;
import uk.co.akm.test.imagesearch.process.track.search.BestMatchFinder;
import uk.co.akm.test.imagesearch.process.util.ColourHelper;

/**
 * This is finder is initialised, in the constructor, with a window that must be close to the target
 * window (i.e. partially overlapping). Then it will use a colour-based mean shift iteration to shift
 * the centre of the initial window as close as possible to the one of the target window.
 *
 * Created by Thanos Mavroidis on 03/01/2021.
 */
@Deprecated
public final class SlowMeanShiftBestMatchFinder implements BestMatchFinder {
    private static final int MAX_COLOUR_VALUE_INT = 255;
    private static final float MAX_COLOUR_VALUE = (float)MAX_COLOUR_VALUE_INT;
    private static final int D_PIXEL_TOLERANCE = 0;
    private static final int N_ITERATIONS_MAX = 100;

    private final int nSideDivs = 51;
    private final int nSideDivsSq = nSideDivs * nSideDivs;
    private final float binWidth = MAX_COLOUR_VALUE/nSideDivs;
    private final int[] colourHistogram = new int[nSideDivs*nSideDivsSq];

    private int[][] weights;
    private Window trackingWindow;

    public SlowMeanShiftBestMatchFinder(Window initialTrackingWindow) {
        trackingWindow = new Window(initialTrackingWindow);
    }

    @Override
    public Window findBestMatch(Bitmap targetImage, Window targetWindow, Bitmap image) {
        fillColourHistogramForWindow(targetImage, targetWindow);
        weights = new int[trackingWindow.height + 1][trackingWindow.width + 1];

        int n = 0;
        boolean notConverged = true;
        while (notConverged && n < N_ITERATIONS_MAX) {
            notConverged = !shiftCentre(image, trackingWindow);
            n++;
        }

        return trackingWindow;
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

    private boolean shiftCentre(Bitmap image, Window window) {
        // Calculate the weights.
        int maxWeight = 0;
        for (int j=window.yMin ; j<=window.yMax ; j++) {
            final int wy = j - window.yMin;
            for (int i=window.xMin ; i<=window.xMax ; i++) {
                final int rgb = image.getPixel(i, j);
                final int binIndex = findBinIndexForColour(rgb);
                final int weight = colourHistogram[binIndex];
                weights[wy][i - window.xMin] = weight;
                if (weight > maxWeight) {
                    maxWeight = weight;
                }
            }
        }

        // Calculate the weighted sums.
        int xSum = 0;
        int ySum = 0;
        float sumOfWeights = 0;
        final float maxWeightFloat = (float)maxWeight;
        for (int j=window.yMin ; j<=window.yMax ; j++) {
            final int wy = j - window.yMin;
            for (int i=window.xMin ; i<=window.xMax ; i++) {
                final float weight = weights[wy][i - window.xMin]/maxWeightFloat;
                xSum += weight*i;
                ySum += weight*j;
                sumOfWeights += weight;
            }
        }

        final int x = Math.round(xSum/sumOfWeights);
        final int y = Math.round(ySum/sumOfWeights);
        final int dx = x - (window.xMin + window.width/2);
        final int dy = y - (window.yMin + window.height/2);
        this.trackingWindow = new Window(trackingWindow.shift(dx, dy));

        return (Math.abs(dx) <= D_PIXEL_TOLERANCE && Math.abs(dy) <= D_PIXEL_TOLERANCE);
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
