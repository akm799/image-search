package uk.co.akm.test.imagesearch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import uk.co.akm.test.imagesearch.async.io.BitmapReadAndDisplayTask;
import uk.co.akm.test.imagesearch.photo.PhotoIO;
import uk.co.akm.test.imagesearch.photo.impl.PhotoIOImpl;

/**
 * This activity is used to display the image section selected and captured in the PhotoDisplayActivity.
 * The only purpose for such a display is for debugging, i.e. making sure that the selected image section
 * was captured correctly.
 */
public final class DebugPhotoDisplayActivity extends AppCompatActivity {
    private static final String PHOTO_NAME_ARG_KEY = "DebugPhotoDisplayActivity.Small.Photo.Name.Arg_key";

    public static void start(Activity parent, String photoName) {
        final Intent intent = new Intent(parent, DebugPhotoDisplayActivity.class);
        intent.putExtra(PHOTO_NAME_ARG_KEY, photoName);

        parent.startActivity(intent);
    }

    private final PhotoIO photoIO = new PhotoIOImpl();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug_small_photo_display);
    }

    @Override
    protected void onStart() {
        super.onStart();

        final String photoName = getIntent().getStringExtra(PHOTO_NAME_ARG_KEY);
        if (photoName != null) {
            final ImageView imageView = findViewById(R.id.smallPhotoView);
            new BitmapReadAndDisplayTask(this.getApplicationContext(), imageView).execute(photoName);
        }
    }

    @Override
    public void onBackPressed() {
        try {
            final String photoName = getIntent().getStringExtra(PHOTO_NAME_ARG_KEY);
            if (photoName != null) {
                photoIO.deleteCapturedImage(this, photoName);
            }
        } finally {
            super.onBackPressed();
        }
    }
}