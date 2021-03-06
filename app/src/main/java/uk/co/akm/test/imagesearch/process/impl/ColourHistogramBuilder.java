package uk.co.akm.test.imagesearch.process.impl;

import android.graphics.Bitmap;
import android.util.Log;

import uk.co.akm.test.imagesearch.process.ImageOperator;
import uk.co.akm.test.imagesearch.process.model.window.Window;
import uk.co.akm.test.imagesearch.process.track.search.impl.map.ColourHistogram;
import uk.co.akm.test.imagesearch.process.track.search.impl.map.PixelMap;

/**
 */
public final class ColourHistogramBuilder implements ImageOperator<ColourHistogram> {
    private final int nSideDivs = 51;  //TODO Move in common parameters.
    private final int scaleFactor = 4; //TODO Move in common parameters.

    private final Window window;

    public ColourHistogramBuilder(Window window) {
        this.window = window;
    }

    @Override
    public String getDescription() {
        return "Build colour histogram for the input window image section.";
    }

    @Override
    public ColourHistogram processImage(Bitmap image) {
        return processImageNoLog(image);
    }

    private ColourHistogram processImageNoLog(Bitmap image) {
        final Window scaledDownTargetWindow = scaleDownWindow(window, scaleFactor);
        final Bitmap scaledDownImage = scaleDownImage(image, scaleFactor);

        final ColourHistogram colourHistogram = new ColourHistogram(nSideDivs);
        final PixelMap pixelMap = colourHistogram.toPixelMap(scaledDownImage);
        colourHistogram.fillColourHistogramForWindow(pixelMap, scaledDownTargetWindow);

        return colourHistogram;
    }

    private ColourHistogram processImageVerbose(Bitmap image) {
        final String tag = "histogram";
        Log.d(tag, "Starting  colour histogram building ...");

        final Window scaledDownTargetWindow = scaleDownWindow(window, scaleFactor);

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

        Log.d(tag, "Total search: " + (dt1 + dt2 + dt3) + " ms");
        scaledDownImage.recycle();

        return colourHistogram;
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
}
