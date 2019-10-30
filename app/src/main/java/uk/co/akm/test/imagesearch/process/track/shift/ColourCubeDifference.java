package uk.co.akm.test.imagesearch.process.track.shift;



/**
 * A result of comparison between two @{@link ColourCubeHistogram} instances. The first instance in that comparison is
 * called the 'reference' instance. Please note that both histograms in the comparison must have same number of bins
 * and must be constructed out of images with the same dimensions.
 *
 * Created by Thanos Mavroidis on 09/06/2019.
 */
public interface ColourCubeDifference {
    float NO_COMPARISON = -1;


    int imageWidth();

    int imageHeight();

    int divisionsInSide();

    int nBins();

    /**
     * Returns true if a comparison between the bins with the specified bin index in the two histograms was possible or
     * false otherwise. If the comparison was not possible, then the {@link #binDiff(int)} method should not be called,
     * since no bin difference could be produced. For the comparison to be possible the bin with the input bin index in
     * the reference histogram must contain at least one point.
     *
     * @param binIndex the index of the bin for which we wish to know if a comparison between the two histograms was
     *                 possible
     * @return true if a comparison between the bins with the sepcified bin index in the two histograms was possible or
     * false otherwise
     */
    boolean hasBinDiff(int binIndex);

    /**
     * Returns Math.abs(h - r)/r where r is the number of points in the bin with the input bin index in the reference
     * histogram and h the corresponding number in the histogram we are comparing to the reference.
     *
     * @param binIndex the index of the bin for which the difference between the two histograms will be returned
     * @return Math.abs(h - r)/r where r is the number of points in the bin with the input bin index in the reference
     * histogram and h the corresponding number in the histogram we are comparing to the reference
     */
    float binDiff(int binIndex);

    /**
     * Returns the number of points in the bin with the input bin index in the reference histogram over the total number
     * of points in the same histogram (i.e. the normalized weight of the bin in the reference histogram).
     *
     * @param binIndex the index of the bin, in the reference histogram, for which the normalized weight will be returned
     * @return the number of points in the bin with the input bin index in the reference histogram over the total number
     * of points in the same histogram
     */
    float refBinWeight(int binIndex);
}
