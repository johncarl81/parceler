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

/**
 * @author John Ericksen
 */
public final class MapsUtil {

    public static final String INITIAL_HASH_MAP_CAPACITY_METHOD = "initialHashMapCapacity";

    private static final int MAX_POWER_OF_TWO = 1 << (Integer.SIZE - 2);

    private MapsUtil() {
        //
    }

    /**
     * Copy of capacity method from Guava Maps utility.
     *
     * Returns a capacity that is sufficient to keep the map from being resized as long as it grows no
     * larger than expectedSize and the load factor is ≥ its default (0.75).
     *
     * @param expectedSize
     * @return HashMap capacity that avoids rehashing.
     */
    static int initialHashMapCapacity(int expectedSize) {
        if (expectedSize < 0) {
            throw new ParcelerRuntimeException("Expected size must be non-negative");
        }
        if (expectedSize < 3) {
            return expectedSize + 1;
        }
        if (expectedSize < MAX_POWER_OF_TWO) {
            // This is the calculation used in JDK8 to resize when a putAll
            // happens; it seems to be the most conservative calculation we
            // can make.  0.75 is the default load factor.
            return (int) ((float) expectedSize / 0.75F + 1.0F);
        }
        return Integer.MAX_VALUE; // any large value
    }
}
