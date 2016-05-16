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
package org.parceler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author John Ericksen
 */
public final class IdentityCollection {

    private static final Object RESERVATION = new Object();
    private int sequence = 0;
    private final List<Object> values = new ArrayList<Object>();

    public IdentityCollection() {
        put(null);
    }

    public boolean containsKey(int id){
        return id < sequence;
    }

    public boolean containsValue(Object value){
        return values.contains(value);
    }

    public int reserve() {
        return put(RESERVATION);
    }

    public boolean isReserved(int id) {
        return values.get(id) == RESERVATION;
    }

    public void put(int id, Object input){
        values.add(id, input);
    }

    public int put(Object input) {
        int current = sequence++;
        put(current, input);
        return current;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(int id){
        return (T) values.get(id);
    }

    public int getKey(Object input) {
        return values.indexOf(input);
    }
}
