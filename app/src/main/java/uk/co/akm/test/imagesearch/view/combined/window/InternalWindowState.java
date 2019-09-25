package uk.co.akm.test.imagesearch.view.combined.window;

import android.os.Parcel;
import android.view.View;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class InternalWindowState {
    private static final byte WIDTH_BYTE = 0;
    private static final byte HEIGHT_BYTE = 1;
    private static final int STATE_BYTE_ARRAY_LENGTH = 20;

    private final byte b;
    private final float ratio;
    private final float sizeFraction;
    private final float xFraction;
    private final float yFraction;

    InternalWindowState(View parent, InternalWindow window) {
        final int viewWidth = parent.getWidth();
        final int viewHeight = parent.getHeight();

        final float wLeft = window.getWindowLeft();
        final float wTop = window.getWindowTop();
        final float wWidth = window.getWindowWidth();
        final float wHeight = window.getWindowHeight();

        b = (viewWidth <= viewHeight ? WIDTH_BYTE : HEIGHT_BYTE);
        ratio = (b == WIDTH_BYTE ? wHeight/wWidth : wWidth/wHeight);
        sizeFraction = (b == WIDTH_BYTE ? wWidth/viewWidth : wHeight/viewHeight);
        xFraction = wLeft/viewWidth;
        yFraction = wTop/viewHeight;
    }

    public InternalWindowState(Parcel source) {
        try (DataInputStream dis = getStateDataInputStream(source)) {
            b = dis.readByte();
            ratio = dis.readFloat();
            sizeFraction = dis.readFloat();
            xFraction = dis.readFloat();
            yFraction = dis.readFloat();
        } catch (IOException ioe) {
            throw new RuntimeException(ioe); // Should never happen.
        }
    }

    private DataInputStream getStateDataInputStream(Parcel source) {
        final byte[] state = new byte[STATE_BYTE_ARRAY_LENGTH];
        source.readByteArray(state);

        return new DataInputStream(new ByteArrayInputStream(state));
    }

    public void writeToParcel(Parcel out) {
        out.writeByteArray(toByteArray());
    }

    private byte[] toByteArray() {
        try {
            final ByteArrayOutputStream bos = new ByteArrayOutputStream(STATE_BYTE_ARRAY_LENGTH);
            final DataOutputStream dos = new DataOutputStream(bos);
            dos.writeByte(b);
            dos.writeFloat(ratio);
            dos.writeFloat(sizeFraction);
            dos.writeFloat(xFraction);
            dos.writeFloat(yFraction);

            return bos.toByteArray();
        } catch (IOException ioe) {
            throw new RuntimeException(ioe); // Should never happen.
        }
    }

    public InternalWindow toInternalWindow(View parent) {
        final int viewWidth = parent.getWidth();
        final int viewHeight = parent.getHeight();

        final float wLeft = xFraction*viewWidth;
        final float wTop = yFraction*viewHeight;

        final float wWidth;
        final float wHeight;
        if (b == WIDTH_BYTE) {
            wWidth = sizeFraction*viewWidth;
            wHeight = ratio*wWidth;
        } else {
            wHeight = sizeFraction*viewHeight;
            wWidth = ratio*wHeight;
        }

        if (RangeFunctions.inRange(wLeft, wWidth, viewHeight, wTop, wHeight, viewHeight)) {
            return new InternalWindow(parent, wLeft, wTop, wWidth, wHeight);
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        return "b=" + b + " ratio=" + ratio + " sizeFraction=" + sizeFraction + " xFraction=" + xFraction + " yFraction=" + yFraction + " [" + super.toString() + "]";
    }
}