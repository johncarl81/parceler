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
}
