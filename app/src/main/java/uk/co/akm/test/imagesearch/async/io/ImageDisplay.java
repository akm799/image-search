package uk.co.akm.test.imagesearch.async.io;

import android.content.Context;

public interface ImageDisplay<I> {

    Context getContext();

    void onProcessingStarted();

    void display(I image);
}
