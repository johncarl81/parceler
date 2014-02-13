package org.parceler.collections;

import android.os.Parcelable;
import org.parceler.ParcelWrapper;
import org.parceler.Parcels;

import java.util.HashSet;
import java.util.Set;

public class SetParcelable implements Parcelable, ParcelWrapper<Set> {

    private Set contents;

    @SuppressWarnings("UnusedDeclaration")
    public final static ListParcelableCreator CREATOR = new ListParcelableCreator();

    public SetParcelable(android.os.Parcel parcel) {
        int size = parcel.readInt();
        if (size < 0) {
            contents = null;
        } else {
            contents = new HashSet<String>();
            for (int i = 0; (i < size); i++) {
                contents.add(Parcels.unwrap(parcel.readParcelable(ClassLoader.getSystemClassLoader())));
            }
        }
    }

    public SetParcelable(Set contents) {
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
    public Set getParcel() {
        return contents;
    }

    private final static class ListParcelableCreator implements Creator<SetParcelable> {

        @Override
        public SetParcelable createFromParcel(android.os.Parcel parcel) {
            return new SetParcelable(parcel);
        }

        @Override
        public SetParcelable[] newArray(int size) {
            return new SetParcelable[size];
        }

    }

}