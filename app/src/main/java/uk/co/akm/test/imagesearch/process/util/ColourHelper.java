package uk.co.akm.test.imagesearch.process.util;

public class ColourHelper {
    private static final int MAX_ALPHA = 255;
    private static final byte MAX_ALPHA_BYTE = (byte)MAX_ALPHA;

    public static int getAlpha(int rgb) {
        return (rgb >> 24) & 0xFF;
    }

    public static int getRed(int rgb) {
        return (rgb >> 16) & 0xFF;
    }

    public static int getGreen(int rgb) {
        return (rgb >> 8) & 0xFF;
    }

    public static int getBlue(int rgb) {
        return (rgb >> 0) & 0xFF;
    }

    public static int getRgb(int red, int green, int blue) {
        return getRgb(red, green, blue, MAX_ALPHA);
    }

    public static int getRgb(int red, int green, int blue, int alpha) {
        return  ((alpha   & 0xFF) << 24) |
                ((red     & 0xFF) << 16) |
                ((green   & 0xFF) <<  8) |
                ((blue    & 0xFF) <<  0);
    }

    public static int toGrayScale(int rgb) {
        return (getRed(rgb) + getGreen(rgb) + getBlue(rgb))/3;
    }

    private ColourHelper() {}
}
