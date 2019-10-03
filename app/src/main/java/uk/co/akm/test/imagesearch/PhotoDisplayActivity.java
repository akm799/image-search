package uk.co.akm.test.imagesearch;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import uk.co.akm.test.imagesearch.async.io.BitmapSaveAndDisplayTask;
import uk.co.akm.test.imagesearch.view.combined.PhotoWindowView;

public class PhotoDisplayActivity extends AppCompatActivity {
    private static final String TAG = PhotoDisplayActivity.class.getName();
    private static final String PHOTO_NAME_ARG_KEY = "PhotoDisplayActivity.Photo.Name.Arg_key";

    private PhotoWindowView photoView;

    static void start(Activity parent, String photoName) {
        final Intent intent = new Intent(parent, PhotoDisplayActivity.class);
        intent.putExtra(PHOTO_NAME_ARG_KEY, photoName);

        parent.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_display);

        photoView = findViewById(R.id.photoView);

        final String photoName = getIntent().getStringExtra(PHOTO_NAME_ARG_KEY);
        if (photoName != null) {
            photoView.setPhoto(photoName);
        }
    }

    @Override
    public void onBackPressed() {
        try {
            if (!saveAndDisplaySelectedImageSection()) {
                super.onBackPressed();
            }
            // Back is going to be 'pressed' when the asynchronous task we launch finishes.
        } catch (Exception e) {
            Log.e(TAG, "Error when overriding the back button press.", e);
            super.onBackPressed();
        } finally {
            clear();
        }
    }

    private boolean saveAndDisplaySelectedImageSection() {
        final Bitmap smallImageBitmap = photoView.getInternalWindowBitmap();
        if (smallImageBitmap != null) {
            new BitmapSaveAndDisplayTask(this).saveAndDisplay(smallImageBitmap, "selected_section");
            return true;
        } else {
            return false;
        }
    }

    private void clear() {
        try {
            photoView.clear();
        } catch (Exception e) {
            Log.e(TAG, "Error when clearing the photo (selection) view.", e);
        }
    }
}