/**
 * Copyright 2011-2015 John Ericksen
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

import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import org.parceler.converter.*;

import java.util.*;

final class NonParcelRepository implements Repository<Parcels.ParcelableFactory> {

    private static final NonParcelRepository INSTANCE = new NonParcelRepository();
    private final Map<Class, Parcels.ParcelableFactory> parcelableCollectionFactories = new HashMap<Class, Parcels.ParcelableFactory>();

    private NonParcelRepository() {
        //private singleton constructor
        parcelableCollectionFactories.put(Collection.class, new CollectionParcelableFactory());
        parcelableCollectionFactories.put(List.class, new ListParcelableFactory());
        parcelableCollectionFactories.put(ArrayList.class, new ListParcelableFactory());
        parcelableCollectionFactories.put(Set.class, new SetParcelableFactory());
        parcelableCollectionFactories.put(HashSet.class, new SetParcelableFactory());
        parcelableCollectionFactories.put(TreeSet.class, new TreeSetParcelableFactory());
        parcelableCollectionFactories.put(SparseArray.class, new SparseArrayParcelableFactory());
        parcelableCollectionFactories.put(Map.class, new MapParcelableFactory());
        parcelableCollectionFactories.put(HashMap.class, new MapParcelableFactory());
        parcelableCollectionFactories.put(TreeMap.class, new TreeMapParcelableFactory());
        parcelableCollectionFactories.put(Integer.class, new IntegerParcelableFactory());
        parcelableCollectionFactories.put(Long.class, new LongParcelableFactory());
        parcelableCollectionFactories.put(Double.class, new DoubleParcelableFactory());
        parcelableCollectionFactories.put(Float.class, new FloatParcelableFactory());
        parcelableCollectionFactories.put(Byte.class, new ByteParcelableFactory());
        parcelableCollectionFactories.put(String.class, new StringParcelableFactory());
        parcelableCollectionFactories.put(Character.class, new CharacterParcelableFactory());
        parcelableCollectionFactories.put(Boolean.class, new BooleanParcelableFactory());
        parcelableCollectionFactories.put(byte[].class, new ByteArrayParcelableFactory());
        parcelableCollectionFactories.put(char[].class, new CharArrayParcelableFactory());
        parcelableCollectionFactories.put(boolean[].class, new BooleanArrayParcelableFactory());
        parcelableCollectionFactories.put(IBinder.class, new IBinderParcelableFactory());
        parcelableCollectionFactories.put(Bundle.class, new BundleParcelableFactory());
        parcelableCollectionFactories.put(SparseBooleanArray.class, new SparseBooleanArrayParcelableFactory());
        parcelableCollectionFactories.put(LinkedList.class, new LinkedListParcelableFactory());
        parcelableCollectionFactories.put(LinkedHashMap.class, new LinkedHashMapParcelableFactory());
        parcelableCollectionFactories.put(SortedMap.class, new TreeMapParcelableFactory());
        parcelableCollectionFactories.put(SortedSet.class, new TreeSetParcelableFactory());
        parcelableCollectionFactories.put(LinkedHashSet.class, new LinkedHashSetParcelableFactory());

    }

    public static NonParcelRepository getInstance() {
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

    private static class CharacterParcelableFactory implements Parcels.ParcelableFactory<Character> {

        @Override
        public Parcelable buildParcelable(Character input) {
            return new CharacterParcelable(input);
        }
    }

    private static class BooleanParcelableFactory implements Parcels.ParcelableFactory<Boolean> {

        @Override
        public Parcelable buildParcelable(Boolean input) {
            return new BooleanParcelable(input);
        }
    }

    private static class ByteArrayParcelableFactory implements Parcels.ParcelableFactory<byte[]> {

        @Override
        public Parcelable buildParcelable(byte[] input) {
            return new ByteArrayParcelable(input);
        }
    }

    private static class CharArrayParcelableFactory implements Parcels.ParcelableFactory<char[]> {

        @Override
        public Parcelable buildParcelable(char[] input) {
            return new CharArrayParcelable(input);
        }
    }

    private static class BooleanArrayParcelableFactory implements Parcels.ParcelableFactory<boolean[]> {

        @Override
        public Parcelable buildParcelable(boolean[] input) {
            return new BooleanArrayParcelable(input);
        }
    }

    private static class IBinderParcelableFactory implements Parcels.ParcelableFactory<IBinder> {

        @Override
        public Parcelable buildParcelable(IBinder input) {
            return new IBinderParcelable(input);
        }
    }

    private static class BundleParcelableFactory implements Parcels.ParcelableFactory<Bundle> {

        @Override
        public Parcelable buildParcelable(Bundle input) {
            return input;
        }
    }

    private static class SparseBooleanArrayParcelableFactory implements Parcels.ParcelableFactory<SparseBooleanArray> {

        @Override
        public Parcelable buildParcelable(SparseBooleanArray input) {
            return new SparseBooleanArrayParcelable(input);
        }
    }

    private static class LinkedListParcelableFactory implements Parcels.ParcelableFactory<LinkedList> {

        @Override
        public Parcelable buildParcelable(LinkedList input) {
            return new LinkedListParcelable(input);
        }
    }

    private static class LinkedHashMapParcelableFactory implements Parcels.ParcelableFactory<LinkedHashMap> {

        @Override
        public Parcelable buildParcelable(LinkedHashMap input) {
            return new LinkedHashMapParcelable(input);
        }
    }

    private static class LinkedHashSetParcelableFactory implements Parcels.ParcelableFactory<LinkedHashSet> {

        @Override
        public Parcelable buildParcelable(LinkedHashSet input) {
            return new LinkedHashSetParcelable(input);
        }
    }

    private static class SetParcelableFactory implements Parcels.ParcelableFactory<Set> {

        @Override
        public Parcelable buildParcelable(Set input) {
            return new SetParcelable(input);
        }
    }

    private static class TreeSetParcelableFactory implements Parcels.ParcelableFactory<Set> {

        @Override
        public Parcelable buildParcelable(Set input) {
            return new TreeSetParcelable(input);
        }
    }

    private static class MapParcelableFactory implements Parcels.ParcelableFactory<Map> {

        @Override
        public Parcelable buildParcelable(Map input) {
            return new MapParcelable(input);
        }
    }

    private static class TreeMapParcelableFactory implements Parcels.ParcelableFactory<Map> {

        @Override
        public Parcelable buildParcelable(Map input) {
            return new TreeMapParcelable(input);
        }
    }

    private static class CollectionParcelableFactory implements Parcels.ParcelableFactory<Collection> {

        @Override
        public Parcelable buildParcelable(Collection input) {
            return new CollectionParcelable(input);
        }
    }

    private static class SparseArrayParcelableFactory implements Parcels.ParcelableFactory<SparseArray> {

        @Override
        public Parcelable buildParcelable(SparseArray input) {
            return new SparseArrayParcelable(input);
        }
    }

    private static class IntegerParcelableFactory implements Parcels.ParcelableFactory<Integer>{

        @Override
        public Parcelable buildParcelable(Integer input) {
            return new IntegerParcelable(input);
        }
    }

    private static class LongParcelableFactory implements Parcels.ParcelableFactory<Long>{

        @Override
        public Parcelable buildParcelable(Long input) {
            return new LongParcelable(input);
        }
    }

    private static class DoubleParcelableFactory implements Parcels.ParcelableFactory<Double>{

        @Override
        public Parcelable buildParcelable(Double input) {
            return new DoubleParcelable(input);
        }
    }

    private static class FloatParcelableFactory implements Parcels.ParcelableFactory<Float>{

        @Override
        public Parcelable buildParcelable(Float input) {
            return new FloatParcelable(input);
        }
    }

    private static class ByteParcelableFactory implements Parcels.ParcelableFactory<Byte>{

        @Override
        public Parcelable buildParcelable(Byte input) {
            return new ByteParcelable(input);
        }
    }

    private static class StringParcelableFactory implements Parcels.ParcelableFactory<String>{

        @Override
        public Parcelable buildParcelable(String input) {
            return new StringParcelable(input);
        }
    }

    static class ParcelableParcelableFactory implements Parcels.ParcelableFactory<Parcelable>{

        @Override
        public Parcelable buildParcelable(Parcelable input) {
            return new ParcelableParcelable(input);
        }
    }


    public static final class ListParcelable extends ConverterParcelable<List>{

        private static final ArrayListParcelConverter CONVERTER = new ArrayListParcelConverter() {

            @Override
            public Object itemFromParcel(Parcel parcel) {
                return Parcels.unwrap(parcel.readParcelable(ListParcelable.class.getClassLoader()));
            }

            @Override
            public void itemToParcel(Object input, Parcel parcel, int flags) {
                parcel.writeParcelable(Parcels.wrap(input), flags);
            }
        };

        public ListParcelable(Parcel parcel) {
            super(parcel, CONVERTER);
        }

        public ListParcelable(List value) {
            super(value, CONVERTER);
        }

        @SuppressWarnings("UnusedDeclaration")
        public static final ListParcelableCreator CREATOR = new ListParcelableCreator();

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

    public static final class LinkedListParcelable extends ConverterParcelable<LinkedList>{

        private static final LinkedListParcelConverter CONVERTER = new LinkedListParcelConverter() {

            @Override
            public Object itemFromParcel(Parcel parcel) {
                return Parcels.unwrap(parcel.readParcelable(LinkedListParcelable.class.getClassLoader()));
            }

            @Override
            public void itemToParcel(Object input, Parcel parcel, int flags) {
                parcel.writeParcelable(Parcels.wrap(input), flags);
            }
        };

        public LinkedListParcelable(Parcel parcel) {
            super(parcel, CONVERTER);
        }

        public LinkedListParcelable(LinkedList value) {
            super(value, CONVERTER);
        }

        @SuppressWarnings("UnusedDeclaration")
        public static final LinkedListParcelableCreator CREATOR = new LinkedListParcelableCreator();

        private static final class LinkedListParcelableCreator implements Creator<LinkedListParcelable> {

            @Override
            public LinkedListParcelable createFromParcel(android.os.Parcel parcel) {
                return new LinkedListParcelable(parcel);
            }

            @Override
            public LinkedListParcelable[] newArray(int size) {
                return new LinkedListParcelable[size];
            }
        }
    }

    public static final class MapParcelable extends ConverterParcelable<Map> {

        private static final HashMapParcelConverter CONVERTER = new HashMapParcelConverter() {

            @Override
            public void mapKeyToParcel(Object key, Parcel parcel, int flags) {
                parcel.writeParcelable(Parcels.wrap(key), flags);
            }

            @Override
            public void mapValueToParcel(Object value, Parcel parcel, int flags) {
                parcel.writeParcelable(Parcels.wrap(value), flags);
            }

            @Override
            public Object mapKeyFromParcel(Parcel parcel) {
                return Parcels.unwrap(parcel.readParcelable(MapParcelable.class.getClassLoader()));
            }

            @Override
            public Object mapValueFromParcel(Parcel parcel) {
                return Parcels.unwrap(parcel.readParcelable(MapParcelable.class.getClassLoader()));
            }
        };

        public MapParcelable(Parcel parcel) {
            super(parcel, CONVERTER);
        }

        public MapParcelable(Map value) {
            super(value, CONVERTER);
        }

        @SuppressWarnings("UnusedDeclaration")
        public static final MapParcelableCreator CREATOR = new MapParcelableCreator();

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

    public static final class LinkedHashMapParcelable extends ConverterParcelable<LinkedHashMap> {

        private static final LinkedHashMapParcelConverter CONVERTER = new LinkedHashMapParcelConverter() {

            @Override
            public void mapKeyToParcel(Object key, Parcel parcel, int flags) {
                parcel.writeParcelable(Parcels.wrap(key), flags);
            }

            @Override
            public void mapValueToParcel(Object value, Parcel parcel, int flags) {
                parcel.writeParcelable(Parcels.wrap(value), flags);
            }

            @Override
            public Object mapKeyFromParcel(Parcel parcel) {
                return Parcels.unwrap(parcel.readParcelable(MapParcelable.class.getClassLoader()));
            }

            @Override
            public Object mapValueFromParcel(Parcel parcel) {
                return Parcels.unwrap(parcel.readParcelable(MapParcelable.class.getClassLoader()));
            }
        };

        public LinkedHashMapParcelable(Parcel parcel) {
            super(parcel, CONVERTER);
        }

        public LinkedHashMapParcelable(LinkedHashMap value) {
            super(value, CONVERTER);
        }

        @SuppressWarnings("UnusedDeclaration")
        public static final LinkedHashMapParcelableCreator CREATOR = new LinkedHashMapParcelableCreator();

        private static final class LinkedHashMapParcelableCreator implements Creator<LinkedHashMapParcelable> {

            @Override
            public LinkedHashMapParcelable createFromParcel(android.os.Parcel parcel$$17) {
                return new LinkedHashMapParcelable(parcel$$17);
            }

            @Override
            public LinkedHashMapParcelable[] newArray(int size) {
                return new LinkedHashMapParcelable[size];
            }

        }
    }

    public static final class TreeMapParcelable extends ConverterParcelable<Map> {

        private static final TreeMapParcelConverter CONVERTER = new TreeMapParcelConverter() {

            @Override
            public void mapKeyToParcel(Object key, Parcel parcel, int flags) {
                parcel.writeParcelable(Parcels.wrap(key), flags);
            }

            @Override
            public void mapValueToParcel(Object value, Parcel parcel, int flags) {
                parcel.writeParcelable(Parcels.wrap(value), flags);
            }

            @Override
            public Object mapKeyFromParcel(Parcel parcel) {
                return Parcels.unwrap(parcel.readParcelable(MapParcelable.class.getClassLoader()));
            }

            @Override
            public Object mapValueFromParcel(Parcel parcel) {
                return Parcels.unwrap(parcel.readParcelable(MapParcelable.class.getClassLoader()));
            }
        };

        public TreeMapParcelable(Parcel parcel) {
            super(parcel, CONVERTER);
        }

        public TreeMapParcelable(Map value) {
            super(value, CONVERTER);
        }

        @SuppressWarnings("UnusedDeclaration")
        public static final TreeMapParcelableCreator CREATOR = new TreeMapParcelableCreator();

        private static final class TreeMapParcelableCreator implements Creator<TreeMapParcelable> {

            @Override
            public TreeMapParcelable createFromParcel(android.os.Parcel parcel$$17) {
                return new TreeMapParcelable(parcel$$17);
            }

            @Override
            public TreeMapParcelable[] newArray(int size) {
                return new TreeMapParcelable[size];
            }

        }
    }

    public static final class SetParcelable extends ConverterParcelable<Set> {

        private static final HashSetParcelConverter CONVERTER = new HashSetParcelConverter() {

            @Override
            public Object itemFromParcel(Parcel parcel) {
                return Parcels.unwrap(parcel.readParcelable(SetParcelable.class.getClassLoader()));
            }

            @Override
            public void itemToParcel(Object input, Parcel parcel, int flags) {
                parcel.writeParcelable(Parcels.wrap(input), flags);
            }
        };

        public SetParcelable(Parcel parcel) {
            super(parcel, CONVERTER);
        }

        public SetParcelable(Set value) {
            super(value, CONVERTER);
        }

        @SuppressWarnings("UnusedDeclaration")
        public static final SetParcelableCreator CREATOR = new SetParcelableCreator();

        private static final class SetParcelableCreator implements Creator<SetParcelable> {

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

    public static final class TreeSetParcelable extends ConverterParcelable<Set> {

        private static final TreeSetParcelConverter CONVERTER = new TreeSetParcelConverter() {

            @Override
            public Object itemFromParcel(Parcel parcel) {
                return Parcels.unwrap(parcel.readParcelable(TreeSetParcelable.class.getClassLoader()));
            }

            @Override
            public void itemToParcel(Object input, Parcel parcel, int flags) {
                parcel.writeParcelable(Parcels.wrap(input), flags);
            }
        };

        public TreeSetParcelable(Parcel parcel) {
            super(parcel, CONVERTER);
        }

        public TreeSetParcelable(Set value) {
            super(value, CONVERTER);
        }

        @SuppressWarnings("UnusedDeclaration")
        public static final TreeSetParcelableCreator CREATOR = new TreeSetParcelableCreator();

        private static final class TreeSetParcelableCreator implements Creator<TreeSetParcelable> {

            @Override
            public TreeSetParcelable createFromParcel(android.os.Parcel parcel) {
                return new TreeSetParcelable(parcel);
            }

            @Override
            public TreeSetParcelable[] newArray(int size) {
                return new TreeSetParcelable[size];
            }
        }
    }

    public static final class LinkedHashSetParcelable extends ConverterParcelable<LinkedHashSet> {

        private static final LinkedHashSetParcelConverter CONVERTER = new LinkedHashSetParcelConverter() {

            @Override
            public Object itemFromParcel(Parcel parcel) {
                return Parcels.unwrap(parcel.readParcelable(LinkedHashSetParcelable.class.getClassLoader()));
            }

            @Override
            public void itemToParcel(Object input, Parcel parcel, int flags) {
                parcel.writeParcelable(Parcels.wrap(input), flags);
            }
        };

        public LinkedHashSetParcelable(Parcel parcel) {
            super(parcel, CONVERTER);
        }

        public LinkedHashSetParcelable(LinkedHashSet value) {
            super(value, CONVERTER);
        }

        @SuppressWarnings("UnusedDeclaration")
        public static final LinkedHashSetParcelableCreator CREATOR = new LinkedHashSetParcelableCreator();

        private static final class LinkedHashSetParcelableCreator implements Creator<LinkedHashSetParcelable> {

            @Override
            public LinkedHashSetParcelable createFromParcel(android.os.Parcel parcel) {
                return new LinkedHashSetParcelable(parcel);
            }

            @Override
            public LinkedHashSetParcelable[] newArray(int size) {
                return new LinkedHashSetParcelable[size];
            }
        }
    }

    public static final class CollectionParcelable extends ConverterParcelable<Collection> {

        private static final CollectionParcelConverter CONVERTER = new ArrayListParcelConverter() {

            @Override
            public Object itemFromParcel(Parcel parcel) {
                return Parcels.unwrap(parcel.readParcelable(CollectionParcelable.class.getClassLoader()));
            }

            @Override
            public void itemToParcel(Object input, Parcel parcel, int flags) {
                parcel.writeParcelable(Parcels.wrap(input), flags);
            }
        };

        public CollectionParcelable(Parcel parcel) {
            super(parcel, CONVERTER);
        }

        public CollectionParcelable(Collection value) {
            super(value, CONVERTER);
        }

        @SuppressWarnings("UnusedDeclaration")
        public static final CollectionParcelableCreator CREATOR = new CollectionParcelableCreator();

        private static final class CollectionParcelableCreator implements Creator<CollectionParcelable> {

            @Override
            public CollectionParcelable createFromParcel(android.os.Parcel parcel) {
                return new CollectionParcelable(parcel);
            }

            @Override
            public CollectionParcelable[] newArray(int size) {
                return new CollectionParcelable[size];
            }
        }
    }

    public static final class SparseArrayParcelable extends ConverterParcelable<SparseArray> {

        private static final SparseArrayParcelConverter CONVERTER = new SparseArrayParcelConverter() {

            @Override
            public Object itemFromParcel(Parcel parcel) {
                return Parcels.unwrap(parcel.readParcelable(SparseArrayParcelable.class.getClassLoader()));
            }

            @Override
            public void itemToParcel(Object input, Parcel parcel, int flags) {
                parcel.writeParcelable(Parcels.wrap(input), flags);
            }
        };

        public SparseArrayParcelable(Parcel parcel) {
            super(parcel, CONVERTER);
        }

        public SparseArrayParcelable(SparseArray value) {
            super(value, CONVERTER);
        }

        @SuppressWarnings("UnusedDeclaration")
        public static final SparseArrayCreator CREATOR = new SparseArrayCreator();

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

    public static final class SparseBooleanArrayParcelable extends ConverterParcelable<SparseBooleanArray> {

        private static final NullableParcelConverter<SparseBooleanArray> CONVERTER = new NullableParcelConverter<SparseBooleanArray>() {

            @Override
            public SparseBooleanArray nullSafeFromParcel(Parcel parcel) {
                return parcel.readSparseBooleanArray();
            }

            @Override
            public void nullSafeToParcel(SparseBooleanArray input, Parcel parcel, int flags) {
                parcel.writeSparseBooleanArray(input);
            }
        };

        public SparseBooleanArrayParcelable(Parcel parcel) {
            super(parcel, CONVERTER);
        }

        public SparseBooleanArrayParcelable(SparseBooleanArray value) {
            super(value, CONVERTER);
        }

        @SuppressWarnings("UnusedDeclaration")
        public static final SparseBooleanArrayCreator CREATOR = new SparseBooleanArrayCreator();

        private static final class SparseBooleanArrayCreator implements Creator<SparseBooleanArrayParcelable> {

            @Override
            public SparseBooleanArrayParcelable createFromParcel(android.os.Parcel parcel) {
                return new SparseBooleanArrayParcelable(parcel);
            }

            @Override
            public SparseBooleanArrayParcelable[] newArray(int size) {
                return new SparseBooleanArrayParcelable[size];
            }
        }
    }

    public static final class IntegerParcelable extends ConverterParcelable<Integer> {

        private static final NullableParcelConverter<Integer> CONVERTER = new NullableParcelConverter<Integer>() {

            @Override
            public Integer nullSafeFromParcel(Parcel parcel) {
                return parcel.readInt();
            }

            @Override
            public void nullSafeToParcel(Integer input, Parcel parcel, int flags) {
                parcel.writeInt(input);
            }
        };

        public IntegerParcelable(Parcel parcel) {
            super(parcel, CONVERTER);
        }

        public IntegerParcelable(Integer value) {
            super(value, CONVERTER);
        }

        @SuppressWarnings("UnusedDeclaration")
        public static final IntegerParcelableCreator CREATOR = new IntegerParcelableCreator();

        private static final class IntegerParcelableCreator implements Creator<IntegerParcelable> {

            @Override
            public IntegerParcelable createFromParcel(android.os.Parcel parcel) {
                return new IntegerParcelable(parcel);
            }

            @Override
            public IntegerParcelable[] newArray(int size) {
                return new IntegerParcelable[size];
            }
        }
    }

    public static final class LongParcelable  extends ConverterParcelable<Long> {

        private static final NullableParcelConverter<Long> CONVERTER = new NullableParcelConverter<Long>() {

            @Override
            public Long nullSafeFromParcel(Parcel parcel) {
                return parcel.readLong();
            }

            @Override
            public void nullSafeToParcel(Long input, Parcel parcel, int flags) {
                parcel.writeLong(input);
            }
        };

        public LongParcelable(Parcel parcel) {
            super(parcel, CONVERTER);
        }

        public LongParcelable(Long value) {
            super(value, CONVERTER);
        }

        @SuppressWarnings("UnusedDeclaration")
        public static final LongParcelableCreator CREATOR = new LongParcelableCreator();

        private static final class LongParcelableCreator implements Creator<LongParcelable> {

            @Override
            public LongParcelable createFromParcel(android.os.Parcel parcel) {
                return new LongParcelable(parcel);
            }

            @Override
            public LongParcelable[] newArray(int size) {
                return new LongParcelable[size];
            }
        }
    }

    public static final class DoubleParcelable  extends ConverterParcelable<Double> {

        private static final NullableParcelConverter<Double> CONVERTER = new NullableParcelConverter<Double>() {

            @Override
            public Double nullSafeFromParcel(Parcel parcel) {
                return parcel.readDouble();
            }

            @Override
            public void nullSafeToParcel(Double input, Parcel parcel, int flags) {
                parcel.writeDouble(input);
            }
        };

        public DoubleParcelable(Parcel parcel) {
            super(parcel, CONVERTER);
        }

        public DoubleParcelable(Double value) {
            super(value, CONVERTER);
        }

        @SuppressWarnings("UnusedDeclaration")
        public static final DoubleParcelableCreator CREATOR = new DoubleParcelableCreator();

        private static final class DoubleParcelableCreator implements Creator<DoubleParcelable> {

            @Override
            public DoubleParcelable createFromParcel(android.os.Parcel parcel) {
                return new DoubleParcelable(parcel);
            }

            @Override
            public DoubleParcelable[] newArray(int size) {
                return new DoubleParcelable[size];
            }
        }
    }

    public static final class FloatParcelable  extends ConverterParcelable<Float> {

        private static final NullableParcelConverter<Float> CONVERTER = new NullableParcelConverter<Float>() {

            @Override
            public Float nullSafeFromParcel(Parcel parcel) {
                return parcel.readFloat();
            }

            @Override
            public void nullSafeToParcel(Float input, Parcel parcel, int flags) {
                parcel.writeFloat(input);
            }
        };

        public FloatParcelable(Parcel parcel) {
            super(parcel, CONVERTER);
        }

        public FloatParcelable(Float value) {
            super(value, CONVERTER);
        }

        @SuppressWarnings("UnusedDeclaration")
        public static final FloatParcelableCreator CREATOR = new FloatParcelableCreator();

        private static final class FloatParcelableCreator implements Creator<FloatParcelable> {

            @Override
            public FloatParcelable createFromParcel(android.os.Parcel parcel) {
                return new FloatParcelable(parcel);
            }

            @Override
            public FloatParcelable[] newArray(int size) {
                return new FloatParcelable[size];
            }
        }
    }

    public static final class ByteParcelable extends ConverterParcelable<Byte> {

        private static final NullableParcelConverter<Byte> CONVERTER = new NullableParcelConverter<Byte>() {

            @Override
            public Byte nullSafeFromParcel(Parcel parcel) {
                return parcel.readByte();
            }

            @Override
            public void nullSafeToParcel(Byte input, Parcel parcel, int flags) {
                parcel.writeByte(input);
            }
        };

        public ByteParcelable(Parcel parcel) {
            super(parcel, CONVERTER);
        }

        public ByteParcelable(Byte value) {
            super(value, CONVERTER);
        }

        @SuppressWarnings("UnusedDeclaration")
        public static final ByteParcelableCreator CREATOR = new ByteParcelableCreator();

        private static final class ByteParcelableCreator implements Creator<ByteParcelable> {

            @Override
            public ByteParcelable createFromParcel(android.os.Parcel parcel) {
                return new ByteParcelable(parcel);
            }

            @Override
            public ByteParcelable[] newArray(int size) {
                return new ByteParcelable[size];
            }
        }
    }

    public static final class IBinderParcelable extends ConverterParcelable<IBinder> {

        private static final NullableParcelConverter<IBinder> CONVERTER = new NullableParcelConverter<IBinder>() {

            @Override
            public IBinder nullSafeFromParcel(Parcel parcel) {
                return parcel.readStrongBinder();
            }

            @Override
            public void nullSafeToParcel(IBinder input, Parcel parcel, int flags) {
                parcel.writeStrongBinder(input);
            }
        };

        public IBinderParcelable(Parcel parcel) {
            super(parcel, CONVERTER);
        }

        public IBinderParcelable(IBinder value) {
            super(value, CONVERTER);
        }

        @SuppressWarnings("UnusedDeclaration")
        public static final IBinderParcelableCreator CREATOR = new IBinderParcelableCreator();

        private static final class IBinderParcelableCreator implements Creator<IBinderParcelable> {

            @Override
            public IBinderParcelable createFromParcel(android.os.Parcel parcel) {
                return new IBinderParcelable(parcel);
            }

            @Override
            public IBinderParcelable[] newArray(int size) {
                return new IBinderParcelable[size];
            }
        }
    }

    public static final class ByteArrayParcelable extends ConverterParcelable<byte[]> {

        private static final NullableParcelConverter<byte[]> CONVERTER = new NullableParcelConverter<byte[]>() {

            @Override
            public byte[] nullSafeFromParcel(Parcel parcel) {
                return parcel.createByteArray();
            }

            @Override
            public void nullSafeToParcel(byte[] input, Parcel parcel, int flags) {
                parcel.writeByteArray(input);
            }
        };

        public ByteArrayParcelable(Parcel parcel) {
            super(parcel, CONVERTER);
        }

        public ByteArrayParcelable(byte[] value) {
            super(value, CONVERTER);
        }

        @SuppressWarnings("UnusedDeclaration")
        public static final ByteArrayParcelableCreator CREATOR = new ByteArrayParcelableCreator();

        private static final class ByteArrayParcelableCreator implements Creator<ByteArrayParcelable> {

            @Override
            public ByteArrayParcelable createFromParcel(android.os.Parcel parcel) {
                return new ByteArrayParcelable(parcel);
            }

            @Override
            public ByteArrayParcelable[] newArray(int size) {
                return new ByteArrayParcelable[size];
            }
        }
    }

    public static final class BooleanArrayParcelable extends ConverterParcelable<boolean[]> {

        private static final BooleanArrayParcelConverter CONVERTER = new BooleanArrayParcelConverter();

        public BooleanArrayParcelable(Parcel parcel) {
            super(parcel, CONVERTER);
        }

        public BooleanArrayParcelable(boolean[] value) {
            super(value, CONVERTER);
        }

        @SuppressWarnings("UnusedDeclaration")
        public static final BooleanArrayParcelableCreator CREATOR = new BooleanArrayParcelableCreator();

        private static final class BooleanArrayParcelableCreator implements Creator<BooleanArrayParcelable> {

            @Override
            public BooleanArrayParcelable createFromParcel(android.os.Parcel parcel) {
                return new BooleanArrayParcelable(parcel);
            }

            @Override
            public BooleanArrayParcelable[] newArray(int size) {
                return new BooleanArrayParcelable[size];
            }
        }
    }

    public static final class BooleanParcelable extends ConverterParcelable<Boolean> {

        private static final NullableParcelConverter<Boolean> CONVERTER = new NullableParcelConverter<Boolean>() {

            @Override
            public Boolean nullSafeFromParcel(Parcel parcel) {
                return parcel.createBooleanArray()[0];
            }

            @Override
            public void nullSafeToParcel(Boolean input, Parcel parcel, int flags) {
                parcel.writeBooleanArray(new boolean[]{input});
            }
        };

        public BooleanParcelable(Parcel parcel) {
            super(parcel, CONVERTER);
        }

        public BooleanParcelable(boolean value) {
            super(value, CONVERTER);
        }

        @SuppressWarnings("UnusedDeclaration")
        public static final BooleanParcelableCreator CREATOR = new BooleanParcelableCreator();

        private static final class BooleanParcelableCreator implements Creator<BooleanParcelable> {

            @Override
            public BooleanParcelable createFromParcel(android.os.Parcel parcel) {
                return new BooleanParcelable(parcel);
            }

            @Override
            public BooleanParcelable[] newArray(int size) {
                return new BooleanParcelable[size];
            }
        }
    }

    public static final class CharArrayParcelable extends ConverterParcelable<char[]> {

        private static final CharArrayParcelConverter CONVERTER = new CharArrayParcelConverter();

        public CharArrayParcelable(Parcel parcel) {
            super(parcel, CONVERTER);
        }

        public CharArrayParcelable(char[] value) {
            super(value, CONVERTER);
        }

        @SuppressWarnings("UnusedDeclaration")
        public static final CharArrayParcelableCreator CREATOR = new CharArrayParcelableCreator();

        private static final class CharArrayParcelableCreator implements Creator<CharArrayParcelable> {

            @Override
            public CharArrayParcelable createFromParcel(android.os.Parcel parcel) {
                return new CharArrayParcelable(parcel);
            }

            @Override
            public CharArrayParcelable[] newArray(int size) {
                return new CharArrayParcelable[size];
            }
        }
    }

    public static final class CharacterParcelable extends ConverterParcelable<Character> {

        private static final NullableParcelConverter<Character> CONVERTER = new NullableParcelConverter<Character>() {

            @Override
            public Character nullSafeFromParcel(Parcel parcel) {
                return parcel.createCharArray()[0];
            }

            @Override
            public void nullSafeToParcel(Character input, Parcel parcel, int flags) {
                parcel.writeCharArray(new char[]{input});
            }
        };

        public CharacterParcelable(Parcel parcel) {
            super(parcel, CONVERTER);
        }

        public CharacterParcelable(Character value) {
            super(value, CONVERTER);
        }

        @SuppressWarnings("UnusedDeclaration")
        public static final CharacterParcelableCreator CREATOR = new CharacterParcelableCreator();

        private static final class CharacterParcelableCreator implements Creator<CharacterParcelable> {

            @Override
            public CharacterParcelable createFromParcel(android.os.Parcel parcel) {
                return new CharacterParcelable(parcel);
            }

            @Override
            public CharacterParcelable[] newArray(int size) {
                return new CharacterParcelable[size];
            }
        }
    }

    public static final class StringParcelable implements Parcelable, ParcelWrapper<String> {

        private String contents;

        @SuppressWarnings("UnusedDeclaration")
        public static final StringParcelableCreator CREATOR = new StringParcelableCreator();

        @SuppressWarnings("unchecked")
        private StringParcelable(android.os.Parcel parcel) {
            contents = parcel.readString();
        }

        private StringParcelable(String contents) {
            this.contents = contents;
        }

        @Override
        public void writeToParcel(android.os.Parcel parcel, int flags) {
            parcel.writeString(contents);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public String getParcel() {
            return contents;
        }

        private static final class StringParcelableCreator implements Creator<StringParcelable> {

            @Override
            public StringParcelable createFromParcel(android.os.Parcel parcel) {
                return new StringParcelable(parcel);
            }

            @Override
            public StringParcelable[] newArray(int size) {
                return new StringParcelable[size];
            }
        }
    }

    private static class ConverterParcelable<T> implements Parcelable, ParcelWrapper<T> {

        private final T value;
        private final TypeRangeParcelConverter<T, T> converter;

        @SuppressWarnings("unchecked")
        private ConverterParcelable(android.os.Parcel parcel, TypeRangeParcelConverter<T, T> converter) {
            this(converter.fromParcel(parcel), converter);
        }

        private ConverterParcelable(T value, TypeRangeParcelConverter<T, T> converter) {
            this.converter = converter;
            this.value = value;
        }

        @Override
        public void writeToParcel(android.os.Parcel parcel, int flags) {
            converter.toParcel(value, parcel, flags);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public T getParcel() {
            return value;
        }
    }

    public static final class ParcelableParcelable implements Parcelable, ParcelWrapper<Parcelable> {

        private Parcelable parcelable;

        private ParcelableParcelable(android.os.Parcel parcel) {
            parcelable = parcel.readParcelable(ParcelableParcelable.class.getClassLoader());
        }

        private ParcelableParcelable(Parcelable parcelable) {
            this.parcelable = parcelable;
        }

        @Override
        public void writeToParcel(android.os.Parcel parcel, int flags) {
            parcel.writeParcelable(parcelable, flags);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public Parcelable getParcel() {
            return parcelable;
        }

        public static final ParcelableParcelableCreator CREATOR = new ParcelableParcelableCreator();

        private static final class ParcelableParcelableCreator implements Creator<ParcelableParcelable> {

            @Override
            public ParcelableParcelable createFromParcel(android.os.Parcel parcel) {
                return new ParcelableParcelable(parcel);
            }

            @Override
            public ParcelableParcelable[] newArray(int size) {
                return new ParcelableParcelable[size];
            }
        }
    }
}