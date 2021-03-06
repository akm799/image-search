package uk.co.akm.test.imagesearch.async.io;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import uk.co.akm.test.imagesearch.photo.BitmapFunctions;
import uk.co.akm.test.imagesearch.photo.PhotoIO;
import uk.co.akm.test.imagesearch.photo.impl.PhotoIOImpl;
import uk.co.akm.test.imagesearch.process.impl.ColourHistogramBuilder;
import uk.co.akm.test.imagesearch.process.model.window.Window;
import uk.co.akm.test.imagesearch.process.track.search.impl.map.ColourHistogram;
import uk.co.akm.test.imagesearch.store.Store;

public final class ColourHistogramSaveTask extends AsyncTask<String, Void, Boolean>  {
    private static final String TAG = ColourHistogramSaveTask.class.getName();

    private final PhotoIO photoIO = new PhotoIOImpl();

    private final ImageDisplay<Boolean> parent;

    public ColourHistogramSaveTask(ImageDisplay<Boolean> parent) {
        this.parent = parent;
    }

    @Override
    protected void onPreExecute() {
        if (parent != null) {
            parent.onProcessingStarted();
        }
    }

    @Override
    protected Boolean doInBackground(String... params) {
        if (parent == null || params == null) {
            Log.e(TAG, "Error: null parent and/or task input parameters.");
            return false;
        }

        if (params.length < 1) {
            Log.e(TAG, "Error: empty task input parameters.");
            return false;
        }

        final Context context = parent.getContext();
        final String photoName = params[0];
        if (context == null || photoName == null) {
            Log.e(TAG, "Error: null parent context and/or input stored image name.");
            return false;
        }

        return process(context, photoName);
    }

    private boolean process(Context context, String photoName) {
        final Window selection = Store.getWindow(context);
        if (selection == null) {
            Log.e(TAG, "Error: no selected image section window found.");
            return false;
        }

        final Bitmap image = readBitmap(photoName);
        if (image == null) {
            Log.e(TAG, "Error: no stored image found.");
            return false;
        }

        try {
            final ColourHistogram colourHistogram = processBitmap(image, selection);
            Store.saveColourHistogram(context, colourHistogram);
            photoIO.deleteCapturedImage(context, photoName);
        } catch (Exception e) {
            Log.e(TAG, "Error when calculating and saving the colour histogram.", e);
            return false;
        }

        return true;
    }

    private Bitmap readBitmap(String photoName) {
        try {
            return photoIO.readCapturedImage(parent.getContext(), photoName);
        } catch (Exception e) {
            Log.e(TAG, "Error when trying to read the saved image section.", e);
            return null;
        }
    }

    private ColourHistogram processBitmap(Bitmap image, Window selection) {
        final Bitmap rotated = BitmapFunctions.quarterRotateClockwise(image, true);
        final ColourHistogramBuilder colourHistogramBuilder = new ColourHistogramBuilder(selection);

        return colourHistogramBuilder.processImage(rotated);
    }

    @Override
    protected void onPostExecute(Boolean success) {
        if (parent != null) {
            parent.display(success);
        }
    }
}
