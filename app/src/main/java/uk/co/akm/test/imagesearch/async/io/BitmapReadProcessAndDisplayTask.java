package uk.co.akm.test.imagesearch.async.io;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import uk.co.akm.test.imagesearch.photo.BitmapFunctions;
import uk.co.akm.test.imagesearch.photo.PhotoIO;
import uk.co.akm.test.imagesearch.photo.impl.PhotoIOImpl;
import uk.co.akm.test.imagesearch.process.ImageProcessor;
import uk.co.akm.test.imagesearch.process.impl.SearchImageProcessor;
import uk.co.akm.test.imagesearch.process.model.window.Window;
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
        parent.onProcessingStarted();
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        final Window selection = Store.getWindow(parent.getContext());
        if (selection == null) {
            Log.e(TAG, "Error: no selected image section window found.");
            return null;
        }

        final Bitmap input = readBitmap(params[0]);
        final Bitmap output = processBitmap(input, selection);
        Store.removeWindow(parent.getContext());

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

    private Bitmap processBitmap(Bitmap input, Window selection) {
        final Bitmap rotated = BitmapFunctions.quarterRotateClockwise(input, true);

        final int rgb = ColourHelper.getRgb(0, 255, 0);
        final ImageProcessor windowImageProcessor = new SearchImageProcessor(selection, rgb);
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
