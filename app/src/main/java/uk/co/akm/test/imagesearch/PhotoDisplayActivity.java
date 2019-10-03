package uk.co.akm.test.imagesearch;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import uk.co.akm.test.imagesearch.async.io.BitmapSaveAndDisplayTask;
import uk.co.akm.test.imagesearch.view.combined.PhotoWindowView;

public class PhotoDisplayActivity extends AppCompatActivity {
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
    public void onBackPressed() {
        if (photoView.hasInternalWindowBitmap()) {
            showSaveSelectionDialog();
        } else {
            clearAndPressBack();
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
                        clearAndPressBack();
                    }
                }).show();
    }

    private void onSaveImageSelection() {
        try {
            if (!saveAndDisplaySelectedImageSection()) {
                clearAndPressBack();
            }
            // We have an image selection and we have launched an asynchronous task to save and display it. The back-button is going to be 'pressed' when the task finishes.
        } catch (Exception e) {
            Log.e(TAG, "Error when overriding the back button press.", e);
            clearAndPressBack();
        }
    }

    private boolean saveAndDisplaySelectedImageSection() {
        final Bitmap smallImageBitmap = photoView.getInternalWindowBitmap();
        if (smallImageBitmap != null) {
            new BitmapSaveAndDisplayTask(this).saveAndDisplay(smallImageBitmap, "selected_section");
            return true;
        } else {
            return false;
        }
    }

    public final void clearAndPressBack() {
        clear();
        super.onBackPressed();
    }

    private void clear() {
        try {
            photoView.clear();
        } catch (Exception e) {
            Log.e(TAG, "Error when clearing the photo (selection) view.", e);
        }
    }

    private void dismissSaveDialog() {
        if (saveDialog != null) {
            saveDialog.dismiss();
            saveDialog = null;
        }
    }
}