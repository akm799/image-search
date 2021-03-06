package uk.co.akm.test.imagesearch.async.io;

public interface ImageDisplay<I> extends ImageProcessingParent {

    void display(I image);
}
