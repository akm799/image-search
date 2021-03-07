package uk.co.akm.test.imagesearch;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import uk.co.akm.test.imagesearch.async.io.ColourHistogramSaveTask;
import uk.co.akm.test.imagesearch.async.io.ImageDisplay;
import uk.co.akm.test.imagesearch.store.Store;
import uk.co.akm.test.imagesearch.view.combined.PhotoWindowView;

public class WindowSelectionActivity extends AppCompatActivity implements ImageDisplay<Boolean> {
    private static final String TAG = WindowSelectionActivity.class.getName();
    private static final String PHOTO_NAME_ARG_KEY = "PhotoDisplayActivity.Photo.Name.Arg_key";

    private AlertDialog saveDialog;
    private PhotoWindowView photoView;

    public static void start(Activity parent, String photoName) {
        final Intent intent = new Intent(parent, WindowSelectionActivity.class);
        intent.putExtra(PHOTO_NAME_ARG_KEY, photoName);

        parent.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_window_selection);

        photoView = findViewById(R.id.photoView);

        final String photoName = getIntent().getStringExtra(PHOTO_NAME_ARG_KEY);
        if (photoName != null) {
            photoView.setPhoto(photoName);
        }
    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }

    @Override
    public void onProcessingStarted() {
        findViewById(R.id.windowSelectionProgress).setVisibility(View.VISIBLE);
    }

    @Override
    public void display(Boolean success) {
        if (success) {
            Log.d(TAG, "Saved the colour histogram for the selected image section.");
            finish();
        } else {
            Log.e(TAG, "Could not save the colour histogram for the selected image section.");
            findViewById(R.id.windowSelectionProgress).setVisibility(View.GONE);
            Toast.makeText(this, "Could not save the colour histogram for the selected image section.", Toast.LENGTH_SHORT).show();
        }
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
                        WindowSelectionActivity.super.onBackPressed();
                    }
                }).show();
    }

    private void onSaveImageSelection() {
        if (saveSelectedImageWindow()) {
            final String photoName = getIntent().getStringExtra(PHOTO_NAME_ARG_KEY);
            (new ColourHistogramSaveTask(this)).execute(photoName);
        }
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