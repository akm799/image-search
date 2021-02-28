package uk.co.akm.test.imagesearch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import uk.co.akm.test.imagesearch.async.io.BitmapReadProcessDebugAndDisplayTask;
import uk.co.akm.test.imagesearch.async.io.ImageDisplay;
import uk.co.akm.test.imagesearch.photo.PhotoIO;
import uk.co.akm.test.imagesearch.photo.impl.PhotoIOImpl;

/**
 * This activity is used to display the image section selected and captured in the PhotoDisplayActivity.
 * The only purpose for such a display is for debugging, i.e. making sure that the selected image section
 * was captured correctly.
 */
public final class DebugPhotoDisplayActivity extends AppCompatActivity implements ImageDisplay<Bitmap> {
    private static final String PHOTO_NAME_ARG_KEY = "DebugPhotoDisplayActivity.Small.Photo.Name.Arg_key";

    public static void start(Activity parent, String photoName) {
        final Intent intent = new Intent(parent, DebugPhotoDisplayActivity.class);
        intent.putExtra(PHOTO_NAME_ARG_KEY, photoName);

        parent.startActivity(intent);
    }

    private final PhotoIO photoIO = new PhotoIOImpl();

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug_small_photo_display);

        imageView = findViewById(R.id.smallPhotoView);
    }

    @Override
    protected void onStart() {
        super.onStart();

        final String photoName = getIntent().getStringExtra(PHOTO_NAME_ARG_KEY);
        if (photoName != null) {
            new BitmapReadProcessDebugAndDisplayTask(this).execute(photoName);
        }
    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }

    @Override
    public void onProcessingStarted() {
        findViewById(R.id.debugProcessingProgress).setVisibility(View.VISIBLE);
    }

    @Override
    public void display(Bitmap image) {
        findViewById(R.id.debugProcessingProgress).setVisibility(View.GONE);
        if (imageView != null) {
            imageView.setImageBitmap(image);
        }
    }

    @Override
    public void onBackPressed() {
        try {
            deleteCapturedImage();
        } finally {
            super.onBackPressed();
        }
    }

    private void deleteCapturedImage() {
        final String photoName = getIntent().getStringExtra(PHOTO_NAME_ARG_KEY);
        if (photoName != null) {
            photoIO.deleteCapturedImage(this, photoName);
        }
    }
}