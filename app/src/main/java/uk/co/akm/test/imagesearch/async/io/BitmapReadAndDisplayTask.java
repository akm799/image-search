package uk.co.akm.test.imagesearch.async.io;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import uk.co.akm.test.imagesearch.photo.PhotoIO;
import uk.co.akm.test.imagesearch.photo.impl.PhotoIOImpl;

public final class BitmapReadAndDisplayTask extends AsyncTask<String, Void, Bitmap> {
    private static final String TAG = BitmapReadAndDisplayTask.class.getName();

    private final PhotoIO photoIO = new PhotoIOImpl();

    private final Context context;
    private final ImageView imageView;

    public BitmapReadAndDisplayTask(Context context, ImageView imageView) {
        this.context = context;
        this.imageView = imageView;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        try {
            return photoIO.readCapturedImage(context, params[0]);
        } catch (Exception e) {
            Log.e(TAG, "Error when trying to read the saved image section.", e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (bitmap != null && imageView != null) {
            imageView.setImageBitmap(bitmap);
        }
    }
}
