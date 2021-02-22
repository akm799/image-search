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
        final ColourHistogram colourHistogram = new ColourHistogram(nSideDivs);
        final PixelMap pixelMap = colourHistogram.toPixelMap(image);
        colourHistogram.fillColourHistogramForWindow(pixelMap, targetWindow);

        final BestMatchFinder basicBestMatchFinder = new BasicBestMatchFinder(colourHistogram, targetWindow.width, targetWindow.height);
        final Window initialMatchWindow = basicBestMatchFinder.findBestMatch(pixelMap);

        final BestMatchFinder meanShiftBestMatch = new MeanShiftBestMatchFinder(colourHistogram, initialMatchWindow);
        final Window meanShiftBestMatchWindow = meanShiftBestMatch.findBestMatch(pixelMap);

        final BestMatchFinder smallShiftBestMatchFinder = new SmallShiftBestMatchFinder(colourHistogram, meanShiftBestMatchWindow);
        final Window bestMatchWindow = smallShiftBestMatchFinder.findBestMatch(pixelMap);

        final ImageProcessor windowImageProcessor = new WindowImageProcessor(new ColouredWindow(bestMatchWindow, bestMatchColour));
        return windowImageProcessor.processImage(image);
    }

    private Bitmap processImageVerbose(Bitmap image) {
        final String tag = "shift";
        Log.d(tag, "Starting search ...");

        final long t0 = System.currentTimeMillis();
        final ColourHistogram colourHistogram = new ColourHistogram(nSideDivs);
        final PixelMap pixelMap = colourHistogram.toPixelMap(image);
        colourHistogram.fillColourHistogramForWindow(pixelMap, targetWindow);
        final long dt1 = System.currentTimeMillis() - t0;
        Log.d(tag, "Target colour histogram initialisation " + dt1 + " ms");

        final long t1 = System.currentTimeMillis();
        final BestMatchFinder basicBestMatchFinder = new BasicBestMatchFinder(colourHistogram, targetWindow.width, targetWindow.height);
        final Window initialMatchWindow = basicBestMatchFinder.findBestMatch(pixelMap);
        final long dt2 = System.currentTimeMillis() - t1;
        Log.d(tag, "Brute force search " + dt2 + " ms");
        logWindowShift(tag, targetWindow, initialMatchWindow);

        final long t2 = System.currentTimeMillis();
        final BestMatchFinder meanShiftBestMatch = new MeanShiftBestMatchFinder(colourHistogram, initialMatchWindow);
        final Window meanShiftBestMatchWindow = meanShiftBestMatch.findBestMatch(pixelMap);
        final long dt3 = System.currentTimeMillis() - t2;
        Log.d(tag, "Mean shift search " + dt3 + " ms");
        logWindowShift(tag, initialMatchWindow, meanShiftBestMatchWindow);

        final long t3 = System.currentTimeMillis();
        final BestMatchFinder smallShiftBestMatchFinder = new SmallShiftBestMatchFinder(colourHistogram, meanShiftBestMatchWindow);
        final Window bestMatchWindow = smallShiftBestMatchFinder.findBestMatch(pixelMap);
        final long dt4 = System.currentTimeMillis() - t3;
        Log.d(tag, "Small shift search " + dt4 + " ms");
        logWindowShift(tag, meanShiftBestMatchWindow, bestMatchWindow);

        Log.d(tag, "Total search " + (dt1 + dt2 + dt3 + dt4) + " ms");
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
