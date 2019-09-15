package uk.co.akm.test.imagesearch.view.display.persistent;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

final class PhotoViewState extends View.BaseSavedState {
    private final String photoName;

    public static final Parcelable.Creator<PhotoViewState> CREATOR = new Parcelable.Creator<PhotoViewState>() {
        @Override
        public PhotoViewState createFromParcel(Parcel parcel) {
            return new PhotoViewState(parcel);
        }

        @Override
        public PhotoViewState[] newArray(int size) {
            return new PhotoViewState[size];
        }
    };

    PhotoViewState(Parcelable superState, String photoName) {
        super(superState);

        this.photoName = photoName;
    }

    private PhotoViewState(Parcel source) {
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
