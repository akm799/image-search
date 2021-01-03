package uk.co.akm.test.imagesearch.process.impl;

import android.graphics.Bitmap;

import uk.co.akm.test.imagesearch.process.ImageProcessor;
import uk.co.akm.test.imagesearch.process.model.window.ColouredWindow;
import uk.co.akm.test.imagesearch.process.model.window.Window;
import uk.co.akm.test.imagesearch.process.track.search.impl.BasicBestMatchFinder;
import uk.co.akm.test.imagesearch.process.track.search.impl.MeanShiftBestMatchFinder;

public final class SearchImageProcessor implements ImageProcessor {
    private final int bestMatchColour;
    private final Window targetWindow;

    public SearchImageProcessor(Window targetWindow, int bestMatchColour) {
        this.targetWindow = targetWindow;
        this.bestMatchColour = bestMatchColour;
    }

    @Override
    public String getDescription() {
        return "Found best match with the input window.";
    }

    @Override
    public Bitmap processImage(Bitmap image) {
        final Window initialMatchWindow = (new BasicBestMatchFinder()).findBestMatch(image, targetWindow, image);
        final Window bestMatchWindow = (new MeanShiftBestMatchFinder(initialMatchWindow)).findBestMatch(image, targetWindow, image);
        final ImageProcessor windowImageProcessor = new WindowImageProcessor(new ColouredWindow(bestMatchWindow, bestMatchColour));

        return windowImageProcessor.processImage(image);
    }
}
