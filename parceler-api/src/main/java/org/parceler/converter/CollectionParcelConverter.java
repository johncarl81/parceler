/*
 * Copyright 2015 u61173.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.parceler.converter;

import android.os.Parcel;
import org.parceler.TypeRangeParcelConverter;

import java.util.Collection;

/**
 *
 * @author u61173
 */
public abstract class CollectionParcelConverter<T, C extends Collection<T>> implements TypeRangeParcelConverter<Collection<T>, C> {

    private static final int NULL = -1;

    @Override
    public void toParcel(Collection<T> input, Parcel parcel) {
        if (input == null) {
            parcel.writeInt(NULL);
        } else {
            parcel.writeInt(input.size());
            for (T item : input) {
                itemToParcel(item, parcel);
            }
        }
    }

    @Override
    public C fromParcel(Parcel parcel) {
        C list;
        int size = parcel.readInt();
        if (size == NULL) {
            list = null;
        } else {
            list = createCollection();
            for (int i = 0; (i < size); i++) {
                list.add(itemFromParcel(parcel));
            }
        }
        return list;
    }

    public abstract void itemToParcel(T input, Parcel parcel);
    public abstract T itemFromParcel(Parcel parcel);
    public abstract C createCollection();
}