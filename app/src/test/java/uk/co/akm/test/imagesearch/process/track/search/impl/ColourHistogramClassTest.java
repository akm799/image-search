package uk.co.akm.test.imagesearch.process.track.search.impl;

import org.junit.Assert;
import org.junit.Test;

import uk.co.akm.test.imagesearch.helper.MockBitmap;
import uk.co.akm.test.imagesearch.helper.MockBitmapFactory;
import uk.co.akm.test.imagesearch.process.model.window.Window;

public class ColourHistogramClassTest {
    private static final int DX_INDEX = 0;
    private static final int DY_INDEX = 1;

    private final int nSideDivs = 51;
    @Test
    public void shouldFillColourHistogram() {
        final int width = 10;
        final int height = 10;
        final MockBitmap image = MockBitmapFactory.randomMockBitmapInstance(width, height);
        final Window window = new Window(0, 0, width, height);

        final ColourHistogram underTest = new ColourHistogram(nSideDivs);
        underTest.fillColourHistogramForWindow(image, window);

        final ColourHistogram copyUnderTest = new ColourHistogram(underTest);

        final int diff = underTest.diff(copyUnderTest);
        Assert.assertEquals(0, diff);
    }

    @Test
    public void shouldShiftColourHistogramLeft() {
        shiftColourHistogramTest(ColourHistogram.SHIFT_LEFT);
    }

    @Test
    public void shouldShiftColourHistogramRight() {
        shiftColourHistogramTest(ColourHistogram.SHIFT_RIGHT);
    }

    @Test
    public void shouldShiftColourHistogramUp() {
        shiftColourHistogramTest(ColourHistogram.SHIFT_UP);
    }

    @Test
    public void shouldShiftColourHistogramDown() {
        shiftColourHistogramTest(ColourHistogram.SHIFT_DOWN);
    }

    @Test
    public void shouldShiftColourHistogramLeftUp() {
        shiftColourHistogramTest(ColourHistogram.SHIFT_LEFT_UP);
    }

    @Test
    public void shouldShiftColourHistogramLeftDown() {
        shiftColourHistogramTest(ColourHistogram.SHIFT_LEFT_DOWN);
    }

    @Test
    public void shouldShiftColourHistogramRightUp() {
        shiftColourHistogramTest(ColourHistogram.SHIFT_RIGHT_UP);
    }

    @Test
    public void shouldShiftColourHistogramRightDown() {
        shiftColourHistogramTest(ColourHistogram.SHIFT_RIGHT_DOWN);
    }

    private void shiftColourHistogramTest(int shiftDirection) {
        final int[] dxy = getShift(shiftDirection);
        final int dx = dxy[DX_INDEX];
        final int dy = dxy[DY_INDEX];

        final int width = 15;
        final int height = 15;
        final MockBitmap image = MockBitmapFactory.randomMockBitmapInstance(width, height);

        final Window window = new Window(7, 7,4, 4);
        final ColourHistogram colourHistogram = new ColourHistogram(nSideDivs);
        colourHistogram.fillColourHistogramForWindow(image, window);

        final Window shiftedWindow = new Window(window.xMin + dx, window.yMin + dy, window.width, window.height);
        final ColourHistogram expectedColourHistogram = new ColourHistogram(nSideDivs);
        expectedColourHistogram.fillColourHistogramForWindow(image, shiftedWindow);
        Assert.assertTrue(expectedColourHistogram.diff(colourHistogram) > 0);

        final ColourHistogram shiftedColourHistogram = new ColourHistogram(colourHistogram);
        shiftedColourHistogram.fillColourHistogramForShiftedWindow(image, window, shiftDirection);
        Assert.assertEquals(0, expectedColourHistogram.diff(shiftedColourHistogram));
    }

    private int[] getShift(int shiftDirection) {
        switch (shiftDirection) {
            case ColourHistogram.SHIFT_LEFT: return new int[]{-1, 0};

            case ColourHistogram.SHIFT_RIGHT: return new int[]{1, 0};

            case ColourHistogram.SHIFT_UP: return new int[]{0, -1};

            case ColourHistogram.SHIFT_DOWN: return new int[]{0, 1};

            case ColourHistogram.SHIFT_LEFT_UP: return new int[]{-1, -1};

            case ColourHistogram.SHIFT_LEFT_DOWN: return new int[]{-1, 1};

            case ColourHistogram.SHIFT_RIGHT_UP: return new int[]{1, -1};

            case ColourHistogram.SHIFT_RIGHT_DOWN: return new int[]{1, 1};

            default:
                throw new IllegalArgumentException("Unexpected shift direction: " + shiftDirection);
        }
    }
}
