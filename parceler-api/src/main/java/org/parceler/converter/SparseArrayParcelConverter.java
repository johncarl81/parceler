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
package org.parceler.converter;

import android.os.Parcel;
import android.util.SparseArray;
import org.parceler.ParcelConverter;

/**
 * @author John Ericksen
 */
public abstract class SparseArrayParcelConverter<T> implements ParcelConverter<SparseArray<T>> {
    @Override
    public void toParcel(SparseArray<T> input, Parcel parcel, int flags) {
        if (input == null) {
            parcel.writeInt(-1);
        } else {
            parcel.writeInt(input.size());
            for (int i = 0; (i < input.size()); i++) {
                parcel.writeInt(input.keyAt(i));
                itemToParcel(input.valueAt(i), parcel, flags);
            }
        }
    }

    @Override
    public SparseArray<T> fromParcel(Parcel parcel) {
        SparseArray<T> array;
        int size = parcel.readInt();
        if (size < 0) {
            array = null;
        } else {
            array = new SparseArray<T>(size);
            for (int i = 0; (i < size); i++) {
                int key = parcel.readInt();
                array.append(key, itemFromParcel(parcel));
            }
        }
        return array;
    }

    public abstract void itemToParcel(T input, Parcel parcel, int flags);
    public abstract T itemFromParcel(Parcel parcel);
}
