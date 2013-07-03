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

/**
 * Defines a concrete serialization of an instance to and from a Parcel.
 *
 * @author John Ericksen
 */
public interface ParcelConverter<T> {

    String CONVERT_TO_PARCEL = "toParcel";
    String CONVERT_FROM_PARCEL = "fromParcel";

    /**
     * Write the given input parameter to the destinationParcel.
     *
     * @param input T instance
     * @param destinationParcel Parcel to write to
     */
    void toParcel(T input, android.os.Parcel destinationParcel);

    /**
     * Generates an instance from the values provided in the given parcel.
     *
     * @param parcel Parcel to read from
     * @return instance of the mapped class.
     */
    T fromParcel(android.os.Parcel parcel);
}
