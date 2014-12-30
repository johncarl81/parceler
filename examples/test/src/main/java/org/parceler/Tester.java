/**
 * Copyright 2013-2015 John Ericksen
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
@ParcelClasses({
    @ParcelClass(ExternalParcel.class),
    @ParcelClass(value = ManuallyRegistered.class, converter = ManuallyRegisteredConverter.class)
})
public class Tester {

    public static void main(String[] args){

        ExampleParcel example = new ExampleParcel("one", 2, 3L, new SubParcel("name"));

        Parcelable parcelable = Parcels.wrap(example);

        ExampleParcel parcel = ((ParcelWrapper<ExampleParcel>) parcelable).getParcel();

        System.out.println("Should be one: " + parcel.getOne());
        System.out.println("Should be 2: " + parcel.getTwo());
        System.out.println("Should be 3: " + parcel.getThree());
        System.out.println("Should be name: " + parcel.getFour().getName());
    }
}
