package uk.co.akm.test.imagesearch.helper;

abstract class AbstractMockBitmap implements MockBitmap {
    private final int width;
    private final int height;
    private final int[] pixels;

    public AbstractMockBitmap(int width, int height) {
        checkArguments(width, height);

        this.width = width;
        this.height = height;
        this.pixels = new int[width*height];
        fillPixels(pixels);
    }

    private void checkArguments(int width, int height) {
        if (width <= 0) {
            throw new IllegalArgumentException("Illegal 'width' argument: " + width + ". It must be greater than zero.");
        }

        if (height <= 0) {
            throw new IllegalArgumentException("Illegal 'height' argument: " + height + ". It must be greater than zero.");
        }
    }

    abstract void fillPixels(int[] pixels);

    @Override
    public final int getWidth() {
        return width;
    }

    @Override
    public final int getHeight() {
        return height;
    }

    @Override
    public final int getPixel(int x, int y) {
        checkPixelArguments(x, y);

        return pixels[y*width + x];
    }

    private void checkPixelArguments(int x, int y) {
        if (x < 0 || x >= width) {
            throw new IllegalArgumentException("Illegal pixel 'x' argument: " + x + ". It must be 0 <= x < " + width + ".");
        }

        if (y < 0 || y >= height) {
            throw new IllegalArgumentException("Illegal pixel 'y' argument: " + y + ". It must be 0 <= y < " + height + ".");
        }
    }
}
