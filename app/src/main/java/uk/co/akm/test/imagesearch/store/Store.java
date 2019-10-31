package uk.co.akm.test.imagesearch.store;

import android.content.Context;
import android.content.SharedPreferences;

public class Store {
    private static final String SHARED_PREFERENCES_FILE_KEY = "uk.co.akm.test.imagesearch.shared_preferences_storage_file";

    public static String get(Context context, String key) {
        return sharedPreferences(context).getString(key, null);
    }

    public static void put(Context context, String key, String value) {
        sharedPreferences(context).edit().putString(key, value).apply();
    }

    public static void remove(Context context, String key) {
        sharedPreferences(context).edit().remove(key).apply();
    }

    private static SharedPreferences sharedPreferences(Context context) {
        return context.getSharedPreferences(SHARED_PREFERENCES_FILE_KEY, Context.MODE_PRIVATE);
    }

    private Store() {}
}
