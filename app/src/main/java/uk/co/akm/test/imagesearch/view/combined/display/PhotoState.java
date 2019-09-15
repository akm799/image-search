package uk.co.akm.test.imagesearch.view.combined.display;

import android.os.Parcel;

public final class PhotoState {
    private final String photoName;

    public PhotoState(String photoName) {
        this.photoName = photoName;
    }

    public PhotoState(Parcel source) {
        photoName = source.readString();
    }

    public void writeToParcel(Parcel out) {
        out.writeString(photoName);
    }

    public String getPhotoName() {
        return photoName;
    }

    @Override
    public String toString() {
        return photoName;
    }
}
