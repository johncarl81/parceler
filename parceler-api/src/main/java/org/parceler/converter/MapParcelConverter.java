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

import java.util.Map;

/**
 *
 * @author u61173
 */
public abstract class MapParcelConverter<K, V, M extends Map<K, V>> implements TypeRangeParcelConverter<Map<K, V>, M> {

    private static final int NULL = -1;
    
    @Override
    public void toParcel(Map<K, V> input, Parcel parcel) {
        if (input == null) {
            parcel.writeInt(NULL);
        } else {
            parcel.writeInt(input.size());
            for (Map.Entry<K, V> entry : input.entrySet()) {
                mapKeyToParcel(entry.getKey(), parcel);
                mapValueToParcel(entry.getValue(), parcel);
            }
        }
    }

    @Override
    public M fromParcel(Parcel parcel) {
        M map;
        int size = parcel.readInt();
        if (size == NULL) {
            map = null;
        } else {
            map = createMap();
            for (int i = 0; (i < size); i++) {
                K key = mapKeyFromParcel(parcel);
                V value = mapValueFromParcel(parcel);
                map.put(key, value);
            }
        }
        return map;
    }

    public abstract M createMap();

    public abstract void mapKeyToParcel(K key, Parcel parcel);
    public abstract void mapValueToParcel(V value, Parcel parcel);

    public abstract K mapKeyFromParcel(Parcel parcel);
    public abstract V mapValueFromParcel(Parcel parcel);
}