package uk.co.akm.test.imagesearch.process.track.search;

import android.graphics.Bitmap;

import uk.co.akm.test.imagesearch.process.model.window.Window;


public interface BestMatchFinder {

    Window findBestMatch(Bitmap targetImage, Window targetWindow, Bitmap image);
}
