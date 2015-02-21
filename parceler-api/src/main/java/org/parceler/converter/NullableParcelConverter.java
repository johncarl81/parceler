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
package org.parceler.converter;

import android.os.Parcel;
import org.parceler.ParcelConverter;

/**
 * Convenience class to handle nullable objects.
 *
 * @author John Ericksen
 */
public abstract class NullableParcelConverter<T> implements ParcelConverter<T> {

    private static final int NULL = -1;
    private static final int NOT_NULL = 1;

    @Override
    public void toParcel(T input, Parcel parcel) {
        if (input == null) {
            parcel.writeInt(NULL);
        } else {
            parcel.writeInt(NOT_NULL);
            nullSafeToParcel(input, parcel);
        }
    }

    @Override
    public T fromParcel(Parcel parcel) {
        T result;
        if (parcel .readInt() == NULL) {
            result = null;
        } else {
            result = nullSafeFromParcel(parcel);
        }
        return result;
    }

    public abstract void nullSafeToParcel(T input, Parcel parcel);
    public abstract T nullSafeFromParcel(Parcel parcel);
}
