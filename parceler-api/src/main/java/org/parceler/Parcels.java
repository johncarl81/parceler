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

import android.os.Parcelable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.lang.ClassLoader;

/**
 * Static utility class used to wrap an `@Parcel` annotated class with the generated `Parcelable` wrapper.
 *
 * @author John Ericksen
 */
public final class Parcels {

    public static final String IMPL_EXT = "Parcelable";

    private static final ParcelCodeRepository REPOSITORY = new ParcelCodeRepository();

    static{
        REPOSITORY.loadRepository(NonParcelRepository.getInstance());
    }

    private Parcels(){
        // private utility class constructor
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
        return wrap(input.getClass(), input);
    }

    /**
     * Wraps the input `@Parcel` annotated class with a `Parcelable` wrapper.
     *
     * @throws ParcelerRuntimeException if there was an error looking up the wrapped Parceler$Parcels class.
     * @param inputType specific type to parcel
     * @param input Parcel
     * @return Parcelable wrapper
     */
    @SuppressWarnings("unchecked")
    public static <T> Parcelable wrap(Class<? extends T> inputType, T input) {
        if(input == null){
            return null;
        }
        ParcelableFactory parcelableFactory = REPOSITORY.get(inputType);

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

        public ParcelableFactory get(Class clazz){
            ParcelableFactory result = generatedMap.get(clazz);
            if (result == null) {
                ParcelableFactory value = findClass(clazz, clazz.getClassLoader());

                if (Parcelable.class.isAssignableFrom(clazz)) {
                    value = new NonParcelRepository.ParcelableParcelableFactory();
                }

                if(value == null){
                    throw new ParcelerRuntimeException(
                                    "Unable to find generated Parcelable class for " + clazz.getName() +
                                    ", verify that your class is configured properly and that the Parcelable class " +
                                    buildParcelableImplName(clazz) +
                                    " is generated by Parceler.");
                }
                result = generatedMap.putIfAbsent(clazz, value);
                if (result == null) {
                    result = value;
                }
            }

            return result;
        }

        private static String buildParcelableImplName(Class clazz){
            return clazz.getName() + "$$" + IMPL_EXT;
        }

        @SuppressWarnings("unchecked")
        public ParcelableFactory findClass(Class clazz, ClassLoader classLoader){
            try {
                Class parcelWrapperClass = classLoader.loadClass(buildParcelableImplName(clazz));
                return new ParcelableFactoryReflectionProxy(clazz, parcelWrapperClass);
            } catch (ClassNotFoundException e) {
                return null;
            }
        }

        public void loadRepository(Repository<ParcelableFactory> repository){
            generatedMap.putAll(repository.get());
        }
    }
}
