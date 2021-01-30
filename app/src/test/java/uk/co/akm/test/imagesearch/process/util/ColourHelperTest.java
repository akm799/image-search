package uk.co.akm.test.imagesearch.process.util;

import junit.framework.Assert;

import org.junit.Test;

public class ColourHelperTest {

    @Test
    public void shouldDecompose() {
        decomposeTest(0xFFFF0000, 0xFF, 0x00, 0x00);
        decomposeTest(0xFF00FF00, 0x00, 0xFF, 0x00);
        decomposeTest(0xFF0000FF, 0x00, 0x00, 0xFF);
        decomposeTest(0xFF35D39A, 0x35, 0xD3, 0x9A);
    }

    private void decomposeTest(int rgb, int r, int g, int b) {
        Assert.assertEquals(r, ColourHelper.getRed(rgb));
        Assert.assertEquals(g, ColourHelper.getGreen(rgb));
        Assert.assertEquals(b, ColourHelper.getBlue(rgb));
    }

    @Test
    public void shouldCompose() {
        composeTest(0xFF, 0x00, 0x00, 0xFFFF0000);
        composeTest(0x00, 0xFF, 0x00, 0xFF00FF00);
        composeTest(0x00, 0x00, 0xFF, 0xFF0000FF);
        composeTest(0x35, 0xD3, 0x9A, 0xFF35D39A);
    }

    private void composeTest(int r, int g, int b, int rgb) {
        Assert.assertEquals(rgb, ColourHelper.getRgb(r, g, b));
    }
}
