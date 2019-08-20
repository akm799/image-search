package uk.co.akm.test.imagesearch.view.display.persistent;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

final class PhotoWindowState extends View.BaseSavedState {
    private final String photoName;

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

    PhotoWindowState(Parcelable superState, String photoName) {
        super(superState);

        this.photoName = photoName;
    }

    private PhotoWindowState(Parcel source) {
        super(source);

        photoName = source.readString();
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeString(photoName);
    }

    String getPhotoName() {
        return photoName;
    }
}
