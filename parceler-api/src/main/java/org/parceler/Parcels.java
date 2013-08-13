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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Static utility class used to wrap an `@Parcel` annotated class with the generated `Parcelable` wrapper.
 *
 * @author John Ericksen
 */
public final class Parcels {

    public static final String PARCELS_NAME = "Parcels";
    public static final String PARCELS_REPOSITORY_NAME = "Parceler$Parcels";
    public static final String PARCELS_PACKAGE = "org.parceler";
    public static final String IMPL_EXT = "$Parcel";

    private static final ParcelCodeRepository REPOSITORY = new ParcelCodeRepository();

    private Parcels(){
        // private utility class constructor
    }

    /**
     * Testing method for replacing the Parceler$Parcels class with one referenced in the given classloader.
     *
     * @param classLoader
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
    public static <T> Parcelable wrap(T input) {
        ParcelableFactory parcelableFactory = REPOSITORY.get(input.getClass());

        return parcelableFactory.buildParcelable(input);
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

    private static final class ParcelableFactoryReflectionProxy<T> implements Parcels.ParcelableFactory<T> {

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
                    throw new ParcelerRuntimeException("Unable to create ParcelableFactory");
                }
                result = generatedMap.putIfAbsent(clazz, value);
                if (result == null) {
                    result = value;
                }
            }

            return result;
        }

        public ParcelableFactory findClass(Class clazz){
            try {
                Class parcelWrapperClass = Class.forName(clazz.getName() + IMPL_EXT);
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
        public void loadRepository(ClassLoader classLoader){
            try{
                Class repositoryClass = classLoader.loadClass(PARCELS_PACKAGE + "." + PARCELS_REPOSITORY_NAME);
                Repository<ParcelableFactory> instance = (Repository<ParcelableFactory>) repositoryClass.newInstance();
                generatedMap.putAll(instance.get());

            } catch (ClassNotFoundException e) {
                //nothing
            } catch (InstantiationException e) {
                throw new ParcelerRuntimeException("Unable to instantiate generated Repository", e);
            } catch (IllegalAccessException e) {
                throw new ParcelerRuntimeException("Unable to access generated Repository", e);
            }
        }
    }

}
