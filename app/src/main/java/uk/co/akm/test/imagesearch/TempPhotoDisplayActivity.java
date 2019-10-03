package uk.co.akm.test.imagesearch;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import uk.co.akm.test.imagesearch.photo.PhotoIO;
import uk.co.akm.test.imagesearch.photo.impl.PhotoIOImpl;

//TODO Delete or rename this class to indicate it is used only fore debugging.
// Debug activity to display the small image we have captured so we know it has been captured correctly.
public final class TempPhotoDisplayActivity extends AppCompatActivity {
    private static final String PHOTO_NAME_ARG_KEY = "TempPhotoDisplayActivity.Small.Photo.Name.Arg_key";

    public static void start(Activity parent, String photoName) {
        final Intent intent = new Intent(parent, TempPhotoDisplayActivity.class);
        intent.putExtra(PHOTO_NAME_ARG_KEY, photoName);

        parent.startActivity(intent);
    }

    private final PhotoIO photoIO = new PhotoIOImpl();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_small_photo_display);

        final String photoName = getIntent().getStringExtra(PHOTO_NAME_ARG_KEY);
        if (photoName != null) {
            final Bitmap smallPhoto = photoIO.readCapturedImage(this, photoName);
            ((ImageView) findViewById(R.id.smallPhotoView)).setImageBitmap(smallPhoto);
        }
    }

    @Override
    public void onBackPressed() {
        final String photoName = getIntent().getStringExtra(PHOTO_NAME_ARG_KEY);
        if (photoName != null) {
            photoIO.deleteCapturedImage(this, photoName);
        }

        super.onBackPressed();
    }
}