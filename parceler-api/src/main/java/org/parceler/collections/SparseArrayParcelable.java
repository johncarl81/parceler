package org.parceler.collections;

import android.util.SparseArray;
import org.parceler.ParcelWrapper;
import org.parceler.Parcels;

public class SparseArrayParcelable implements android.os.Parcelable, ParcelWrapper<SparseArray> {

    private SparseArray contents;
    @SuppressWarnings("UnusedDeclaration")
    public final static SparseArrayCreator CREATOR = new SparseArrayCreator();

    public SparseArrayParcelable(android.os.Parcel parcel) {
        int size = parcel.readInt();
        if (size < 0) {
            contents = null;
        } else {
            contents = new android.util.SparseArray<android.os.Parcelable>(size);
            for (int i = 0; (i < size); i++) {
                int key = parcel.readInt();
                contents.append(key, Parcels.unwrap(parcel.readParcelable(ClassLoader.getSystemClassLoader())));
            }
        }
    }

    public SparseArrayParcelable(SparseArray contents) {
        this.contents = contents;
    }

    @Override
    public void writeToParcel(android.os.Parcel parcel, int flags) {
        if (contents == null) {
            parcel.writeInt(-1);
        } else {
            parcel.writeInt(contents.size());
            for (int i = 0; (i < contents.size()); i++) {
                parcel.writeInt(contents.keyAt(i));
                parcel.writeParcelable(Parcels.wrap(contents.valueAt(i)), flags);
            }
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public SparseArray getParcel() {
        return contents;
    }

    private final static class SparseArrayCreator implements Creator<SparseArrayParcelable> {

        @Override
        public SparseArrayParcelable createFromParcel(android.os.Parcel parcel) {
            return new SparseArrayParcelable(parcel);
        }

        @Override
        public SparseArrayParcelable[] newArray(int size) {
            return new SparseArrayParcelable[size];
        }
    }
}