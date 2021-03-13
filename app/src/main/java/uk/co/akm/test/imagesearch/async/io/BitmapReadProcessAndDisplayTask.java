package uk.co.akm.test.imagesearch.async.io;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import uk.co.akm.test.imagesearch.photo.BitmapFunctions;
import uk.co.akm.test.imagesearch.photo.PhotoIO;
import uk.co.akm.test.imagesearch.photo.impl.PhotoIOImpl;
import uk.co.akm.test.imagesearch.process.ImageProcessor;
import uk.co.akm.test.imagesearch.process.impl.SearchImageProcessor;
import uk.co.akm.test.imagesearch.process.model.window.Window;
import uk.co.akm.test.imagesearch.process.track.search.impl.map.ColourHistogram;
import uk.co.akm.test.imagesearch.process.util.ColourHelper;
import uk.co.akm.test.imagesearch.store.Store;

public final class BitmapReadProcessAndDisplayTask extends AsyncTask<String, Void, Bitmap> {
    private static final String TAG = BitmapReadProcessAndDisplayTask.class.getName();

    private final PhotoIO photoIO = new PhotoIOImpl();

    private final ImageDisplay<Bitmap> parent;

    public BitmapReadProcessAndDisplayTask(ImageDisplay<Bitmap> parent) {
        this.parent = parent;
    }

    @Override
    protected void onPreExecute() {
        if (parent != null) {
            parent.onProcessingStarted();
        }
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        if (parent == null || params == null) {
            Log.e(TAG, "Error: null parent and/or task input parameters.");
            return null;
        }

        if (params.length < 1) {
            Log.e(TAG, "Error: empty task input parameters.");
            return null;
        }

        final Context context = parent.getContext();
        final String photoName = params[0];
        if (context == null || photoName == null) {
            Log.e(TAG, "Error: null parent context and/or input stored image name.");
            return null;
        }

        return process(context, photoName);
    }

    private Bitmap process(Context context, String photoName) {
        final Window selection = Store.getWindow(context);
        if (selection == null) {
            Log.e(TAG, "Error: no selected image section window found.");
            return null;
        }

        final ColourHistogram colourHistogram = Store.readColourHistogram(context);
        if (colourHistogram == null) {
            Log.e(TAG, "Error: no stored coloured histogram found.");
            return null;
        }

        final Bitmap input = readBitmap(photoName);
        final Bitmap output = processBitmap(input, selection, colourHistogram);
        Store.removeWindow(context);

        return output;
    }

    private Bitmap readBitmap(String photoName) {
        try {
            return photoIO.readCapturedImage(parent.getContext(), photoName);
        } catch (Exception e) {
            Log.e(TAG, "Error when trying to read the saved image section.", e);
            return null;
        }
    }

    private Bitmap processBitmap(Bitmap input, Window selection, ColourHistogram colourHistogram) {
        final Bitmap rotated = BitmapFunctions.quarterRotateClockwise(input, true);

        final int rgb = ColourHelper.getRgb(0, 255, 0);
        final ImageProcessor windowImageProcessor = new SearchImageProcessor(selection, colourHistogram, rgb);
        final Bitmap processed = windowImageProcessor.processImage(rotated);
        rotated.recycle();

        return processed;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (bitmap != null && parent != null) {
            parent.display(bitmap);
        }
    }
}
