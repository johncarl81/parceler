package org.parceler;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;

/**
 * Created by john on 9/1/16.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE)
public class MismatchedTypeTest {

    @Parcel(Parcel.Serialization.BEAN)
    public static class Victim {

        private int value;

        @ParcelConstructor
        public Victim(Integer value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    @Test
    public void testMismatched() {
        Victim victim = new Victim(42);

        Victim output = Parcels.unwrap(ParcelsTestUtil.wrap(victim));

        assertEquals(42, output.getValue());
    }
}
