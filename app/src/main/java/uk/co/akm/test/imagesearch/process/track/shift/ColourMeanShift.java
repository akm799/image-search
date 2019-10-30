package uk.co.akm.test.imagesearch.process.track.shift;

import android.graphics.Point;

import uk.co.akm.test.imagesearch.process.model.collections.IntIterator;


/**
 * Basic Mean Shift Algorithm implementation to shift back to an initial window from some shifted input window.
 * 
 * Created by Thanos Mavroidis on 19/05/2019.
 */
public class ColourMeanShift {
    private static final float INFINITY_WEIGHT = 3f;

    /**
     * Returns a point that represents a shift from the input window back towards some initial window. To evaluate this
     * shift a comparison, made between our input window and the initial window, is used.
     * @param window a window representing a shift from some initial window (the shift we want is the reverse shift that
     *               this input window represents, which gets us back to the initial window)
     * @param comparison a comparison between the input window and some initial window
     * @return a point that represents a shift from the input window back towards some initial window
     */
    public static Point shift(ColourCubeHistogram window, ColourCubeDifference comparison) {
        float sumX = 0;
        float sumY = 0;
        float sumWeight = 0;

        final int width = window.imageWidth();
        for (int i=0 ; i<window.nBins() ; i++) {
            if (comparison.hasBinDiff(i)) {
                final float diff = comparison.binDiff(i);
                final float weight = (diff == 0 ? INFINITY_WEIGHT : 1f/diff)*comparison.refBinWeight(i);
                final IntIterator points = window.binPoints(i).iterator();
                while (points.hasNext()) {
                    final int pixelIndex = points.next();
                    sumX += weight*(pixelIndex%width);
                    sumY += weight*(pixelIndex/width);
                    sumWeight += weight;
                }
            }
        }

        final int shiftedX = Math.round(sumX/sumWeight);
        final int shiftedY = Math.round(sumY/sumWeight);

        final int xShift = shiftedX - window.imageWidth()/2;
        final int yShift = shiftedY - window.imageHeight()/2;

        return new Point(xShift, yShift);
    }

    private ColourMeanShift() {}
}
