package uk.co.akm.test.imagesearch;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import uk.co.akm.test.imagesearch.photo.PhotoFunctions;
import uk.co.akm.test.imagesearch.photo.impl.FilePhotoFunctions;
import uk.co.akm.test.imagesearch.view.display.PhotoWindowView;

public class PhotoDisplayActivity extends AppCompatActivity {
    private final PhotoFunctions photoFunctions = new FilePhotoFunctions();

    static void start(Activity parent) {
        parent.startActivity(new Intent(parent, PhotoDisplayActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_display);

        displayCapturedPhoto((PhotoWindowView) findViewById(R.id.photoView));
    }

    private void displayCapturedPhoto(PhotoWindowView imageView) {
        try {
            final Bitmap bitmap = photoFunctions.readCapturedImage(this);
            imageView.setPhoto(rotateBitmap(bitmap));
        } catch (Exception e) {
            Log.e("PhotoDisplayActivity", "Error while reading the captured image.", e);
            Toast.makeText(this, "Could not read the captured image.", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap rotateBitmap(Bitmap bitmap) {
        final Matrix rotation = new Matrix();
        rotation.postRotate(90);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), rotation, true);
    }
}
