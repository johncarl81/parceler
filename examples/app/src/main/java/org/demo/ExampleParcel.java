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
package org.demo;

import com.google.auto.value.AutoValue;
import org.parceler.Parcel;
import org.parceler.ParcelFactory;
import org.parceler.ParcelProperty;

/**
 * Intentionally in a different package to make sure we don't accidentally match it with org.parceler Proguard matchers.
 */
@AutoValue
@Parcel
public abstract class ExampleParcel {

    @ParcelProperty("message")
    public abstract String getMessage();

    @ParcelFactory
    public static ExampleParcel create(@ParcelProperty("message") String message) {
        return new AutoValue_ExampleParcel(message);
    }
}