package uk.co.akm.test.imagesearch.process.track.search.impl;


import android.graphics.Bitmap;
import android.graphics.Point;
import android.support.constraint.solver.widgets.Rectangle;

import java.util.Iterator;

import uk.co.akm.test.imagesearch.process.model.window.Window;
import uk.co.akm.test.imagesearch.process.track.search.BestMatchFinder;
import uk.co.akm.test.imagesearch.process.track.search.WindowsIterator;
import uk.co.akm.test.imagesearch.process.track.shift.ColourCubeDifference;
import uk.co.akm.test.imagesearch.process.track.shift.ColourCubeHistogram;
import uk.co.akm.test.imagesearch.process.track.shift.ColourMeanShift;
import uk.co.akm.test.imagesearch.process.track.shift.ColourSimilarity;
import uk.co.akm.test.imagesearch.process.track.shift.MutableColourCubeHistogram;
import uk.co.akm.test.imagesearch.process.track.shift.impl.ColourCubeDifferenceImpl;
import uk.co.akm.test.imagesearch.process.track.shift.impl.MutableColourCubeHistogramImpl;

/**
 * Created by Thanos Mavroidis on 29/07/2019.
 */
public final class BasicBestMatchFinder implements BestMatchFinder {
    private final int nDivsInSide = 51;

    private final int maxIterations = 10;
    private final double convergenceDistance = 1.5;

    private Window trackingWindow;
    private Window bestMatchWindow;
    private ColourCubeHistogram targetColourDistribution;

    @Override
    public Window findBestMatch(Bitmap targetImage, Window targetWindow, Bitmap image) {
        this.targetColourDistribution = buildColourHistogramForWindow(targetImage, targetWindow);
        this.trackingWindow = findMostSimilarWindow(targetWindow, image);
        shiftTowardsTheTargetWindow(image);

        return bestMatchWindow;
    }

    private ColourCubeHistogram buildColourHistogramForWindow(Bitmap image, Window window) {
        final MutableColourCubeHistogram histogram = new MutableColourCubeHistogramImpl(window.width, window.height, nDivsInSide);
        for (int j=window.yMin ; j<=window.yMax ; j++) {
            for (int i=window.xMin ; i<=window.xMax ; i++) {
                final int pixelIndex = (j - window.yMin)*window.width + (i - window.xMin);
                final int rgb = image.getPixel(i, j);
                histogram.add(pixelIndex, rgb);
            }
        }

        return histogram;
    }

    private Window findMostSimilarWindow(Window targetWindow, Bitmap image) {
        float highestSimilarity = -1;
        Window mostSimilarWindow = null;

        final Iterator<Window> iterator = new WindowsIterator(image, targetWindow);
        while (iterator.hasNext()) {
            final Window testWindow = iterator.next();
            final ColourCubeHistogram testHistogram = buildColourHistogramForWindow(image, testWindow);
            final float similarity = ColourSimilarity.findSimilarity(targetColourDistribution, testHistogram);
            if (similarity > highestSimilarity) {
                highestSimilarity = similarity;
                mostSimilarWindow = testWindow;
            }
        }

        return mostSimilarWindow;
    }

    private void shiftTowardsTheTargetWindow(Bitmap image) {
        Point shift = null;
        float highestSimilarity = -1;

        int i = 0;
        while (haveNotConverged(shift) && i < maxIterations) {
            shift = calculateNewBestCentre(image);
            if (shift != null) {
                trackingWindow = new Window(shiftWindow(shift, trackingWindow));
                final ColourCubeHistogram trackingColourDistribution = buildColourHistogramForWindow(image, trackingWindow);
                final float similarity = ColourSimilarity.findSimilarity(targetColourDistribution, trackingColourDistribution);
                if (similarity > highestSimilarity) {
                    highestSimilarity = similarity;
                    bestMatchWindow = trackingWindow;
                }
            }

            i++;
        }

        if (i == maxIterations) {
            System.out.println("Could not converge after " + i + " iterations.");
        }
    }

    private boolean haveNotConverged(Point shift) {
        if (shift == null) {
            return true;
        }

        return (Math.sqrt(shift.x*shift.x + shift.y*shift.y) > convergenceDistance);
    }

    private Point calculateNewBestCentre(Bitmap image) {
        final ColourCubeHistogram trackingColourDistribution = buildColourHistogramForWindow(image, trackingWindow);
        final ColourCubeDifference comparison = new ColourCubeDifferenceImpl(targetColourDistribution, trackingColourDistribution);

        return ColourMeanShift.shift(trackingColourDistribution, comparison);
    }

    private Rectangle shiftWindow(Point shift, Window window) {
        final Rectangle shiftedWindow = new Rectangle();
        shiftedWindow.setBounds(window.xMin + shift.x, window.yMin + shift.y, window.width, window.height);

        return shiftedWindow;
    }
}
