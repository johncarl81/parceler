package org.parceler.collections;

import android.os.Parcelable;
import android.util.SparseArray;
import org.parceler.Parcels;
import org.parceler.Repository;

import java.util.*;

public class CollectionsRepository implements Repository<Parcels.ParcelableFactory> {

    private static final Map<Class, Parcels.ParcelableFactory> PARCEL_COLLECTIONS = new HashMap<Class, Parcels.ParcelableFactory>();

    static {
        PARCEL_COLLECTIONS.put(List.class, new ListParcelableFactory());
        PARCEL_COLLECTIONS.put(ArrayList.class, new ListParcelableFactory());
        PARCEL_COLLECTIONS.put(Set.class, new SetParcelableFactory());
        PARCEL_COLLECTIONS.put(HashSet.class, new SetParcelableFactory());
        PARCEL_COLLECTIONS.put(SparseArray.class, new SparseArrayParcelableFactory());
        PARCEL_COLLECTIONS.put(Map.class, new MapParcelableFactory());
        PARCEL_COLLECTIONS.put(HashMap.class, new MapParcelableFactory());
    }

    @Override
    public Map<Class, Parcels.ParcelableFactory> get() {
        return PARCEL_COLLECTIONS;
    }

    private static class ListParcelableFactory implements Parcels.ParcelableFactory<List> {

        @Override
        public Parcelable buildParcelable(List input) {
            return new ListParcelable(input);
        }
    }

    private static class SetParcelableFactory implements Parcels.ParcelableFactory<Set> {

        @Override
        public Parcelable buildParcelable(Set input) {
            return new SetParcelable(input);
        }
    }

    private static class MapParcelableFactory implements Parcels.ParcelableFactory<Map> {

        @Override
        public Parcelable buildParcelable(Map input) {
            return new MapParcelable(input);
        }
    }

    private static class SparseArrayParcelableFactory implements Parcels.ParcelableFactory<SparseArray> {

        @Override
        public Parcelable buildParcelable(SparseArray input) {
            return new SparseArrayParcelable(input);
        }
    }
}