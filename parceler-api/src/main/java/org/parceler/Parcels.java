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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Static utility class used to wrap an `@Parcel` annotated class with the generated `Parcelable` wrapper.
 *
 * @author John Ericksen
 */
public final class Parcels {

    public static final String PARCELS_NAME = "Parcels";
    public static final String PARCELS_REPOSITORY_NAME = "Parceler$$Parcels";
    public static final String PARCELS_PACKAGE = "org.parceler";
    public static final String IMPL_EXT = "Parcelable";

    private static final ParcelCodeRepository REPOSITORY = new ParcelCodeRepository();

    static{
        REPOSITORY.loadRepository(new CollectionsRepository());
    }

    private Parcels(){
        // private utility class constructor
    }

    /**
     * Testing method for replacing the Parceler$Parcels class with one referenced in the given classloader.
     *
     * @param classLoader ClassLoader to use when loading repository.
     */
    protected static void update(ClassLoader classLoader){
        REPOSITORY.loadRepository(classLoader);
    }

    /**
     * Wraps the input `@Parcel` annotated class with a `Parcelable` wrapper.
     *
     * @throws ParcelerRuntimeException if there was an error looking up the wrapped Parceler$Parcels class.
     * @param input Parcel
     * @return Parcelable wrapper
     */
    @SuppressWarnings("unchecked")
    public static <T> Parcelable wrap(T input) {
        if(input == null){
            return null;
        }
        ParcelableFactory parcelableFactory = REPOSITORY.get(input.getClass());

        return parcelableFactory.buildParcelable(input);
    }

    /**
     * Unwraps the input wrapped `@Parcel` `Parcelable`
     *
     * @throws ClassCastException if the input Parcelable does not implement ParcelWrapper with the correct parameter type.
     * @param input Parcelable implementing ParcelWrapper
     * @param <T> type of unwrapped `@Parcel`
     * @return Unwrapped `@Parcel`
     */
    @SuppressWarnings("unchecked")
    public static <T> T unwrap(Parcelable input) {
        if(input == null){
            return null;
        }
        ParcelWrapper<T> wrapper = (ParcelWrapper<T>) input;
        return wrapper.getParcel();
    }

    /**
     * Factory class for building a `Parcelable` from the given input.
     */
    public interface ParcelableFactory<T> {

        String BUILD_PARCELABLE = "buildParcelable";

        /**
         * Build the corresponding `Parcelable` class.
         *
         * @param input input to wrap with a Parcelable
         * @return Parcelable instance
         */
        Parcelable buildParcelable(T input);
    }

    private static final class ParcelableFactoryReflectionProxy<T> implements ParcelableFactory<T> {

        private final Constructor<? extends Parcelable> constructor;

        public ParcelableFactoryReflectionProxy(Class<T> parcelClass, Class<? extends Parcelable> parcelWrapperClass) {
            try {
                this.constructor = parcelWrapperClass.getConstructor(parcelClass);
            } catch (NoSuchMethodException e) {
                throw new ParcelerRuntimeException("Unable to create ParcelFactory Type", e);
            }
        }

        @Override
        public Parcelable buildParcelable(T input) {
            try {
                return constructor.newInstance(input);
            } catch (InstantiationException e) {
                throw new ParcelerRuntimeException("Unable to create ParcelFactory Type", e);
            } catch (IllegalAccessException e) {
                throw new ParcelerRuntimeException("Unable to create ParcelFactory Type", e);
            } catch (InvocationTargetException e) {
                throw new ParcelerRuntimeException("Unable to create ParcelFactory Type", e);
            }
        }
    }

    private static final class ParcelCodeRepository {

        private ConcurrentMap<Class, ParcelableFactory> generatedMap = new ConcurrentHashMap<Class, ParcelableFactory>();

        public ParcelCodeRepository() {
            loadRepository(getClass().getClassLoader());
        }

        public ParcelableFactory get(Class clazz){
            ParcelableFactory result = generatedMap.get(clazz);
            if (result == null) {
                ParcelableFactory value = findClass(clazz);
                if(value == null){
                    throw new ParcelerRuntimeException("Unable to create ParcelableFactory for " + clazz.getName());
                }
                result = generatedMap.putIfAbsent(clazz, value);
                if (result == null) {
                    result = value;
                }
            }

            return result;
        }

        @SuppressWarnings("unchecked")
        public ParcelableFactory findClass(Class clazz){
            try {
                Class parcelWrapperClass = Class.forName(clazz.getName() + "$$" + IMPL_EXT);
                return new ParcelableFactoryReflectionProxy(clazz, parcelWrapperClass);
            } catch (ClassNotFoundException e) {
                return null;
            }
        }

        /**
         * Update the repository class from the given classloader.  If the given repository class cannot be instantiated
         * then this method will throw a ParcelerRuntimeException.
         *
         * @throws ParcelerRuntimeException
         * @param classLoader
         */
        @SuppressWarnings("unchecked")
        public void loadRepository(ClassLoader classLoader){
            try{
                Class repositoryClass = classLoader.loadClass(PARCELS_PACKAGE + "." + PARCELS_REPOSITORY_NAME);
                loadRepository((Repository<ParcelableFactory>) repositoryClass.newInstance());


            } catch (ClassNotFoundException e) {
                //nothing
            } catch (InstantiationException e) {
                throw new ParcelerRuntimeException("Unable to instantiate generated Repository", e);
            } catch (IllegalAccessException e) {
                throw new ParcelerRuntimeException("Unable to access generated Repository", e);
            }
        }

