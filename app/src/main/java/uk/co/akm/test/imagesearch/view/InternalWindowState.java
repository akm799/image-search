package uk.co.akm.test.imagesearch.view;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

final class InternalWindowState extends View.BaseSavedState {
    private static final byte WIDTH_BYTE = 0;
    private static final byte HEIGHT_BYTE = 1;
    private static final int STATE_BYTE_ARRAY_LENGTH = 20;

    public static final Parcelable.Creator<InternalWindowState> CREATOR = new Parcelable.Creator<InternalWindowState>() {
        @Override
        public InternalWindowState createFromParcel(Parcel parcel) {
            return new InternalWindowState(parcel);
        }

        @Override
        public InternalWindowState[] newArray(int i) {
            return new InternalWindowState[i];
        }
    };

    private final byte b;
    private final float ratio;
    private final float sizeFraction;
    private final float xFraction;
    private final float yFraction;

    InternalWindowState(
            Parcelable superState,
            int viewWidth,
            int viewHeight,
            float wLeft,
            float wTop,
            float wWidth,
            float wHeight
    ) {
        super(superState);

        b = (viewWidth < viewHeight ? WIDTH_BYTE : HEIGHT_BYTE);
        ratio = (b == WIDTH_BYTE ? wHeight/wWidth : wWidth/wHeight);
        sizeFraction = (b == WIDTH_BYTE ? wWidth/viewWidth : wHeight/viewHeight);
        xFraction = wLeft/viewWidth;
        yFraction = wTop/viewHeight;
    }

    private InternalWindowState(Parcel source) {
        super(source);

        final byte[] state = new byte[STATE_BYTE_ARRAY_LENGTH];
        source.readByteArray(state);

        final DataInputStream dis = new DataInputStream(new ByteArrayInputStream(state));

        try {
            b = dis.readByte();
            ratio = dis.readFloat();
            sizeFraction = dis.readFloat();
            xFraction = dis.readFloat();
            yFraction = dis.readFloat();
        } catch (IOException ioe) {
            throw new RuntimeException(ioe); // Should never happen.
        }
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
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

    InternalWindow toInternalWindow(View parent) {
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

        return new InternalWindow(parent, wLeft, wTop, wWidth, wHeight);
    }
}