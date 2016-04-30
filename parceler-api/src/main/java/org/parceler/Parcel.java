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

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Identifies a class to be wrapped by a `Parcelable` wrapper.
 * This wrapper will serialize an instance of the current class to and from a Parcelable representation based on the serialization technique `value` if no `ParcelConverter` value is specified.
 * If a ParcelConverter is specified it will be used instead.
 *
 * Only a select number of types may be used as attributes of a `@Parcel` class.
 * The following list includes the mapped types:
 *
 * - `byte`
 * - `double`
 * - `float`
 * - `int`
 * - `long`
 * - `char`
 * - `boolean`
 * - `String`
 * - `IBinder`
 * - `Bundle`
 * - `SparseArray` of any of the mapped types*
 * - `SparseBooleanArray`
 * - `List`, `ArrayList` and `LinkedList` of any of the mapped types*
 * - `Map`, `HashMap`, `LinkedHashMap`, `SortedMap`, and `TreeMap` of any of the mapped types*
 * - `Set`, `HashSet`, `SortedSet`, `TreeSet`, `LinkedHashSet` of any of the mapped types*
 * - `Parcelable`
 * - `Serializable`
 * - Array of any of the mapped types
 * - Any other class annotated with `@Parcel`
 *
 * Parcel will error if the generic parameter is not mapped.
 *
 * Parceler also supports any of the above types directly.
 * This is especially useful when dealing with collections of classes annotated with `@Parcel`:
 *
 * [source,java]
 * ----
 * Parcelable listParcelable = Parcels.wrap(new ArrayList<Example>());
 * Parcelable mapParcelable = Parcels.wrap(new HashMap<String, Example>());
 * ----
 *
 * Properties that should not be serialized can be annotated with the `@Transient` annotation on either the getter or setter.
 * Parceler will ignore `@Transient` annotated properties during Parcelable serialization.
 *
 * @author John Ericksen
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface Parcel {

    /**
     * Specifies the technique for which Parceler will serialize.
     */
    Serialization value() default Serialization.FIELD;

    /**
     * The concrete implementations that should be mapped to this class for use with the Parcels utility class.
     */
    Class[] implementations() default {};

    /**
     * No longer used.
     */
    @Deprecated
    boolean parcelsIndex() default true;

    /**
     * Identifies the types to include in analysis.
     * Parceler will walk to inheritance extension hierarchy only including the classes specified, if the list of classes is non-empty.
     */
    Class[] analyze() default {};

    /**
     * `ParcelConverter` to use when serializing this class.
     * Parceler will attempt to generate serialization specifics if this converter is not specified.
     */
    Class<? extends TypeRangeParcelConverter> converter() default ParcelConverter.EmptyConverter.class;

    enum Serialization {
        /**
         * Read and write fields directly.
         */
        FIELD,
        /**
         * Deprecated, use `BEAN` instead
         */
        @Deprecated
        METHOD,
        /**
         * Read and write via the Bean standard, public matching getter and setters.
         */
        BEAN,
        /**
         * Read and write via value accessors and mutators.
         */
        VALUE
    }
}
