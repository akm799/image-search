package uk.co.akm.test.imagesearch.process.track.search;


import uk.co.akm.test.imagesearch.process.model.window.Window;
import uk.co.akm.test.imagesearch.process.track.search.impl.map.PixelMap;


public interface BestMatchFinder {

    Window findBestMatch(PixelMap image);
}
