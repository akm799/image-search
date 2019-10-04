package uk.co.akm.test.imagesearch.async.io;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import uk.co.akm.test.imagesearch.photo.PhotoIO;
import uk.co.akm.test.imagesearch.photo.impl.PhotoIOImpl;

public final class BitmapReadAndDisplayTask extends AsyncTask<String, Void, Bitmap> {
    private static final String TAG = BitmapReadAndDisplayTask.class.getName();

    private final PhotoIO photoIO = new PhotoIOImpl();

    private final ImageDisplay<Bitmap> parent;

    public BitmapReadAndDisplayTask(ImageDisplay<Bitmap> parent) {
        this.parent = parent;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        try {
            return photoIO.readCapturedImage(parent.getContext(), params[0]);
        } catch (Exception e) {
            Log.e(TAG, "Error when trying to read the saved image section.", e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (bitmap != null && parent != null) {
            parent.display(bitmap);
        }
    }
}
