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

import android.os.Parcelable;

/**
 * @author John Ericksen
 */
public class ParcelsTestUtil {

    public static android.os.Parcel wrap(Object input){
        android.os.Parcel parcel = android.os.Parcel.obtain();

        Parcelable parcelable = Parcels.wrap(input);
        parcelable.writeToParcel(parcel, 0);

        return parcel;
    }
}
