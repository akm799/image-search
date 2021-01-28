package uk.co.akm.test.imagesearch.helper;

public class MockBitmapFactory {

    public static MockBitmap randomMockBitmapInstance(int width, int height) {
        return new RandomMockImage(width, height);
    }

    private MockBitmapFactory() {}
}
