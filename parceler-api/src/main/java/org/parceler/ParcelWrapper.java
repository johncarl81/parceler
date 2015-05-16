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
 * Defines the getter for the contents of a wrapped `@Parcel` instance.
 *
 * @author John Ericksen
 */
public interface ParcelWrapper<T> {

    String GET_PARCEL = "getParcel";

    /**
     * Return wrapped `@Parcel` instance
     *
     * @return `@Parcel` instance
     */
    T getParcel();
}