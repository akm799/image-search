package uk.co.akm.test.imagesearch.process.track.search.impl;


import uk.co.akm.test.imagesearch.process.model.window.Window;
import uk.co.akm.test.imagesearch.process.track.search.BestMatchFinder;
import uk.co.akm.test.imagesearch.process.track.search.impl.map.ColourHistogram;
import uk.co.akm.test.imagesearch.process.track.search.impl.map.PixelMap;

/**
 * This is finder is initialised, in the constructor, with a window that must be close to the target
 * window (i.e. partially overlapping). Then it will use a colour-based mean shift iteration to shift
 * the centre of the initial window as close as possible to the one of the target window.
 *
 * Created by Thanos Mavroidis on 18/02/2021.
 */
public final class MeanShiftBestMatchFinder implements BestMatchFinder {
    private static final int D_PIXEL_TOLERANCE = 0;
    private static final int N_ITERATIONS_MAX = 100;

    private final int[][] weights;
    private final ColourHistogram colourHistogram;

    private Window trackingWindow;

    public MeanShiftBestMatchFinder(ColourHistogram colourHistogram, Window initialTrackingWindow) {
        this.colourHistogram = colourHistogram;
        this.trackingWindow = new Window(initialTrackingWindow);
        this.weights = new int[trackingWindow.height + 1][trackingWindow.width + 1];
    }

    @Override
    public Window findBestMatch(PixelMap image) {
        int n = 0;
        boolean notConverged = true;
        while (notConverged && n < N_ITERATIONS_MAX) {
            notConverged = !shiftCentre(image, trackingWindow);
            n++;
        }

        return trackingWindow;
    }

    private boolean shiftCentre(PixelMap image, Window window) {
        // Calculate the weights.
        int maxWeight = 0;
        for (int j=window.yMin ; j<=window.yMax ; j++) {
            final int wy = j - window.yMin;
            for (int i=window.xMin ; i<=window.xMax ; i++) {
                final int binIndex = image.getPixel(i, j); // Our 'image' pixel value is the colour histogram bin index.
                final int weight = colourHistogram.getValueForBin(binIndex);
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
}
