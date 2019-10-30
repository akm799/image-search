package uk.co.akm.test.imagesearch.process.track.shift;

/**
 * Created by Thanos Mavroidis on 07/04/2019.
 */
public interface MutableColourCubeHistogram extends ColourCubeHistogram {

    /**
     * Adds the pixel with the input pixel index and colour to the colour distribution
     * expressed by this colour histogram.
     *
     * @param pixelIndex the index of the pixel to add
     * @param rgb the colour of the pixel to add
     */
    void add(int pixelIndex, int rgb);

    /**
     * Clears this instance of all added pixels.
     */
    void clear();
}
