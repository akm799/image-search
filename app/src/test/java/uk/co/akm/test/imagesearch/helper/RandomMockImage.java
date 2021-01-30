package uk.co.akm.test.imagesearch.helper;

import java.util.Random;

final class RandomMockImage extends AbstractMockBitmap {

    RandomMockImage(int width, int height) {
        super(width, height);
    }

    @Override
    final void fillPixels(int[] pixels) {
        final Random random = new Random(System.currentTimeMillis());
        fillRandomPixels(random, pixels);
    }

    private void fillRandomPixels(Random random, int[] pixels) {
        for (int i=0 ; i<pixels.length ; i++) {
            pixels[i] = 0xFF000000 | random.nextInt(0x1000000);
        }
    }
}
