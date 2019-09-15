package uk.co.akm.test.imagesearch.view.combined.window;


final class RangeFunctions {

    static boolean inRange(float start1, float size1, int maxSize1, float start2, float size2, int maxSize2) {
        return inRange(start1, size1, maxSize1) && inRange(start2, size2, maxSize2);
    }

    static boolean inRange(float start, float size, int maxSize) {
        return start > 0 && start + size < maxSize;
    }

    private RangeFunctions() {}
}
