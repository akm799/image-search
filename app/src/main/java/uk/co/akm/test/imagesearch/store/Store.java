package uk.co.akm.test.imagesearch.store;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.constraint.solver.widgets.Rectangle;

import uk.co.akm.test.imagesearch.process.model.window.Window;

public class Store {
    private static final String SHARED_PREFERENCES_FILE_KEY = "uk.co.akm.test.imagesearch.shared_preferences_storage_file";

    private static final char NUMBER_SEPARATOR = '|';
    private static final String NUMBER_SEPARATOR_REGEX = "\\|";

    private static final int WINDOW_LEFT_INDEX = 0;
    private static final int WINDOW_TOP_INDEX = 1;
    private static final int WINDOW_WIDTH_INDEX = 2;
    private static final int WINDOW_HEIGHT_INDEX = 3;
    private static final int WINDOW_DATA_COMPONENTS_NUMBER = 4;
    private static final String IMAGE_SELECTION_WINDOW_KEY = "image.selection.window_key";

    public static String get(Context context, String key) {
        return sharedPreferences(context).getString(key, null);
    }

    public static void put(Context context, String key, String value) {
        sharedPreferences(context).edit().putString(key, value).apply();
    }

    public static void remove(Context context, String key) {
        sharedPreferences(context).edit().remove(key).apply();
    }

    public static Window getWindow(Context context) {
        final String windowData = get(context, IMAGE_SELECTION_WINDOW_KEY);
        if (windowData == null) {
            return null;
        }

        return buildWindow(windowData);
    }

    private static Window buildWindow(String windowData) {
        final int[] components = deserialize(windowData);
        if (components.length != WINDOW_DATA_COMPONENTS_NUMBER) {
            throw new IllegalStateException("Invalid image selection window data stored. Expected " + WINDOW_DATA_COMPONENTS_NUMBER + " components but got " + components.length + ".");
        }

        final Rectangle rectangle = new Rectangle();
        rectangle.setBounds(components[WINDOW_LEFT_INDEX], components[WINDOW_TOP_INDEX], components[WINDOW_WIDTH_INDEX], components[WINDOW_HEIGHT_INDEX]);

        return new Window(rectangle);
    }

    public static void putWindow(Context context, Window window) {
        final int[] numbers = new int[WINDOW_DATA_COMPONENTS_NUMBER];
        numbers[WINDOW_LEFT_INDEX] = window.left;
        numbers[WINDOW_TOP_INDEX] = window.top;
        numbers[WINDOW_WIDTH_INDEX] = window.width;
        numbers[WINDOW_HEIGHT_INDEX] = window.height;

        put(context, IMAGE_SELECTION_WINDOW_KEY, serialize(numbers));
    }

    public static void removeWindow(Context context) {
        remove(context, IMAGE_SELECTION_WINDOW_KEY);
    }

    private static String serialize(int[] numbers) {
        final StringBuilder sb = new StringBuilder();
        for (int i=0; i<numbers.length ; i++) {
            sb.append(numbers[i]);
            if (i < numbers.length - 1) {
                sb.append(NUMBER_SEPARATOR);
            }
        }

        return sb.toString();
    }

    private static int[] deserialize(String windowData) {
        final String[] components = windowData.split(NUMBER_SEPARATOR_REGEX);
        final int[] numbers = new int[components.length];
        for (int i=0 ; i<components.length ; i++) {
            try {
                numbers[i] = Integer.parseInt(components[i]);
            } catch (NumberFormatException nfe) {
                throw new IllegalStateException("Invalid image selection window data stored. Expected an integer for component with index " + i + " but got '" + components[i] + "'.", nfe);
            }
        }

        return numbers;
    }

    private static SharedPreferences sharedPreferences(Context context) {
        return context.getSharedPreferences(SHARED_PREFERENCES_FILE_KEY, Context.MODE_PRIVATE);
    }

    private Store() {}
}
