package uk.co.akm.test.imagesearch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import uk.co.akm.test.imagesearch.photo.PhotoFunctions;
import uk.co.akm.test.imagesearch.photo.impl.FilePhotoFunctions;

public class PhotoCaptureActivity extends AppCompatActivity {
    private final PhotoFunctions photoFunctions = new FilePhotoFunctions();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_capture);
    }

    public void onPhoto(View view) {
        photoFunctions.initiateImageCapture(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (photoFunctions.imageCaptured(requestCode, resultCode)) {
            PhotoDisplayActivity.start(this);
        }
    }
}
