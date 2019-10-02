package uk.co.akm.test.imagesearch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import uk.co.akm.test.imagesearch.view.combined.PhotoWindowView;

public class PhotoDisplayActivity extends AppCompatActivity {
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
        final String smallImageName = "selected_section";
        if (photoView.saveInternalWindowBitmap(this, smallImageName)) {
            TempPhotoDisplayActivity.start(this, smallImageName);
        }

        photoView.clear();
        super.onBackPressed();
    }
}