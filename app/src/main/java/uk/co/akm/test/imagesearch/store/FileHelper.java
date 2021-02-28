package uk.co.akm.test.imagesearch.store;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileHelper {
    private static final String TAG = FileHelper.class.getSimpleName();

    static boolean writeBytes(byte[] bytes, File file) {
        BufferedOutputStream bos = null;

        if (bytes == null || bytes.length == 0 || file == null) {
            Log.e(TAG, "Invalid arguments when writing bytes to file.");
            return false;
        }

        try {
            bos = new BufferedOutputStream(new FileOutputStream(file));
            bos.write(bytes);
            bos.flush();

            return true;
        } catch (IOException ioe) {
            Log.e(TAG, "I/O error when writing bytes to file " + file.getAbsolutePath(), ioe);
            return false;
        } finally {
            closeSilently(bos);
        }
    }

    static byte[] readBytes(File file) {
        BufferedInputStream bis = null;

        if (file == null) {
            Log.e(TAG, "Cannot read bytes from a null file.");
            return null;
        }

        final byte[] bytes = new byte[(int)file.length()];

        try {
            bis = new BufferedInputStream(new FileInputStream(file));
            final int nRead = bis.read(bytes);
            if (nRead != bytes.length) {
                Log.e(TAG, "Could only read " + nRead + " out of " + bytes.length + " bytes from file " + file);
                return null;
            }

            return bytes;
        } catch (IOException ioe) {
            Log.e(TAG, "I/O error when reading bytes from file " + file.getAbsolutePath(), ioe);
            return null;
        } finally {
            closeSilently(bis);
        }
    }

    private static void closeSilently(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException ioe) {
                Log.e(TAG, "I/O error when closing stream.", ioe);
            }
        }
    }

    private FileHelper() {}
}
