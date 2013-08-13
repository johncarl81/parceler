/**
 * Copyright 2013 John Ericksen
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

import android.os.Parcel;

/**
 * @author John Ericksen
 */
public class ManuallyRegisteredConverter implements ParcelConverter<ManuallyRegistered> {

    @Override
    public void toParcel(ManuallyRegistered input, Parcel destinationParcel) {
        destinationParcel.writeString(input.getValue());
    }

    @Override
    public ManuallyRegistered fromParcel(Parcel parcel) {
        ManuallyRegistered serialized = new ManuallyRegistered();

        serialized.setValue(parcel.readString());

        return serialized;
    }
}
