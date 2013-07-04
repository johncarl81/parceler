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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Identifies a class to be wrapped by a `Parcelable` wrapper.  This wrapper will serialize an instance of the current
 * class to and from a Parcelable representation based on the public Java-Bean standard property Getters and Setters if
 * no `ParcelConverter` value is specified.  If a ParcelConverter is specified it will be used instead.
 *
 * The following types are available as property types, which correspond to the types accepted by the Android Bundle:
 * 
 * - `byte`
 * - `byte[]`
 * - `double`
 * - `double[]`
 * - `float`
 * - `float[]`
 * - `int`
 * - `int[]`
 * - `long`
 * - `long[]`
 * - `String`
 * - `String[]`
 * - `IBinder`
 * - `Bundle`
 * - `Object[]`
 * - `SparseArray`
 * - `SparseBooleanArray`
 * - `Exception`
 * - Other classes annotated with `@Parcel`
 *
 * Instances annotated with `@Parcel` may be used as extras when passing values between Components.  Parceler
 * will automatically wrap and unwrap the given instance with the generated wrapper.
 *
 * Properties that should not be serialized can be annotated with the `@Transient` annotation on either the getter
 * or setter.  Parceler will ignore `@Transient` annotated properties during Parcelable serialization.
 *
 * @author John Ericksen
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Parcel {

    /**
     * Optional Converter class.
     */
    Class<? extends ParcelConverter> value() default EmptyConverter.class;

    /**
     * Noop ParcelConverter used as a empty placeholder for the Parcel.value annotation parameter.  Performs no mapping
     * and throws `ParcelerRuntimeExceptions` upon calling any method.
     */
    class EmptyConverter implements ParcelConverter<Object> {
        @Override
        public void toParcel(Object input, android.os.Parcel destinationParcel) {
            throw new ParcelerRuntimeException("Empty Converter should not be used.");
        }

        @Override
        public Object fromParcel(android.os.Parcel parcel) {
            throw new ParcelerRuntimeException("Empty Converter should not be used.");
        }
    }
}