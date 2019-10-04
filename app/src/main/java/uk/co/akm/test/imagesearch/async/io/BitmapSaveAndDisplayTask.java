package uk.co.akm.test.imagesearch.async.io;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import uk.co.akm.test.imagesearch.photo.PhotoIO;
import uk.co.akm.test.imagesearch.photo.impl.PhotoIOImpl;

public final class BitmapSaveAndDisplayTask extends AsyncTask<BitmapSaveParams, Void, String> {
    private static final String TAG = BitmapSaveAndDisplayTask.class.getName();

    private final PhotoIO photoIO = new PhotoIOImpl();

    private final ImageDisplayWithAfterAction<String> parent;

    public BitmapSaveAndDisplayTask(ImageDisplayWithAfterAction<String> parent) {
        this.parent = parent;
    }

    public void saveAndDisplay(Bitmap bitmap, String photoName) {
        execute(new BitmapSaveParams(bitmap, photoName));
    }

    @Override
    protected String doInBackground(BitmapSaveParams... params) {
        return saveBitmap(params);
    }

    private String saveBitmap(BitmapSaveParams... params) {
        try {
            return photoIO.writeImage(parent.getContext(), params[0].bitmap, params[0].photoName) ? params[0].photoName : null;
        } catch (Exception e) {
            Log.e(TAG, "Error when trying to save the selected image section.", e);
            return null;
        } finally {
            recycleBitmap(params);
        }
    }

    private void recycleBitmap(BitmapSaveParams... params) {
        try {
            params[0].bitmap.recycle();
        } catch (Exception e) {
            Log.e(TAG, "Error when trying to recycle the saved bitmap.", e);
        }
    }

    @Override
    protected void onPostExecute(String photoName) {
        displayBitmap(photoName);
        parent.afterDisplay();
    }

    private void displayBitmap(String photoName) {
        if (photoName != null) {
            try {
                parent.display(photoName);
            } catch (Exception e) {
                Log.e(TAG, "Error when trying to display the saved image section.", e);
            }
        }
    }

    @Override
    protected void onCancelled(String s) {
        parent.afterDisplay();
    }
}
