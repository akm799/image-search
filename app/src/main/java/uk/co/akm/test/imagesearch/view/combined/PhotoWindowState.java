package uk.co.akm.test.imagesearch.view.combined;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import uk.co.akm.test.imagesearch.view.combined.display.PhotoState;
import uk.co.akm.test.imagesearch.view.combined.window.InternalWindowState;
import uk.co.akm.test.imagesearch.view.combined.window.InternalWindow;

final class PhotoWindowState extends View.BaseSavedState {
    public static final Parcelable.Creator<PhotoWindowState> CREATOR = new Parcelable.Creator<PhotoWindowState>() {
        @Override
        public PhotoWindowState createFromParcel(Parcel parcel) {
            return new PhotoWindowState(parcel);
        }

        @Override
        public PhotoWindowState[] newArray(int size) {
            return new PhotoWindowState[size];
        }
    };

    private final PhotoState photoState;
    private final InternalWindowState windowState;

    PhotoWindowState(
            Parcelable superState,
            View parent,
            float wLeft,
            float wTop,
            float wWidth,
            float wHeight,
            String photoName) {
        super(superState);

        photoState = new PhotoState(photoName);
        windowState = new InternalWindowState(parent, wLeft, wTop, wWidth, wHeight);
    }

    private PhotoWindowState(Parcel source) {
        super(source);

        photoState = new PhotoState(source);
        windowState = new InternalWindowState(source);
    }

    String getPhotoName() {
        return photoState.getPhotoName();
    }

    InternalWindow toInternalWindow(View parent) {
        return windowState.toInternalWindow(parent);
    }

    @Override
    public String toString() {
        return ("photo=" + photoState + "  window=" + windowState);
    }
}
