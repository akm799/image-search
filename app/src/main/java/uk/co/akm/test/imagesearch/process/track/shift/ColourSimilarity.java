package uk.co.akm.test.imagesearch.process.track.shift;

/**
 * Utility class with a method that gives the similarity between two colour histograms (distributions).
 *
 * Created by Thanos Mavroidis on 22/07/2019.
 */
public class ColourSimilarity {
    private static final float MAX_BIN_SIMILARITY = 3f;

    /**
     * Returns a number representing the similarity between the two input colour histograms (distributions). The higher
     * the number the greater the similarity.
     *
     * @param reference the reference colour histogram (distribution)
     * @param histogram the colour histogram (distribution) whose similarity, with the reference one, we seek
     * @return a number representing the similarity between the two input colour histograms (distributions)
     */
    public static float findSimilarity(ColourCubeHistogram reference, ColourCubeHistogram histogram) {
        final float nPoints = reference.nPoints();
        final int side = reference.divisionsInSide();
        final int len = side*side*side;

        float similarity = 0;
        for (int i=0 ; i<len ; i++) {
            final float referenceBinSize = reference.binSize(i);
            if (referenceBinSize > 0) {
                final float weight = referenceBinSize/nPoints;
                final float binDiff = Math.abs(histogram.binSize(i) - referenceBinSize)/referenceBinSize;
                final float binSimilarity = (binDiff == 0 ? MAX_BIN_SIMILARITY : 1/binDiff);
                similarity += weight*binSimilarity;
            }
        }

        return similarity;
    }

    private ColourSimilarity() {}
}
