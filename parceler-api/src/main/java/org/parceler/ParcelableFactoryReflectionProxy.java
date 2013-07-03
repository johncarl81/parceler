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

/**
 * @author John Ericksen
 */
public class ParcelableFactoryReflectionProxy<T> implements Parcels.ParcelableFactory<T> {

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
