/**
 * Copyright 2011-2015 John Ericksen
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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

    @Parcel
    static class Three {
        Four four;
        String name;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Three)) return false;
            Three three = (Three) o;
            return EqualsBuilder.reflectionEquals(this, three, new String[]{"four"});
        }

        @Override
        public int hashCode() {
            return HashCodeBuilder.reflectionHashCode(this, new String[]{"four"});
        }
    }

    @Parcel
    static class Four {
        Three three;
        String name;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Three)) return false;
            Four four = (Four) o;
            return EqualsBuilder.reflectionEquals(this, four, new String[]{"three"});
        }

        @Override
        public int hashCode() {
            return HashCodeBuilder.reflectionHashCode(this, new String[]{"three"});
        }
    }

    @Test
    public void testCircularInstances() {
        Three three = new Three();
        Four four = new Four();

        three.name = "3";
        four.name = "4";

        three.four = four;
        four.three = three;

        Three unwrap = Parcels.unwrap(ParcelsTestUtil.wrap(three));

        assertEquals(three, unwrap);
        assertEquals(four, three.four);
    }

    @Parcel
    static class Five {
        Six a;
        Six b;
        Six c;
        String name;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Five)) return false;
            Five five = (Five) o;
            return EqualsBuilder.reflectionEquals(this, five, new String[]{"a", "b", "c"});
        }

        @Override
        public int hashCode() {
            return HashCodeBuilder.reflectionHashCode(this, new String[]{"a", "b", "c"});
        }
    }

    @Parcel
    static class Six{
        String name;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Six)) return false;
            Six six = (Six) o;
            return EqualsBuilder.reflectionEquals(this, six);
        }

        @Override
        public int hashCode() {
            return HashCodeBuilder.reflectionHashCode(this);
        }
    }

    @Test
    public void testMultipleReferences() {
        Five five = new Five();
        five.name = "5";
        Six six = new Six();
        five.a = six;
        five.b = six;
        five.c = six;
        six.name = "6";

        Five unwrap = Parcels.unwrap(ParcelsTestUtil.wrap(five));

        assertEquals(five, unwrap);
        assertEquals(five.a, unwrap.a);
        assertEquals(five.b, unwrap.b);
        assertEquals(five.c, unwrap.c);
        assertEquals(unwrap.a, unwrap.b);
        assertEquals(unwrap.a, unwrap.c);
    }

    @Parcel
    static class Seven {
        Eight eight;
        String name;
        @ParcelConstructor Seven(Eight eight, String name){
            this.eight = eight;
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Seven)) return false;
            Seven seven = (Seven) o;
            return EqualsBuilder.reflectionEquals(this, seven);
        }

        @Override
        public int hashCode() {
            return HashCodeBuilder.reflectionHashCode(this);
        }
    }

    @Parcel
    static class Eight {
        Seven seven;
        String name;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Eight)) return false;
            Eight eight = (Eight) o;
            return EqualsBuilder.reflectionEquals(this, eight, new String[]{"seven"});
        }

        @Override
        public int hashCode() {
            return HashCodeBuilder.reflectionHashCode(this, new String[]{"seven"});
        }
    }

    @Test
    public void testCircularConstructor(){
        Eight eight = new Eight();
        eight.name = "8";
        Seven seven = new Seven(eight, "7");
        eight.seven = seven;

        Eight unwrappedEight = Parcels.unwrap(ParcelsTestUtil.wrap(eight));

        assertEquals(eight, unwrappedEight);
        assertEquals(seven, unwrappedEight.seven);
        assertEquals(eight, unwrappedEight.seven.eight);

        try {
            Parcels.unwrap(ParcelsTestUtil.wrap(seven));
            assertTrue("Parcels.unwrap did not throw an exception", false);
        }
        catch (ParcelerRuntimeException e){}
    }

    @Parcel
    static class Nine {
        Ten ten;
        String name;

        public Nine(Ten ten, String name) {
            this.ten = ten;
            this.name = name;
        }

        @ParcelFactory
        public static Nine build(Ten ten, String name){
            return new Nine(ten, name);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Nine)) return false;
            Nine nine = (Nine) o;
            return EqualsBuilder.reflectionEquals(this, nine, new String[]{"ten"});
        }

        @Override
        public int hashCode() {
            return HashCodeBuilder.reflectionHashCode(this, new String[]{"ten"});
        }
    }

    @Parcel
    static class Ten {
        Nine nine;
        String name;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Ten)) return false;

            Ten ten = (Ten) o;

            return EqualsBuilder.reflectionEquals(this, ten, new String[]{"nine"});
        }

        @Override
        public int hashCode() {
            return HashCodeBuilder.reflectionHashCode(this, new String[]{"nine"});
        }
    }

    @Test
    public void testCircularFactory(){
        Ten ten = new Ten();
        ten.name = "10";
        Nine nine = Nine.build(ten, "9");
        ten.nine = nine;

        Ten unwrappedTen = Parcels.unwrap(ParcelsTestUtil.wrap(ten));

        assertEquals(ten, unwrappedTen);
        assertEquals(ten, unwrappedTen.nine.ten);
        assertEquals(nine, unwrappedTen.nine);

        try {
            Parcels.unwrap(ParcelsTestUtil.wrap(nine));
            assertTrue("Parcels.unwrap did not throw an exception", false);
        }
        catch (ParcelerRuntimeException e){}
    }
}
