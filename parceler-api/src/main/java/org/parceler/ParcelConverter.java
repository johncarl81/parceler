/**
 * Copyright 2013-2015 John Ericksen
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

/**
 * Defines a concrete serialization of an instance to and from a Parcel.
 *
 * @author John Ericksen
 */
public interface ParcelConverter<T> extends TypeRangeParcelConverter<T, T> {

    String CONVERT_TO_PARCEL = "toParcel";
    String CONVERT_FROM_PARCEL = "fromParcel";

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
