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

/**
 * Static utility class used to wrap an `@Parcel` annotated class with the generated `Parcelable` wrapper.
 *
 * @author John Ericksen
 */
public final class Parcels {

    public static final String PARCELS_NAME = "Parcels";
    public static final String PARCELS_REPOSITORY_NAME = "Transfuse$Parcels";
    public static final String PARCELS_PACKAGE = "org.androidtransfuse";
    public static final String IMPL_EXT = "$Parcel";

    private static final GeneratedCodeRepository<ParcelableFactory> REPOSITORY =
            new GeneratedCodeRepository<ParcelableFactory>(PARCELS_PACKAGE, PARCELS_REPOSITORY_NAME) {
                @Override
                public ParcelableFactory findClass(Class clazz) {

                    try {
                        Class parcelWrapperClass = Class.forName(clazz.getName() + IMPL_EXT);
                        return new ParcelableFactoryReflectionProxy(clazz, parcelWrapperClass);
                    } catch (ClassNotFoundException e) {
                        return null;
                    }
                }
            };

    private Parcels(){
        // private utility class constructor
    }

    /**
     * Testing method for replacing the Transfuse$Parcels class with one referenced in the given classloader.
     *
     * @param classLoader
     */
    protected static void update(ClassLoader classLoader){
        REPOSITORY.loadRepository(classLoader, PARCELS_PACKAGE, PARCELS_REPOSITORY_NAME);
    }

    /**
     * Wraps the input `@Parcel` annotated class with a `Parcelable` wrapper.
     *
     * @throws ParcelerRuntimeException if there was an error looking up the wrapped
     * Transfuse$Parcels class.
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
}
