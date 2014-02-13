package org.parceler.collections;

import android.os.Parcelable;
import org.parceler.ParcelWrapper;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class ListParcelable implements Parcelable, ParcelWrapper<List> {

    private List contents;

    @SuppressWarnings("UnusedDeclaration")
    public final ListParcelableCreator CREATOR = new ListParcelableCreator();

    public ListParcelable(android.os.Parcel parcel) {
        int size = parcel.readInt();
        if (size < 0) {
            contents = null;
        } else {
            contents = new ArrayList<String>();
            for (int i = 0; (i < size); i++) {
                contents.add(Parcels.unwrap(parcel.readParcelable(ClassLoader.getSystemClassLoader())));
            }
        }
    }

    public ListParcelable(List contents) {
        this.contents = contents;
    }

    @Override
    public void writeToParcel(android.os.Parcel parcel$$16, int flags) {
        if (contents == null) {
            parcel$$16.writeInt(-1);
        } else {
            parcel$$16.writeInt(contents.size());
            for (Object c : contents) {
                parcel$$16.writeParcelable(Parcels.wrap(c), flags);
            }
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public List getParcel() {
        return contents;
    }

    private final static class ListParcelableCreator implements Creator<ListParcelable> {

        @Override
        public ListParcelable createFromParcel(android.os.Parcel parcel) {
            return new ListParcelable(parcel);
        }

        @Override
        public ListParcelable[] newArray(int size) {
            return new ListParcelable[size];
        }
    }
}