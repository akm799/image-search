package uk.co.akm.test.imagesearch.process.impl;

import android.graphics.Bitmap;
import android.util.Log;

import uk.co.akm.test.imagesearch.process.ImageProcessor;
import uk.co.akm.test.imagesearch.process.model.window.ColouredWindow;
import uk.co.akm.test.imagesearch.process.model.window.Window;
import uk.co.akm.test.imagesearch.process.track.search.BestMatchFinder;
import uk.co.akm.test.imagesearch.process.track.search.impl.BasicBestMatchFinder;
import uk.co.akm.test.imagesearch.process.track.search.impl.MeanShiftBestMatchFinder;
import uk.co.akm.test.imagesearch.process.track.search.impl.SmallShiftBestMatchFinder;
import uk.co.akm.test.imagesearch.process.track.search.impl.map.ColourHistogram;
import uk.co.akm.test.imagesearch.process.track.search.impl.map.PixelMap;

public final class SearchImageProcessor implements ImageProcessor {
    private final int nSideDivs = 51;
    private final int scaleFactor = 4;

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
        final Window scaledDownTargetWindow = scaleDownWindow(targetWindow, scaleFactor);
        final Bitmap scaledDownImage = scaleDownImage(image, scaleFactor);

        final ColourHistogram colourHistogram = new ColourHistogram(nSideDivs);
        final PixelMap pixelMap = colourHistogram.toPixelMap(scaledDownImage);
        colourHistogram.fillColourHistogramForWindow(pixelMap, scaledDownTargetWindow);

        final BestMatchFinder basicBestMatchFinder = new BasicBestMatchFinder(colourHistogram, scaledDownTargetWindow.width, scaledDownTargetWindow.height);
        final Window initialMatchWindow = basicBestMatchFinder.findBestMatch(pixelMap);

        final BestMatchFinder meanShiftBestMatch = new MeanShiftBestMatchFinder(colourHistogram, initialMatchWindow);
        final Window meanShiftBestMatchWindow = meanShiftBestMatch.findBestMatch(pixelMap);

        final BestMatchFinder smallShiftBestMatchFinder = new SmallShiftBestMatchFinder(colourHistogram, meanShiftBestMatchWindow);
        final Window bestMatchWindow = smallShiftBestMatchFinder.findBestMatch(pixelMap);

        final Window scaledUpBestMatchWindow = scaleUpWindow(bestMatchWindow, scaleFactor);
        final ImageProcessor windowImageProcessor = new WindowImageProcessor(new ColouredWindow(scaledUpBestMatchWindow, bestMatchColour));
        return windowImageProcessor.processImage(image);
    }

    private Bitmap processImageVerbose(Bitmap image) {
        final String tag = "shift";
        Log.d(tag, "Starting search ...");

        final Window scaledDownTargetWindow = scaleDownWindow(targetWindow, scaleFactor);

        final long t0 = System.currentTimeMillis();
        final Bitmap scaledDownImage = scaleDownImage(image, scaleFactor);
        final long dt1 = System.currentTimeMillis() - t0;
        Log.d(tag, "Image down-sizing: " + dt1 + " ms");
        Log.d(tag, "Image size: " + image.getWidth() + " x " + image.getHeight());
        Log.d(tag, "Down-sized Image size: " + scaledDownImage.getWidth() + " x " + scaledDownImage.getHeight());

        final long t1 = System.currentTimeMillis();
        final ColourHistogram colourHistogram = new ColourHistogram(nSideDivs);
        final PixelMap pixelMap = colourHistogram.toPixelMap(scaledDownImage);
        final long dt2 = System.currentTimeMillis() - t1;
        Log.d(tag, "Pixel map generation: " + dt2 + " ms");

        final long t2 = System.currentTimeMillis();
        colourHistogram.fillColourHistogramForWindow(pixelMap, scaledDownTargetWindow);
        final long dt3 = System.currentTimeMillis() - t2;
        Log.d(tag, "Target colour histogram initialisation: " + dt3 + " ms");

        final long t3 = System.currentTimeMillis();
        final BestMatchFinder basicBestMatchFinder = new BasicBestMatchFinder(colourHistogram, scaledDownTargetWindow.width, scaledDownTargetWindow.height);
        final Window initialMatchWindow = basicBestMatchFinder.findBestMatch(pixelMap);
        final long dt4 = System.currentTimeMillis() - t3;
        Log.d(tag, "Brute force approximate search: " + dt4 + " ms");
        logWindowShift(tag, scaledDownTargetWindow, initialMatchWindow);

        final long t4 = System.currentTimeMillis();
        final BestMatchFinder meanShiftBestMatch = new MeanShiftBestMatchFinder(colourHistogram, initialMatchWindow);
        final Window meanShiftBestMatchWindow = meanShiftBestMatch.findBestMatch(pixelMap);
        final long dt5 = System.currentTimeMillis() - t4;
        Log.d(tag, "Mean shift search: " + dt5 + " ms");
        logWindowShift(tag, initialMatchWindow, meanShiftBestMatchWindow);

        final long t5 = System.currentTimeMillis();
        final BestMatchFinder smallShiftBestMatchFinder = new SmallShiftBestMatchFinder(colourHistogram, meanShiftBestMatchWindow);
        final Window bestMatchWindow = smallShiftBestMatchFinder.findBestMatch(pixelMap);
        final long dt6 = System.currentTimeMillis() - t5;
        Log.d(tag, "Small shift search: " + dt6 + " ms");
        logWindowShift(tag, meanShiftBestMatchWindow, bestMatchWindow);

        Log.d(tag, "Total search: " + (dt1 + dt2 + dt3 + dt4 + dt5 + dt6) + " ms");
        logWindowShift(tag, scaledDownTargetWindow, bestMatchWindow);

        final Window scaledUpBestMatchWindow = scaleUpWindow(bestMatchWindow, scaleFactor);
        final ImageProcessor windowImageProcessor = new WindowImageProcessor(new ColouredWindow(scaledUpBestMatchWindow, bestMatchColour));
        scaledDownImage.recycle();

        return windowImageProcessor.processImage(image);
    }

    private Bitmap scaleDownImage(Bitmap image, double scaleFactor) {
        final int scaledWidth = (int)Math.round(image.getWidth() / scaleFactor);
        final int scaledHeight = (int)Math.round(image.getHeight() / scaleFactor);

        return Bitmap.createScaledBitmap(image, scaledWidth, scaledHeight, false);
    }

    private Window scaleDownWindow(Window window, double scaleFactor) {
        final int xMin = (int)Math.round(window.xMin / scaleFactor);
        final int yMin = (int)Math.round(window.yMin / scaleFactor);
        final int width = (int)Math.round(window.width / scaleFactor);
        final int height = (int)Math.round(window.height / scaleFactor);

        return new Window(xMin, yMin, width, height);
    }

    private Window scaleUpWindow(Window window, int scaleFactor) {
        final int xMin = scaleFactor * window.xMin;
        final int yMin = scaleFactor * window.yMin;
        final int width = scaleFactor * window.width;
        final int height = scaleFactor * window.height;

        return new Window(xMin, yMin, width, height);
    }

    private void logWindowShift(String tag, Window start, Window end) {
        final int dx = Math.abs(end.xMin - start.xMin);
        final int dy = Math.abs(end.yMin - start.yMin);
        final long d = Math.round(Math.sqrt(dx*dx + dy*dy));
        Log.d(tag, "From " + start + " to " + end);
        Log.d(tag, "dx=" + dx + " dy=" + dy + " d=" + d);
    }
}
