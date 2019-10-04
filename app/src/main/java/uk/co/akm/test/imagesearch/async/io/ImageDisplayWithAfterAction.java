package uk.co.akm.test.imagesearch.async.io;

public interface ImageDisplayWithAfterAction<T> extends ImageDisplay<T> {

    void afterDisplay();
}
