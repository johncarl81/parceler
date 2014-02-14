/**
 * Copyright 2013 John Ericksen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.parceler;

import android.os.Parcelable;
import android.util.SparseArray;

import java.util.*;

final class CollectionsRepository implements Repository<Parcels.ParcelableFactory> {

    private static final CollectionsRepository INSTANCE = new CollectionsRepository();
    private final Map<Class, Parcels.ParcelableFactory> parcelableCollectionFactories = new HashMap<Class, Parcels.ParcelableFactory>();

    private CollectionsRepository() {
        //private singleton constructor
        parcelableCollectionFactories.put(List.class, new ListParcelableFactory());
        parcelableCollectionFactories.put(ArrayList.class, new ListParcelableFactory());
        parcelableCollectionFactories.put(Set.class, new SetParcelableFactory());
        parcelableCollectionFactories.put(HashSet.class, new SetParcelableFactory());
        parcelableCollectionFactories.put(SparseArray.class, new SparseArrayParcelableFactory());
        parcelableCollectionFactories.put(Map.class, new MapParcelableFactory());
        parcelableCollectionFactories.put(HashMap.class, new MapParcelableFactory());
    }

    static {

    }

    public static CollectionsRepository getInstance() {
        return INSTANCE;
    }

    @Override
    public Map<Class, Parcels.ParcelableFactory> get() {
        return parcelableCollectionFactories;
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

    static final class ListParcelable implements Parcelable, ParcelWrapper<List> {

        private List contents;

        @SuppressWarnings("UnusedDeclaration")
        public static final ListParcelableCreator CREATOR = new ListParcelableCreator();

        @SuppressWarnings("unchecked")
        ListParcelable(android.os.Parcel parcel) {
            int size = parcel.readInt();
            if (size < 0) {
                contents = null;
            } else {
                contents = new ArrayList<String>();
                for (int i = 0; (i < size); i++) {
                    contents.add(Parcels.unwrap(parcel.readParcelable(SparseArrayParcelableFactory.class.getClassLoader())));
                }
            }
        }

        ListParcelable(List contents) {
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

        private static final class ListParcelableCreator implements Creator<ListParcelable> {

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

    static final class MapParcelable implements android.os.Parcelable, ParcelWrapper<Map> {

        private Map<Object, Object> contents;
        @SuppressWarnings("UnusedDeclaration")
        public static final MapParcelable.MapParcelableCreator CREATOR = new MapParcelable.MapParcelableCreator();

        MapParcelable(android.os.Parcel parcel) {
            int size = parcel.readInt();
            if (size < 0) {
                contents = null;
            } else {
                contents = new HashMap<Object, Object>();
                for (int i = 0; (i < size); i++) {
                    Parcelable key = parcel.readParcelable(MapParcelable.class.getClassLoader());
                    Parcelable value = parcel.readParcelable(MapParcelable.class.getClassLoader());
                    contents.put(Parcels.unwrap(key), Parcels.unwrap(value));
                }
            }
        }

        @SuppressWarnings("unchecked")
        MapParcelable(Map contents) {
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

        private static final class MapParcelableCreator implements Creator<MapParcelable> {


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

    static final class SetParcelable implements Parcelable, ParcelWrapper<Set> {

        private Set contents;

        @SuppressWarnings("UnusedDeclaration")
        public static final ListParcelableCreator CREATOR = new ListParcelableCreator();

        @SuppressWarnings("unchecked")
        SetParcelable(android.os.Parcel parcel) {
            int size = parcel.readInt();
            if (size < 0) {
                contents = null;
            } else {
                contents = new HashSet<String>();
                for (int i = 0; (i < size); i++) {
                    contents.add(Parcels.unwrap(parcel.readParcelable(SetParcelable.class.getClassLoader())));
                }
            }
        }

        SetParcelable(Set contents) {
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

        private static final class ListParcelableCreator implements Creator<SetParcelable> {

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

    static final class SparseArrayParcelable implements android.os.Parcelable, ParcelWrapper<SparseArray> {

        private SparseArray contents;
        @SuppressWarnings("UnusedDeclaration")
        public static final SparseArrayCreator CREATOR = new SparseArrayCreator();

        @SuppressWarnings("unchecked")
        SparseArrayParcelable(android.os.Parcel parcel) {
            int size = parcel.readInt();
            if (size < 0) {
                contents = null;
            } else {
                contents = new android.util.SparseArray<android.os.Parcelable>(size);
                for (int i = 0; (i < size); i++) {
                    int key = parcel.readInt();
                    contents.append(key, Parcels.unwrap(parcel.readParcelable(SparseArrayParcelable.class.getClassLoader())));
                }
            }
        }

        SparseArrayParcelable(SparseArray contents) {
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

        private static final class SparseArrayCreator implements Creator<SparseArrayParcelable> {

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
}