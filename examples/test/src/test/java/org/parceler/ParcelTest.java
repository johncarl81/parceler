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

        ExampleParcel parcel = ((ParcelWrapper<ExampleParcel>) parcelable).getParcel();

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

        Manual output = ((ParcelWrapper<Manual>)parcelable).getParcel();

        assertEquals(input.getValue(), output.getValue());
    }

    @Test
    public void testManuallyRegistered(){

        String value = "test";

        ExternalParcel input = new ExternalParcel();
        input.setValue(value);

        Parcelable parcelable = Parcels.wrap(input);

        ExternalParcel output = ((ParcelWrapper<ExternalParcel>)parcelable).getParcel();

        assertEquals(input.getValue(), output.getValue());
    }

    @Test
    public void testManuallyRegisteredSerialization(){

        String value = "test";

        ManuallyRegistered input = new ManuallyRegistered();
        input.setValue(value);

        Parcelable parcelable = Parcels.wrap(input);

        ManuallyRegistered output = ((ParcelWrapper<ManuallyRegistered>)parcelable).getParcel();

        assertEquals(input.getValue(), output.getValue());
    }

}
