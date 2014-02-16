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

final class NonParcelRepository implements Repository<Parcels.ParcelableFactory> {

    private static final NonParcelRepository INSTANCE = new NonParcelRepository();
    private final Map<Class, Parcels.ParcelableFactory> parcelableCollectionFactories = new HashMap<Class, Parcels.ParcelableFactory>();

    private NonParcelRepository() {
        //private singleton constructor
        parcelableCollectionFactories.put(List.class, new ListParcelableFactory());
        parcelableCollectionFactories.put(ArrayList.class, new ListParcelableFactory());
        parcelableCollectionFactories.put(Set.class, new SetParcelableFactory());
        parcelableCollectionFactories.put(HashSet.class, new SetParcelableFactory());
        parcelableCollectionFactories.put(SparseArray.class, new SparseArrayParcelableFactory());
        parcelableCollectionFactories.put(Map.class, new MapParcelableFactory());
        parcelableCollectionFactories.put(HashMap.class, new MapParcelableFactory());
        parcelableCollectionFactories.put(Integer.class, new IntegerParcelableFactory());
        parcelableCollectionFactories.put(Long.class, new LongParcelableFactory());
        parcelableCollectionFactories.put(Double.class, new DoubleParcelableFactory());
        parcelableCollectionFactories.put(Float.class, new FloatParcelableFactory());
        parcelableCollectionFactories.put(Byte.class, new ByteParcelableFactory());
        parcelableCollectionFactories.put(String.class, new StringParcelableFactory());
    }

    static {

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
        public static final SetParcelableCreator CREATOR = new SetParcelableCreator();

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

    static final class IntegerParcelable implements Parcelable, ParcelWrapper<Integer> {

        private Integer contents;

        @SuppressWarnings("UnusedDeclaration")
        public static final IntegerParcelableCreator CREATOR = new IntegerParcelableCreator();

        @SuppressWarnings("unchecked")
        IntegerParcelable(android.os.Parcel parcel) {
            if(parcel.readInt() == 1){
                contents = parcel.readInt();
            }
            else{
                contents = null;
            }
        }

        IntegerParcelable(Integer contents) {
            this.contents = contents;
        }

        @Override
        public void writeToParcel(android.os.Parcel parcel, int flags) {
            if(contents == null){
                parcel.writeInt(-1);
            }
            else{
                parcel.writeInt(1);
                parcel.writeInt(contents);
            }
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public Integer getParcel() {
            return contents;
        }

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

    static final class LongParcelable implements Parcelable, ParcelWrapper<Long> {

        private Long contents;

        @SuppressWarnings("UnusedDeclaration")
        public static final LongParcelableCreator CREATOR = new LongParcelableCreator();

        @SuppressWarnings("unchecked")
        LongParcelable(android.os.Parcel parcel) {
            if(parcel.readInt() == 1){
                contents = parcel.readLong();
            }
            else{
                contents = null;
            }
        }

        LongParcelable(Long contents) {
            this.contents = contents;
        }

        @Override
        public void writeToParcel(android.os.Parcel parcel, int flags) {
            if(contents == null){
                parcel.writeInt(-1);
            }
            else{
                parcel.writeInt(1);
                parcel.writeLong(contents);
            }
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public Long getParcel() {
            return contents;
        }

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

    static final class DoubleParcelable implements Parcelable, ParcelWrapper<Double> {

        private Double contents;

        @SuppressWarnings("UnusedDeclaration")
        public static final DoubleParcelableCreator CREATOR = new DoubleParcelableCreator();

        @SuppressWarnings("unchecked")
        DoubleParcelable(android.os.Parcel parcel) {
            if(parcel.readInt() == 1){
                contents = parcel.readDouble();
            }
            else{
                contents = null;
            }
        }

        DoubleParcelable(Double contents) {
            this.contents = contents;
        }

        @Override
        public void writeToParcel(android.os.Parcel parcel, int flags) {
            if(contents == null){
                parcel.writeInt(-1);
            }
            else{
                parcel.writeInt(1);
                parcel.writeDouble(contents);
            }
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public Double getParcel() {
            return contents;
        }

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

    static final class FloatParcelable implements Parcelable, ParcelWrapper<Float> {

        private Float contents;

        @SuppressWarnings("UnusedDeclaration")
        public static final FloatParcelableCreator CREATOR = new FloatParcelableCreator();

        @SuppressWarnings("unchecked")
        FloatParcelable(android.os.Parcel parcel) {
            if(parcel.readInt() == 1){
                contents = parcel.readFloat();
            }
            else{
                contents = null;
            }
        }

        FloatParcelable(Float contents) {
            this.contents = contents;
        }

        @Override
        public void writeToParcel(android.os.Parcel parcel, int flags) {
            if(contents == null){
                parcel.writeInt(-1);
            }
            else{
                parcel.writeInt(1);
                parcel.writeFloat(contents);
            }
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public Float getParcel() {
            return contents;
        }

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

    static final class ByteParcelable implements Parcelable, ParcelWrapper<Byte> {

        private Byte contents;

        @SuppressWarnings("UnusedDeclaration")
        public static final ByteParcelableCreator CREATOR = new ByteParcelableCreator();

        @SuppressWarnings("unchecked")
        ByteParcelable(android.os.Parcel parcel) {
            if(parcel.readInt() == 1){
                contents = parcel.readByte();
            }
            else{
                contents = null;
            }
        }

        ByteParcelable(Byte contents) {
            this.contents = contents;
        }

        @Override
        public void writeToParcel(android.os.Parcel parcel, int flags) {
            if(contents == null){
                parcel.writeInt(-1);
            }
            else{
                parcel.writeInt(1);
                parcel.writeByte(contents);
            }
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public Byte getParcel() {
            return contents;
        }

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

    static final class StringParcelable implements Parcelable, ParcelWrapper<String> {

        private String contents;

        @SuppressWarnings("UnusedDeclaration")
        public static final StringParcelableCreator CREATOR = new StringParcelableCreator();

        @SuppressWarnings("unchecked")
        StringParcelable(android.os.Parcel parcel) {
            System.out.println("READING STRING");
            contents = parcel.readString();
        }

        StringParcelable(String contents) {
            this.contents = contents;
        }

        @Override
        public void writeToParcel(android.os.Parcel parcel, int flags) {
            System.out.println("WRITING STRING");
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
}