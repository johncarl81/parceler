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
import android.os.Parcelable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;

/**
 * @author John Ericksen
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE)
public class ParcelTest {

    @Test
    public void testParcel(){
        String one = "one";
        int two = 2;
        long three = 3;
        String name = "name";
        SubParcel sub = new SubParcel(name);

        ExampleParcel example = new ExampleParcel(one, two, three, sub);

        ExampleParcel exampleParcel = Parcels.unwrap(new ExampleParcel$$Parcelable(wrap(example)));

        assertEquals(one, exampleParcel.getOne());
        assertEquals(two, exampleParcel.getTwo());
        assertEquals(three, exampleParcel.getThree());
        assertEquals(sub, exampleParcel.getFour());
        assertEquals(name, exampleParcel.getFour().getName());
    }

    @Test
    public void testManualSerialization(){

        String value = "test";

        Manual input = new Manual();
        input.setValue(value);

        Manual output = Parcels.unwrap(new Manual$$Parcelable(wrap(input)));

        assertEquals(input.getValue(), output.getValue());
    }

    @Test
    public void testManuallyRegistered(){

        String value = "test";

        ExternalParcel input = new ExternalParcel();
        input.setValue(value);

        ExternalParcel output = Parcels.unwrap(new ExternalParcel$$Parcelable(wrap(input)));

        assertEquals(input.getValue(), output.getValue());
    }

    @Test
    public void testManuallyRegisteredSerialization(){

        String value = "test";

        ManuallyRegistered input = new ManuallyRegistered();
        input.setValue(value);

        ManuallyRegistered output = Parcels.unwrap(new ManuallyRegistered$$Parcelable(wrap(input)));

        assertEquals(input.getValue(), output.getValue());
    }

    private Parcel wrap(Object input){
        android.os.Parcel parcel = android.os.Parcel.obtain();

        Parcelable parcelable = Parcels.wrap(input);
        parcelable.writeToParcel(parcel, 0);

        return parcel;
    }
}
