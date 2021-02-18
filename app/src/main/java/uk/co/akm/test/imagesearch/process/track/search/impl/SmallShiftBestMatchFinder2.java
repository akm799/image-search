package uk.co.akm.test.imagesearch.process.track.search.impl;


import android.graphics.Bitmap;

import uk.co.akm.test.imagesearch.process.model.window.Window;
import uk.co.akm.test.imagesearch.process.track.search.BestMatchFinder;
import uk.co.akm.test.imagesearch.process.track.search.impl.map.ColourHistogramMap;
import uk.co.akm.test.imagesearch.process.track.search.impl.map.PixelMap;

/**
 * Created by Thanos Mavroidis on 04/01/2021.
 */
public final class SmallShiftBestMatchFinder2 implements BestMatchFinder {
    private static final int SHIFT_NO_DIRECTION = 0;
    private static final ShiftResult SHIFT_NO_IMPROVEMENT = new ShiftResult();


    private final int nSideDivs = 51;
    private final ColourHistogramMap colourHistogram = new ColourHistogramMap(nSideDivs);
    private final ColourHistogramMap trackingColourHistogram = new ColourHistogramMap(nSideDivs);

    private final Window initialWindow;

    public SmallShiftBestMatchFinder2(Window initialWindow) {
        this.initialWindow = new Window(initialWindow);
    }

    @Override
    public Window findBestMatch(Bitmap targetImage, Window targetWindow, Bitmap image) {
        final PixelMap targetImageMap = colourHistogram.toPixelMap(targetImage);
        colourHistogram.fillColourHistogramForWindow(targetImageMap, targetWindow);
        trackingColourHistogram.fillColourHistogramForWindow(targetImageMap, initialWindow);

        return findBestMatchWindow(targetImageMap, initialWindow);
    }

    private Window findBestMatchWindow(PixelMap targetImage, Window startWindow) {
        Window window = startWindow;
        ShiftResult shiftResult = new ShiftResult(SHIFT_NO_DIRECTION, Integer.MAX_VALUE);
        while (shiftResult.improved) {
            shiftResult = findBestShiftDirection(targetImage, window, shiftResult.diff);
            if (shiftResult.improved) {
                window = shiftWindow(window, shiftResult.shiftDirection);
                trackingColourHistogram.fillColourHistogramForWindow(targetImage, window);
            }
        }

        return window;
    }

    private ShiftResult findBestShiftDirection(PixelMap image, Window window, int unShiftedDiff) {
        int minDiff = unShiftedDiff;
        int bestShiftDirection = SHIFT_NO_DIRECTION;
        for (int shiftDirection : ColourHistogram.SHIFT_DIRECTIONS) {
            final int diff = diff(trackingColourHistogram, image, window, shiftDirection);
            if (diff < minDiff) {
                minDiff = diff;
                bestShiftDirection = shiftDirection;
            }
        }

        if (minDiff < unShiftedDiff) {
            return new ShiftResult(bestShiftDirection, minDiff);
        } else {
            return SHIFT_NO_IMPROVEMENT;
        }
    }

    private int diff(ColourHistogramMap unShiftedColourHistogram, PixelMap image, Window window, int shiftDirection) {
        final ColourHistogramMap shiftedColourHistogram = shift(unShiftedColourHistogram, image, window, shiftDirection);

        return colourHistogram.diff(shiftedColourHistogram);
    }

    private ColourHistogramMap shift(ColourHistogramMap unShiftedColourHistogram, PixelMap image, Window window, int shiftDirection) {
        final ColourHistogramMap shiftedColourHistogram = new ColourHistogramMap(unShiftedColourHistogram);
        shiftedColourHistogram.fillColourHistogramForShiftedWindow(image, window, shiftDirection);

        return shiftedColourHistogram;
    }

    private Window shiftWindow(Window window, int shiftDirection) {
        int dx, dy;

        switch (shiftDirection) {
            case ColourHistogramMap.SHIFT_LEFT:
                dx = -1;
                dy = 0;
                break;

            case ColourHistogramMap.SHIFT_RIGHT:
                dx = 1;
                dy = 0;
                break;

            case ColourHistogramMap.SHIFT_UP:
                dx = 0;
                dy = -1;
                break;

            case ColourHistogramMap.SHIFT_DOWN:
                dx = 0;
                dy = 1;
                break;

            case ColourHistogramMap.SHIFT_LEFT_UP:
                dx = -1;
                dy = -1;
                break;

            case ColourHistogramMap.SHIFT_LEFT_DOWN:
                dx = -1;
                dy = 1;
                break;

            case ColourHistogramMap.SHIFT_RIGHT_UP:
                dx = 1;
                dy = -1;
                break;

            case ColourHistogramMap.SHIFT_RIGHT_DOWN:
                dx = 1;
                dy = 1;
                break;

            default: throw new IllegalArgumentException("Illegal shift direction: " + shiftDirection);
        }

        return window.shift(dx, dy);
    }

    private static class ShiftResult {
        public final boolean improved;
        public final int shiftDirection;
        public final int diff;

        ShiftResult() {
            improved = false;
            shiftDirection = 0;
            diff = Integer.MAX_VALUE;
        }

        ShiftResult(int shiftDirection, int diff) {
            this.improved = true;
            this.shiftDirection = shiftDirection;
            this.diff = diff;
        }
    }
}

