package uk.co.akm.test.imagesearch.process.track.search.impl;


import android.graphics.Bitmap;


import uk.co.akm.test.imagesearch.process.model.window.Window;
import uk.co.akm.test.imagesearch.process.track.search.BestMatchFinder;

/**
 * Created by Thanos Mavroidis on 04/01/2021.
 */
public final class SmallShiftBestMatchFinder implements BestMatchFinder {
    private static final int SHIFT_NO_DIRECTION = 0;
    private static final ShiftResult SHIFT_NO_IMPROVEMENT = new ShiftResult();


    private final int nSideDivs = 51;
    private final ColourHistogram colourHistogram = new ColourHistogram(nSideDivs);
    private final ColourHistogram trackingColourHistogram = new ColourHistogram(nSideDivs);

    private final Window initialWindow;

    public SmallShiftBestMatchFinder(Window initialWindow) {
        this.initialWindow = new Window(initialWindow);
    }

    @Override
    public Window findBestMatch(Bitmap targetImage, Window targetWindow, Bitmap image) {
        colourHistogram.fillColourHistogramForWindow(targetImage, targetWindow);
        trackingColourHistogram.fillColourHistogramForWindow(targetImage, initialWindow);

        return findBestMatchWindow(targetImage, initialWindow);
    }

    private Window findBestMatchWindow(Bitmap targetImage, Window startWindow) {
        Window window = startWindow;
        ShiftResult shiftResult = new ShiftResult(SHIFT_NO_DIRECTION);
        while (shiftResult.improved) {
            shiftResult = findBestShiftDirection(targetImage, window);
            if (shiftResult.improved) {
                window = shiftWindow(window, shiftResult.shiftDirection);
                trackingColourHistogram.fillColourHistogramForWindow(targetImage, window);
            }
        }

        return window;
    }

    private ShiftResult findBestShiftDirection(Bitmap image, Window window) {
        final int unShiftedDiff = colourHistogram.diff(trackingColourHistogram); //TODO Optimise this: Shift result can also return the diff. We do not need to caculate it again.

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
            return new ShiftResult(bestShiftDirection);
        } else {
            return SHIFT_NO_IMPROVEMENT;
        }
    }

    private int diff(ColourHistogram unShiftedColourHistogram, Bitmap image, Window window, int shiftDirection) {
        final ColourHistogram shiftedColourHistogram = shift(unShiftedColourHistogram, image, window, shiftDirection);

        return unShiftedColourHistogram.diff(shiftedColourHistogram);
    }

    private ColourHistogram shift(ColourHistogram unShiftedColourHistogram, Bitmap image, Window window, int shiftDirection) {
        final ColourHistogram shiftedColourHistogram = new ColourHistogram(unShiftedColourHistogram);
        shiftedColourHistogram.fillColourHistogramForShiftedWindow(image, window, shiftDirection);

        return shiftedColourHistogram;
    }

    private Window shiftWindow(Window window, int shiftDirection) {
        int dx, dy;

        switch (shiftDirection) {
            case ColourHistogram.SHIFT_LEFT:
                dx = -1;
                dy = 0;
                break;

            case ColourHistogram.SHIFT_RIGHT:
                dx = 1;
                dy = 0;
                break;

            case ColourHistogram.SHIFT_UP:
                dx = 0;
                dy = -1;
                break;

            case ColourHistogram.SHIFT_DOWN:
                dx = 0;
                dy = 1;
                break;

            case ColourHistogram.SHIFT_LEFT_UP:
                dx = -1;
                dy = -1;
                break;

            case ColourHistogram.SHIFT_LEFT_DOWN:
                dx = -1;
                dy = 1;
                break;

            case ColourHistogram.SHIFT_RIGHT_UP:
                dx = 1;
                dy = -1;
                break;

            case ColourHistogram.SHIFT_RIGHT_DOWN:
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

        ShiftResult() {
            improved = false;
            shiftDirection = 0;
        }

        ShiftResult(int shiftDirection) {
            this.improved = true;
            this.shiftDirection = shiftDirection;
        }
    }
}

