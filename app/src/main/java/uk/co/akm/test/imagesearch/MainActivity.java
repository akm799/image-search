package uk.co.akm.test.imagesearch;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import uk.co.akm.test.imagesearch.store.Store;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onTestMode(View view) {
        startPhotoCaptureActivity(true);
    }

    public void onSearchMode(View view) {
        startPhotoCaptureActivity(false);
    }

    private void startPhotoCaptureActivity(boolean testMode) {
        Store.setTestMode(this, testMode);
        PhotoCaptureActivity.start(this);
    }

    @Override
    public void onBackPressed() {
        try {
            Store.deleteColourHistogram(this);
        } finally {
            super.onBackPressed();
        }
    }
}
