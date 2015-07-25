package org.parceler;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.parceler.Enums.Values.*;

/**
 * @author John Ericksen
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE)
public class EnumsTest {

    @Test
    public void testEnumsColleciton(){

        List<Enums.Values> values = new ArrayList<Enums.Values>();

        values.add(A);
        values.add(C);
        values.add(null);

        List<Enums.Values> unwrapped = Parcels.unwrap(ParcelsTestUtil.wrap(values));

        assertEquals(3, unwrapped.size());
        assertEquals(A, unwrapped.get(0));
        assertEquals(C, unwrapped.get(1));
        assertNull(unwrapped.get(2));
    }

    @Test
    public void testSubEnums(){

        Enums enums = new Enums();

        enums.one = A;
        enums.two = null;
        enums.three = B;

        Enums unwrapped = Parcels.unwrap(ParcelsTestUtil.wrap(enums));


        assertEquals(A, unwrapped.one);
        assertNull(unwrapped.two);
        assertEquals(B, unwrapped.three);
    }
}
