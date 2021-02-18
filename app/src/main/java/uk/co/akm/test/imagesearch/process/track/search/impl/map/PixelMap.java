package uk.co.akm.test.imagesearch.process.track.search.impl.map;

public final class PixelMap {
    private final int width;
    private final int height;
    private final int[] values;

    public PixelMap(int width, int[] values) {
        checkArguments(width, values);

        this.width = width;
        this.height = values.length/width;
        this.values = values;
    }

    private void checkArguments(int width, int[] values) {
        if (values.length < width) {
            throw new IllegalArgumentException("Input 'values' array length (" + values.length + ") must be a multiple of the input 'width' (" + width + ").");
        }

        final int i = values.length/width;
        final float f = values.length/(float)width;
        if ((float)i != f) {
            throw new IllegalArgumentException("Input 'values' array length (" + values.length + ") must be a multiple of the input 'width' (" + width + ").");
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getPixel(int x, int y) {
        return values[x + y*width];
    }
}
