package uk.co.akm.test.imagesearch.async.io;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import uk.co.akm.test.imagesearch.photo.PhotoIO;
import uk.co.akm.test.imagesearch.photo.impl.PhotoIOImpl;
import uk.co.akm.test.imagesearch.process.model.window.Window;
import uk.co.akm.test.imagesearch.store.Store;

public final class BitmapReadAndDisplayTask extends AsyncTask<String, Void, Bitmap> {
    private static final String TAG = BitmapReadAndDisplayTask.class.getName();

    private final PhotoIO photoIO = new PhotoIOImpl();

    private final ImageDisplay<Bitmap> parent;

    public BitmapReadAndDisplayTask(ImageDisplay<Bitmap> parent) {
        this.parent = parent;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        final Window selection = Store.getWindow(parent.getContext());
        if (selection == null) {
            Log.e(TAG, "Error: no selected image section window found.");
            return null;
        } else {
            Store.removeWindow(parent.getContext()); //TODO Use the selected window to cut out the image section to show.
        }

        final Bitmap bitmap = readBitmap(params[0]);

        return bitmap;
    }

    private Bitmap readBitmap(String photoName) {
        try {
            return photoIO.readCapturedImage(parent.getContext(), photoName);
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
