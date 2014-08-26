package org.parceler;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
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
public class CircularReferenceTest {

    @Parcel
    public static class One {
        Two two;
        int four;

        @ParcelConstructor
        public One(int four, Two two) {
            this.two = two;
            this.four = four;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof One)) return false;

            One one = (One) o;

            return EqualsBuilder.reflectionEquals(this, one);
        }

        @Override
        public int hashCode() {
            return HashCodeBuilder.reflectionHashCode(this);
        }
    }

    @Parcel
    public static class Two {
        One one;
        String three;

        @ParcelConstructor
        public Two(String three, One one) {
            this.one = one;
            this.three = three;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Two)) return false;

            Two two = (Two) o;

            return EqualsBuilder.reflectionEquals(this, two);
        }

        @Override
        public int hashCode() {
            return HashCodeBuilder.reflectionHashCode(this);
        }
    }

    @Test
    public void testCircularReferences() {
        One target = new One(1,
                new Two("two",
                        new One(3,
                                new Two("four",
                                        new One(5,
                                                new Two("six", null))))));

        One unwrap = Parcels.unwrap(ParcelsTestUtil.wrap(target));

        assertEquals(target, unwrap);
    }
}
