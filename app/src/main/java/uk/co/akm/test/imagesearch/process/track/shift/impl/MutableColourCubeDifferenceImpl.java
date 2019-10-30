package uk.co.akm.test.imagesearch.process.track.shift.impl;


import uk.co.akm.test.imagesearch.process.track.shift.ColourCubeHistogram;
import uk.co.akm.test.imagesearch.process.track.shift.MutableColourCubeDifference;

/**
 * Created by Thanos Mavroidis on 07/08/2019.
 */
public final class MutableColourCubeDifferenceImpl implements MutableColourCubeDifference {
    private final int width;
    private final int height;
    private final int nSideDivs;

    private final float[] weights;
    private final float[] differences;

    private final ColourCubeHistogram reference;

    private boolean notEvaluated = true;

    public MutableColourCubeDifferenceImpl(ColourCubeHistogram reference) {
        checkReferenceHistogram(reference);

        this.width = reference.imageWidth();
        this.height = reference.imageHeight();
        this.nSideDivs = reference.divisionsInSide();

        this.weights = buildWeights(reference);
        this.differences = new float[nSideDivs*nSideDivs*nSideDivs];

        this.reference = reference;
    }

    private void checkReferenceHistogram(ColourCubeHistogram reference) {
        if (reference == null) {
            throw new NullPointerException("No null reference histogram argument allowed.");
        }

        if (reference.imageWidth() <= 0) {
            throw new IllegalArgumentException("Reference histogram image width, " + reference.imageWidth() + ", must be greater than zero.");
        }

        if (reference.imageHeight() <= 0) {
            throw new IllegalArgumentException("Reference histogram image height, " + reference.imageHeight() + ", must be greater than zero.");
        }

        if (reference.divisionsInSide() <= 0) {
            throw new IllegalArgumentException("Reference histogram number of divisions per colour dimension (divisionsInSide()), " + reference.imageHeight() + ", must be greater than zero.");
        }
    }

    @Override
    public void evaluate(ColourCubeHistogram histogram) {
        checkCompatibility(reference, histogram);
        determineDifferences(reference, histogram, differences);
        notEvaluated = false;
    }

    private void checkCompatibility(ColourCubeHistogram reference, ColourCubeHistogram histogram) {
        if (histogram == null) {
            throw new NullPointerException("No null histogram argument allowed.");
        }

        if (histogram.imageWidth() != reference.imageWidth()) {
            throw new IllegalArgumentException("Histogram image width, " + histogram.imageWidth() + ", is not equal to the reference histogram image width, " + reference.imageWidth() + ".");
        }

        if (histogram.imageHeight() != reference.imageHeight()) {
            throw new IllegalArgumentException("Histogram image height, " + histogram.imageHeight() + ", is not equal to the reference histogram image height, " + reference.imageHeight() + ".");
        }

        if (histogram.divisionsInSide() != reference.divisionsInSide()) {
            throw new IllegalArgumentException("Histogram number of divisions per colour dimension (divisionsInSide()), " + histogram.divisionsInSide() + ", is not equal to the reference histogram corresponding parameter, " + reference.divisionsInSide() + ".");
        }
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
        ensureEvaluationDone();

        return differences[binIndex] != NO_COMPARISON;
    }

    @Override
    public float binDiff(int binIndex) {
        ensureEvaluationDone();

        return differences[binIndex];
    }

    @Override
    public float refBinWeight(int binIndex) {
        return weights[binIndex];
    }

    private void ensureEvaluationDone() {
        if (notEvaluated) {
            throw new IllegalStateException("No difference has been evaluated. Please ensure that the 'evaluate(ColourCubeHistogram)' method has been called first.");
        }
    }
}
