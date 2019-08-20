package uk.co.akm.test.imagesearch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import uk.co.akm.test.imagesearch.photo.PhotoHandler;
import uk.co.akm.test.imagesearch.photo.impl.PhotoHandlerImpl;

public class PhotoCaptureActivity extends AppCompatActivity {
    private final PhotoHandler photoHandler = new PhotoHandlerImpl();

    public static final String PHOTO_NAME = "target";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_capture);
    }

    public void onPhoto(View view) {
        photoHandler.initiateImageCapture(this, PHOTO_NAME);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (photoHandler.imageCaptured(requestCode, resultCode)) {
            PhotoDisplayActivity.start(this);
        }
    }
}