package uk.co.akm.test.imagesearch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import uk.co.akm.test.imagesearch.debug.DebugWindowSelectionActivity;
import uk.co.akm.test.imagesearch.photo.PhotoHandler;
import uk.co.akm.test.imagesearch.photo.impl.PhotoHandlerImpl;
import uk.co.akm.test.imagesearch.store.Store;

public class PhotoCaptureActivity extends AppCompatActivity {
    private static final String PHOTO_NAME = "target";

    static void start(Activity parent) {
        parent.startActivity(new Intent(parent, PhotoCaptureActivity.class));
    }

    private final PhotoHandler photoHandler = new PhotoHandlerImpl();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_capture);
    }

    @Override
    protected void onResume() {
        super.onResume();

        setPhotoForSearchButtonVisibility();
    }

    private void setPhotoForSearchButtonVisibility() {
        final View button = findViewById(R.id.photoCaptureSearch);
        if (button != null) {
            if (!Store.isTestMode(this) && Store.hasColourHistogramFile(this)) {
                button.setVisibility(View.VISIBLE);
            } else {
                button.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void onPhotoMark(View view) {
        Store.deleteColourHistogram(this);
        photoHandler.initiateImageCapture(this, PHOTO_NAME);
    }

    public void onPhotoSearch(View view) {
        photoHandler.initiateImageCapture(this, PHOTO_NAME);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (photoHandler.imageCaptured(requestCode, resultCode)) {
            if (Store.isTestMode(this)) {
                startImageProcessingActivityTest();
            } else {
                startImageProcessingActivity();
            }
        }
    }

    private void startImageProcessingActivity() {
        if (Store.hasColourHistogramFile(this)) {
            ResultDisplayActivity.start(this, PHOTO_NAME);
        } else {
            WindowSelectionActivity.start(this, PHOTO_NAME);
        }
    }

    private void startImageProcessingActivityTest() {
        DebugWindowSelectionActivity.start(this, PHOTO_NAME);
    }
}
