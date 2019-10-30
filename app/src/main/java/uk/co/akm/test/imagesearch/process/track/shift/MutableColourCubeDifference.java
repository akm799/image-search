package uk.co.akm.test.imagesearch.process.track.shift;

/**
 * Implementations of this class must define the @{@link ColourCubeHistogram} reference instance
 * at creation time. Then, the difference between this reference @{@link ColourCubeHistogram} and
 * any other instance can be evaluated.
 *
 * Created by Thanos Mavroidis on 07/08/2019.
 */
public interface MutableColourCubeDifference extends ColourCubeDifference {

    /**
     * Evaluates the difference between the input @{@link ColourCubeHistogram} and a reference
     * instance that has already been defined.
     *
     * @param histogram the @{@link ColourCubeHistogram} instance whose difference with an already
     *                  defined reference will be evaluated
     */
    void evaluate(ColourCubeHistogram histogram);
}
