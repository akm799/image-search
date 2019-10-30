package uk.co.akm.test.imagesearch.process.track.shift.impl;


import uk.co.akm.test.imagesearch.process.model.collections.IntArrayList;
import uk.co.akm.test.imagesearch.process.model.collections.IntCollection;
import uk.co.akm.test.imagesearch.process.track.shift.MutableColourCubeHistogram;
import uk.co.akm.test.imagesearch.process.util.ColourHelper;

/**
 * Created by Thanos Mavroidis on 07/04/2019.
 */
public final class MutableColourCubeHistogramImpl implements MutableColourCubeHistogram {
    private static final int MAX_COLOUR_VALUE_INT = 255;
    private static final float MAX_COLOUR_VALUE = (float)MAX_COLOUR_VALUE_INT;

    private final int width;
    private final int height;

    private final int nSideDivs;
    private final int nSideDivsSq;

    private final int nBins;
    private final IntCollection[] bins;
    private final float binSide;

    public MutableColourCubeHistogramImpl(int width, int height, int nSideDivs) {
        this.width = width;
        this.height = height;
        this.nSideDivs = nSideDivs;

        this.nSideDivsSq = nSideDivs*nSideDivs;
        this.nBins = nSideDivsSq*nSideDivs;
        this.binSide = MAX_COLOUR_VALUE/nSideDivs;
        this.bins = new IntArrayList[nBins];
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
    public int nPoints() {
        int nPoints = 0;
        for (int i=0 ; i<nBins ; i++) {
            nPoints += binSize(i);
        }

        return nPoints;
    }

    @Override
    public int nBins() {
        return nBins;
    }

    @Override
    public int binSize(int binIndex) {
        if (bins[binIndex] == null) {
            return 0;
        } else {
            return bins[binIndex].size();
        }
    }

    @Override
    public IntCollection binPoints(int binIndex) {
        if (bins[binIndex] == null) {
            return IntCollection.EMPTY;
        } else {
            return bins[binIndex];
        }
    }

    @Override
    public void add(int pixelIndex, int rgb) {
        final int rIndex = findSideBinIndex(ColourHelper.getRed(rgb));
        final int gIndex = findSideBinIndex(ColourHelper.getGreen(rgb));
        final int bIndex = findSideBinIndex(ColourHelper.getBlue(rgb));

        int binIndex = bIndex*nSideDivsSq + gIndex*nSideDivs + rIndex;
        if (bins[binIndex] == null) {
            bins[binIndex] = new IntArrayList();
        }

        bins[binIndex].add(pixelIndex);
    }

    private int findSideBinIndex(int value) {
        if (value == MAX_COLOUR_VALUE_INT) { // Include the 255 value in the last bin.
            return nSideDivs - 1;
        } else {
            return (int) (value/binSide);

        }
    }

    /**
     * Please not that this implementation, although deleting all pixel entries as per the contact of this method, preserves
     * the memory that was occupied by the deleted entries, for future use.
     */
    @Override
    public void clear() {
        for (IntCollection bin: bins) {
            if (bin != null) {
                bin.clear();
            }
        }
    }
}
