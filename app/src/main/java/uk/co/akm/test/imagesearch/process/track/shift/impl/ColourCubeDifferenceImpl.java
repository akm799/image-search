package uk.co.akm.test.imagesearch.process.track.shift.impl;


import uk.co.akm.test.imagesearch.process.track.shift.ColourCubeDifference;
import uk.co.akm.test.imagesearch.process.track.shift.ColourCubeHistogram;

public final class ColourCubeDifferenceImpl implements ColourCubeDifference {
    private final int width;
    private final int height;
    private final int nSideDivs;

    private final float[] weights;
    private final float[] differences;

    public ColourCubeDifferenceImpl(ColourCubeHistogram reference, ColourCubeHistogram histogram) {
        checkCompatibility(reference, histogram);

        width = histogram.imageWidth();
        height = histogram.imageHeight();
        nSideDivs = histogram.divisionsInSide();

        weights = buildWeights(reference);
        differences = buildDifferences(reference, histogram);
    }

    private void checkCompatibility(ColourCubeHistogram reference, ColourCubeHistogram histogram) {
        if (reference == null) {
            throw new NullPointerException("No null reference histogram argument allowed.");
        }

        if (histogram == null) {
            throw new NullPointerException("No null histogram argument allowed.");
        }

        if (histogram.imageWidth() != reference.imageWidth()) {
            throw new IllegalArgumentException("Histogram image width argument, " + histogram.imageWidth() + ", is not equal to the reference histogram image width argument, " + reference.imageWidth() + ".");
        }

        if (histogram.imageHeight() != reference.imageHeight()) {
            throw new IllegalArgumentException("Histogram image height argument, " + histogram.imageHeight() + ", is not equal to the reference histogram image height argument, " + reference.imageHeight() + ".");
        }

        if (histogram.divisionsInSide() != reference.divisionsInSide()) {
            throw new IllegalArgumentException("Histogram number of divisions in cube side argument, " + histogram.divisionsInSide() + ", is not equal to the reference histogram number of divisions in cube side argument, " + reference.divisionsInSide() + ".");
        }
    }

    private float[] buildDifferences(ColourCubeHistogram reference, ColourCubeHistogram histogram) {
        final int side = histogram.divisionsInSide();
        final float[] differences = new float[side*side*side];
        determineDifferences(reference, histogram, differences);

        return differences;
    }

    private void determineDifferences(ColourCubeHistogram reference, ColourCubeHistogram histogram, float[] differences) {
        for (int i=0 ; i<differences.length ; i++) {
            final float referenceBinSize = reference.binSize(i);
            if (referenceBinSize == 0f) {
                differences[i] = NO_COMPARISON;
            } else {
                differences[i] = Math.abs(histogram.binSize(i) - referenceBinSize)/referenceBinSize;
            }
        }
    }

    private float[] buildWeights(ColourCubeHistogram reference) {
        final int side = reference.divisionsInSide();
        final float[] weights = new float[side*side*side];
        determineWeights(reference, weights);

        return weights;
    }

    private void determineWeights(ColourCubeHistogram reference, float[] weights) {
        final float nPoints = reference.nPoints();
        for (int i=0 ; i<weights.length ; i++) {
            weights[i] = reference.binSize(i)/nPoints;
        }
    }

    @Override
    public int imageWidth() {
        return width;
    }

    @Override
    public int imageHeight() {
        return height;
    }

    @Override
    public int divisionsInSide() {
        return nSideDivs;
    }

    @Override
    public int nBins() {
        return nSideDivs*nSideDivs*nSideDivs;
    }

    @Override
    public boolean hasBinDiff(int binIndex) {
        return differences[binIndex] != NO_COMPARISON;
    }

    @Override
    public float binDiff(int binIndex) {
        return differences[binIndex];
    }

    @Override
    public float refBinWeight(int binIndex) {
        return weights[binIndex];
    }
}
