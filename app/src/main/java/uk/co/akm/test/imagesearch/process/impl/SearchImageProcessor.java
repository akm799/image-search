package uk.co.akm.test.imagesearch.process.impl;

import android.graphics.Bitmap;
import android.util.Log;

import uk.co.akm.test.imagesearch.process.ImageProcessor;
import uk.co.akm.test.imagesearch.process.model.window.ColouredWindow;
import uk.co.akm.test.imagesearch.process.model.window.Window;
import uk.co.akm.test.imagesearch.process.track.search.impl.BasicBestMatchFinder;
import uk.co.akm.test.imagesearch.process.track.search.impl.MeanShiftBestMatchFinder;
import uk.co.akm.test.imagesearch.process.track.search.impl.SmallShiftBestMatchFinder;

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
        return processImageNoLog(image);
    }

    private Bitmap processImageNoLog(Bitmap image) {
        final Window initialMatchWindow = (new BasicBestMatchFinder()).findBestMatch(image, targetWindow, image);
        final Window firstBestMatchWindow = (new MeanShiftBestMatchFinder(initialMatchWindow)).findBestMatch(image, targetWindow, image);
        final Window bestMatchWindow = (new SmallShiftBestMatchFinder(firstBestMatchWindow)).findBestMatch(image, targetWindow, image);
        final ImageProcessor windowImageProcessor = new WindowImageProcessor(new ColouredWindow(bestMatchWindow, bestMatchColour));

        return windowImageProcessor.processImage(image);
    }

    private Bitmap processImageVerbose(Bitmap image) {
        final String tag = "shift";

        final long t0 = System.currentTimeMillis();
        final Window initialMatchWindow = (new BasicBestMatchFinder()).findBestMatch(image, targetWindow, image);
        final long dt1 = System.currentTimeMillis() - t0;
        Log.d(tag, "Brute force search " + dt1 + " ms");
        logWindowShift(tag, targetWindow, initialMatchWindow);

        final long t1 = System.currentTimeMillis();
        final Window firstBestMatchWindow = (new MeanShiftBestMatchFinder(initialMatchWindow)).findBestMatch(image, targetWindow, image);
        final long dt2 = System.currentTimeMillis() - t1;
        Log.d(tag, "Mean shift search " + dt2 + " ms");
        logWindowShift(tag, initialMatchWindow, firstBestMatchWindow);

        final long t2 = System.currentTimeMillis();
        final Window bestMatchWindow = (new SmallShiftBestMatchFinder(firstBestMatchWindow)).findBestMatch(image, targetWindow, image);
        final long dt3 = System.currentTimeMillis() - t2;
        Log.d(tag, "Small shift search " + dt3 + " ms");
        logWindowShift(tag, firstBestMatchWindow, bestMatchWindow);

        Log.d(tag, "Total search " + (dt1 + dt2 + dt3) + " ms");
        logWindowShift(tag, targetWindow, bestMatchWindow);

        final ImageProcessor windowImageProcessor = new WindowImageProcessor(new ColouredWindow(bestMatchWindow, bestMatchColour));

        return windowImageProcessor.processImage(image);
    }

    private void logWindowShift(String tag, Window start, Window end) {
        final int dx = Math.abs(end.xMin - start.xMin);
        final int dy = Math.abs(end.yMin - start.yMin);
        final long d = Math.round(Math.sqrt(dx*dx + dy*dy));
        Log.d(tag, "From " + start + " to " + end);
        Log.d(tag, "dx=" + dx + " dy=" + dy + " d=" + d);
    }
}
