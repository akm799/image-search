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
            String photoName) {
        super(superState);

        photoState = new PhotoState(photoName);
        windowState = null;
    }

    PhotoWindowState(
            Parcelable superState,
            View parent,
            float wLeft,
            float wTop,
            float wWidth,
            float wHeight) {
        super(superState);

        photoState = null;
        windowState = new InternalWindowState(parent, wLeft, wTop, wWidth, wHeight);
    }

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

        final StateMode stateMode = StateMode.valueOf(source.readString());

        switch (stateMode) {
            case PHOTO:
                photoState = new PhotoState(source);
                windowState = null;
                break;

            case WINDOW:
                photoState = null;
                windowState = new InternalWindowState(source);
                break;

            case BOTH:
                photoState = new PhotoState(source);
                windowState = new InternalWindowState(source);
                break;

             default: throw new IllegalArgumentException("Unrecognized state mode: " + stateMode);
        }
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);

        out.writeString(findStateMode().name());

        if (photoState != null) {
            photoState.writeToParcel(out);
        }

        if (windowState != null) {
            windowState.writeToParcel(out);
        }
    }

    private StateMode findStateMode() {
        if (photoState != null && windowState != null) {
            return StateMode.BOTH;
        } else if (photoState != null) {
            return StateMode.PHOTO;
        } else if (windowState != null) {
            return StateMode.WINDOW;
        } else {
            throw new IllegalStateException("Both the photo and window states are null.");
        }
    }

    String getPhotoName() {
        return photoState == null ? null : photoState.getPhotoName();
    }

    InternalWindow toInternalWindow(View parent) {
        return windowState == null ? null : windowState.toInternalWindow(parent);
    }

    @Override
    public String toString() {
        return ("photo=" + photoState + "  window=" + windowState);
    }
}
