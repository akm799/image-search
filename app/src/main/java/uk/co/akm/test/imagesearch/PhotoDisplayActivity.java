package uk.co.akm.test.imagesearch;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import uk.co.akm.test.imagesearch.async.io.ImageDisplayWithAfterAction;
import uk.co.akm.test.imagesearch.store.Store;
import uk.co.akm.test.imagesearch.view.combined.PhotoWindowView;

public class PhotoDisplayActivity extends AppCompatActivity implements ImageDisplayWithAfterAction<String> {
    private static final String TAG = PhotoDisplayActivity.class.getName();
    private static final String PHOTO_NAME_ARG_KEY = "PhotoDisplayActivity.Photo.Name.Arg_key";

    private AlertDialog saveDialog;
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
    public void afterDisplay() {
        super.onBackPressed();
    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }

    @Override
    public void onProcessingStarted() {}

    @Override
    public void display(String photoName) {
        DebugPhotoDisplayActivity.start(this, photoName);
    }

    @Override
    public void onBackPressed() {
        if (photoView.hasInternalWindowBitmap()) {
            showSaveSelectionDialog();
        } else {
            super.onBackPressed();
        }
    }

    private void showSaveSelectionDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Save Selection")
                .setMessage("Do you want to save the selected image section?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        dismissSaveDialog();
                        onSaveImageSelection();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        dismissSaveDialog();
                        PhotoDisplayActivity.super.onBackPressed();
                    }
                }).show();
    }

    private void onSaveImageSelection() {
        if (saveSelectedImageWindow()) {
            final String photoName = getIntent().getStringExtra(PHOTO_NAME_ARG_KEY);
            DebugPhotoDisplayActivity.start(this, photoName);
        }

        super.onBackPressed();
    }

    private boolean saveSelectedImageWindow() {
        if (photoView.hasInternalWindowBitmap()) {
            Store.putWindow(this, photoView.getInternalWindowInBitmapScale());
            return true;
        } else {
            return false;
        }
    }

    private void dismissSaveDialog() {
        if (saveDialog != null) {
            saveDialog.dismiss();
            saveDialog = null;
        }
    }
}