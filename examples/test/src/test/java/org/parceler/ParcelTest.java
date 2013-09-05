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
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author John Ericksen
 */
public class ParcelTest {

    @Test
    public void testParcel(){
        String one = "one";
        int two = 2;
        long three = 3;
        String name = "name";
        SubParcel sub = new SubParcel(name);

        ExampleParcel example = new ExampleParcel(one, two, three, sub);

        Parcelable parcelable = Parcels.wrap(example);
        ExampleParcel parcel = Parcels.unwrap(parcelable);

        assertEquals(one, parcel.getOne());
        assertEquals(two, parcel.getTwo());
        assertEquals(three, parcel.getThree());
        assertEquals(sub, parcel.getFour());
        assertEquals(name, parcel.getFour().getName());
    }

    @Test
    public void testManualSerialization(){

        String value = "test";

        Manual input = new Manual();
        input.setValue(value);

        Parcelable parcelable = Parcels.wrap(input);
        Manual output = Parcels.unwrap(parcelable);

        assertEquals(input.getValue(), output.getValue());
    }

    @Test
    public void testManuallyRegistered(){

        String value = "test";

        ExternalParcel input = new ExternalParcel();
        input.setValue(value);

        Parcelable parcelable = Parcels.wrap(input);
        ExternalParcel output = Parcels.unwrap(parcelable);

        assertEquals(input.getValue(), output.getValue());
    }

    @Test
    public void testManuallyRegisteredSerialization(){

        String value = "test";

        ManuallyRegistered input = new ManuallyRegistered();
        input.setValue(value);

        Parcelable parcelable = Parcels.wrap(input);
        ManuallyRegistered output = Parcels.unwrap(parcelable);

        assertEquals(input.getValue(), output.getValue());
    }
}
