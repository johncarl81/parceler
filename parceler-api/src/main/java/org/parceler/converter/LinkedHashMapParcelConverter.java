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

import java.util.LinkedHashMap;

/**
 *
 * @author u61173
 */
public abstract class LinkedHashMapParcelConverter<K, V> extends MapParcelConverter<K, V, LinkedHashMap<K, V>> {

    @Override
    public LinkedHashMap<K, V> createMap() {
        return new LinkedHashMap<K, V>();
    }
}