        public void loadRepository(Repository<ParcelableFactory> repository){
            generatedMap.putAll(repository.get());
        }
    }

    private static final class CollectionsRepository implements Repository<ParcelableFactory>  {

        private static final Map<Class, ParcelableFactory> PARCEL_COLLECTIONS = new HashMap<Class, ParcelableFactory>();

        static{
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

        private static class ListParcelableFactory implements Parcels.ParcelableFactory<List>{

            @Override
            public Parcelable buildParcelable(List input) {
                return new ListParcelable(input);
            }
        }

        private static class SetParcelableFactory implements Parcels.ParcelableFactory<Set>{

            @Override
            public Parcelable buildParcelable(Set input) {
                return new SetParcelable(input);
            }
        }

        private static class MapParcelableFactory implements Parcels.ParcelableFactory<Map>{

            @Override
            public Parcelable buildParcelable(Map input) {
                return new MapParcelable(input);
            }
        }

        private static class SparseArrayParcelableFactory implements Parcels.ParcelableFactory<SparseArray>{

            @Override
            public Parcelable buildParcelable(SparseArray input) {
                return new SparseArrayParcelable(input);
            }
        }
    }

    public final static class ListParcelable implements Parcelable, ParcelWrapper<List> {

        private List contents;

        @SuppressWarnings("UnusedDeclaration")
        public final ListParcelableCreator CREATOR = new ListParcelableCreator();

        public ListParcelable(android.os.Parcel parcel) {
            int size = parcel.readInt();
            if (size < 0) {
                contents = null;
            } else {
                contents = new ArrayList<java.lang.String>();
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
                parcel$$16 .writeInt(-1);
            } else {
                parcel$$16 .writeInt(contents.size());
                for (Object c : contents) {
                    parcel$$16 .writeParcelable(Parcels.wrap(c), flags);
                }
            }
        }

        @Override
        public int describeContents() {
            return  0;
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
                return new ListParcelable[size] ;
            }
        }
    }

    public static final class SetParcelable implements Parcelable, ParcelWrapper<Set> {

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
                parcel$$16 .writeInt(-1);
            } else {
                parcel$$16 .writeInt(contents.size());
                for (Object c : contents) {
                    parcel$$16 .writeParcelable(Parcels.wrap(c), flags);
                }
            }
        }

        @Override
        public int describeContents() {
            return  0;
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
                return new SetParcelable[size] ;
            }

        }

    }

    public static final class MapParcelable implements android.os.Parcelable, ParcelWrapper<Map> {

        private Map<Object, Object> contents;
        @SuppressWarnings("UnusedDeclaration")
        public final static MapParcelable.MapParcelableCreator CREATOR = new MapParcelable.MapParcelableCreator();

        public MapParcelable(android.os.Parcel parcel) {
            int size = parcel .readInt();
            if (size < 0) {
                contents = null;
            } else {
                contents = new HashMap<Object, Object>();
                for (int i = 0; (i < size); i++) {
                    Parcelable key = parcel.readParcelable(ClassLoader.getSystemClassLoader());
                    Parcelable value = parcel.readParcelable(ClassLoader.getSystemClassLoader());
                    contents .put(Parcels.unwrap(key), Parcels.unwrap(value));
                }
            }
        }

        public MapParcelable(Map contents) {
            this.contents = contents;
        }

        @Override
        public void writeToParcel(android.os.Parcel parcel, int flags) {
            if (contents == null) {
                parcel .writeInt(-1);
            } else {
                parcel .writeInt(contents.size());
                for (Map.Entry<Object, Object> entry : contents.entrySet()) {
                    parcel .writeParcelable(Parcels.wrap(entry.getKey()), flags);
                    parcel .writeParcelable(Parcels.wrap(entry.getValue()), flags);
                }
            }
        }

        @Override
        public int describeContents() {
            return  0;
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
                return new MapParcelable[size] ;
            }

        }

    }

    public static final class SparseArrayParcelable implements android.os.Parcelable, ParcelWrapper<SparseArray> {

        private SparseArray contents;
        @SuppressWarnings("UnusedDeclaration")
        public final static SparseArrayCreator CREATOR = new SparseArrayCreator();

        public SparseArrayParcelable(android.os.Parcel parcel) {
            int size = parcel .readInt();
            if (size < 0) {
                contents = null;
            } else {
                contents = new android.util.SparseArray<android.os.Parcelable>(size);
                for (int i = 0; (i <size); i ++) {
                    int key = parcel .readInt();
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
                parcel .writeInt(-1);
            } else {
                parcel .writeInt(contents.size());
                for (int i = 0 ; (i < contents .size()); i ++) {
                    parcel .writeInt(contents.keyAt(i));
                    parcel .writeParcelable(Parcels.wrap(contents.valueAt(i)), flags);
                }
            }
        }

        @Override
        public int describeContents() {
            return  0;
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
}
