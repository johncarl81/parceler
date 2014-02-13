package org.parceler.collections;

import android.os.Parcelable;
import org.parceler.ParcelWrapper;
import org.parceler.Parcels;

import java.util.HashMap;
import java.util.Map;

public class MapParcelable implements android.os.Parcelable, ParcelWrapper<Map> {

    private Map<Object, Object> contents;
    @SuppressWarnings("UnusedDeclaration")
    public final static MapParcelable.MapParcelableCreator CREATOR = new MapParcelable.MapParcelableCreator();

    public MapParcelable(android.os.Parcel parcel) {
        int size = parcel.readInt();
        if (size < 0) {
            contents = null;
        } else {
            contents = new HashMap<Object, Object>();
            for (int i = 0; (i < size); i++) {
                Parcelable key = parcel.readParcelable(ClassLoader.getSystemClassLoader());
                Parcelable value = parcel.readParcelable(ClassLoader.getSystemClassLoader());
                contents.put(Parcels.unwrap(key), Parcels.unwrap(value));
            }
        }
    }

    public MapParcelable(Map contents) {
        this.contents = contents;
    }

    @Override
    public void writeToParcel(android.os.Parcel parcel, int flags) {
        if (contents == null) {
            parcel.writeInt(-1);
        } else {
            parcel.writeInt(contents.size());
            for (Map.Entry<Object, Object> entry : contents.entrySet()) {
                parcel.writeParcelable(Parcels.wrap(entry.getKey()), flags);
                parcel.writeParcelable(Parcels.wrap(entry.getValue()), flags);
            }
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public Map getParcel() {
        return contents;
    }

    private final static class MapParcelableCreator implements Creator<MapParcelable> {


        @Override
        public MapParcelable createFromParcel(android.os.Parcel parcel$$17) {
            return new MapParcelable(parcel$$17);
        }

        @Override
        public MapParcelable[] newArray(int size) {
            return new MapParcelable[size];
        }

    }

}