package uk.co.akm.test.imagesearch;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import uk.co.akm.test.imagesearch.photo.PhotoFunctions;
import uk.co.akm.test.imagesearch.photo.impl.FilePhotoFunctions;
import uk.co.akm.test.imagesearch.photo.impl.PhotoFunctionsImpl;

public class PhotoActivity extends AppCompatActivity {
    private final PhotoFunctions photoFunctions = new FilePhotoFunctions();

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        imageView = findViewById(R.id.photoView);
    }

    public void onPhoto(View view) {
        photoFunctions.initiateImageCapture(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (photoFunctions.imageCaptured(requestCode, resultCode)) {
            try {
                final Bitmap bitmap = photoFunctions.readCapturedImage(this, data);
                imageView.setImageBitmap(bitmap);
            } catch (Exception e) {
                Log.e("PhotoActivity", "Error while reading the captured image.", e);
                Toast.makeText(this, "Could not read the captured image.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
