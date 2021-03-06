package uk.co.akm.test.imagesearch.async.io;

import android.content.Context;

public interface ImageProcessingParent {

    Context getContext();

    void onProcessingStarted();
}
