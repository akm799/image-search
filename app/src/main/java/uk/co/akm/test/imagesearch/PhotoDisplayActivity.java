package uk.co.akm.test.imagesearch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import uk.co.akm.test.imagesearch.view.display.persistent.PhotoView;

public class PhotoDisplayActivity extends AppCompatActivity {
    private static final String PHOTO_NAME_ARG_KEY = "PhotoDisplayActivity.Photo.Name.Arg_key";

    static void start(Activity parent, String photoName) {
        final Intent intent = new Intent(parent, PhotoDisplayActivity.class);
        intent.putExtra(PHOTO_NAME_ARG_KEY, photoName);

        parent.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_display);

        final String photoName = getIntent().getStringExtra(PHOTO_NAME_ARG_KEY);
        if (photoName != null) {
            ((PhotoView) findViewById(R.id.photoView)).setPhoto(photoName);
        }
    }

    @Override
    public void onBackPressed() {
        ((PhotoView) findViewById(R.id.photoView)).clear();
        super.onBackPressed();
    }
}