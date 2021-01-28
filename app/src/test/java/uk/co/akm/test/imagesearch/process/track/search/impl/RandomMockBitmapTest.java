package uk.co.akm.test.imagesearch.process.track.search.impl;

import junit.framework.Assert;

import org.junit.Test;

import uk.co.akm.test.imagesearch.helper.MockBitmap;
import uk.co.akm.test.imagesearch.helper.MockBitmapFactory;
import uk.co.akm.test.imagesearch.process.util.ColourHelper;

public class RandomMockBitmapTest {

    @Test
    public void shouldProduceRandomMockBitmap() {
        final int width = 20;
        final int height = 10;
        final MockBitmap underTest = MockBitmapFactory.randomMockBitmapInstance(width, height);

        Assert.assertNotNull(underTest);
        Assert.assertEquals(width, underTest.getWidth());
        Assert.assertEquals(height, underTest.getHeight());
        assertPixels(width, height, underTest);
    }

    private void assertPixels(int width, int height, MockBitmap underTest) {
        for (int y=0 ; y<height ; y++) {
            for (int x=0 ; x<width ; x++) {
                final int rgb = underTest.getPixel(x, y);

                final int red = ColourHelper.getRed(rgb);
                Assert.assertTrue(red >= 0);
                Assert.assertTrue(red <= 255);

                final int green = ColourHelper.getGreen(rgb);
                Assert.assertTrue(green >= 0);
                Assert.assertTrue(green <= 255);

                final int blue = ColourHelper.getBlue(rgb);
                Assert.assertTrue(blue >= 0);
                Assert.assertTrue(blue <= 255);
            }
        }
    }
}