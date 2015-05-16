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
import org.parceler.converter.ArrayListParcelConverter;
import org.parceler.converter.HashMapParcelConverter;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author John Ericksen
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE)
public class TypeRangeConveterTest {

    @Parcel
    public static class A extends B {
        @ParcelConstructor public A(String value) {
            super(value);
        }
    }
    @Parcel
    public static class B extends C {
        @ParcelConstructor public B(String value) {
            super(value);
        }
    }
    @Parcel
    public static class C {
        String value;

        @ParcelConstructor
        public C(String value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;

            if (!(o instanceof A)) return false;

            A a = (A) o;

            return new EqualsBuilder()
                    .append(value, a.value)
                    .isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 37)
                    .append(value)
                    .toHashCode();
        }}

    @Parcel
    public static class Container {
        @ParcelPropertyConverter(ItemListParcelConverter.class)
        List<A> aList;

        @ParcelPropertyConverter(ItemListParcelConverter.class)
        Collection<A> aCollection;

        @ParcelPropertyConverter(ItemListParcelConverter.class)
        ArrayList<A> aArrayList;

        @ParcelPropertyConverter(ItemMapParcelConverter.class)
        Map<A, A> aMap;

        @ParcelPropertyConverter(ItemMapParcelConverter.class)
        HashMap<A, A> aHashMap;

        @ParcelPropertyConverter(AConverter.class)
        A a;

        @ParcelPropertyConverter(AConverter.class)
        B b;
    }

    @Test
    public void testTypeRanges(){

        Container container = new Container();
        A one = new A("one");
        A two = new A("two");
        A three = new A("three");
        A four = new A("four");
        A five = new A("five");
        A six = new A("six");
        A seven = new A("seven");
        A eight = new A("eight");
        A nine = new A("nine");

        container.aList = Collections.singletonList(one);
        container.aCollection = Collections.singletonList(two);
        container.aArrayList = new ArrayList<A>(Collections.singletonList(three));
        container.aMap = Collections.singletonMap(four, five);
        container.aHashMap = new HashMap<A, A>(Collections.singletonMap(six, seven));
        container.a = eight;
        container.b = nine;

        Container output = Parcels.unwrap(ParcelsTestUtil.wrap(container));

        assertNotNull(output);
        assertEquals(one, output.aList.get(0));
        assertEquals(two, output.aCollection.iterator().next());
        assertEquals(three, output.aArrayList.get(0));
        assertEquals(four, output.aMap.keySet().iterator().next());
        assertEquals(five, output.aMap.values().iterator().next());
        assertEquals(six, output.aHashMap.keySet().iterator().next());
        assertEquals(seven, output.aHashMap.values().iterator().next());
        assertEquals(eight, output.a);
        assertEquals(nine, output.b);
    }

    public static class ItemListParcelConverter extends ArrayListParcelConverter<A> {

        public void itemToParcel(A input, android.os.Parcel parcel) {
            parcel.writeParcelable(Parcels.wrap(input), 0);
        }

        public A itemFromParcel(android.os.Parcel parcel) {
            return Parcels.unwrap(parcel.readParcelable(A.class.getClassLoader()));
        }
    }

    public static class ItemMapParcelConverter extends HashMapParcelConverter<A, A> {

        @Override
        public void mapKeyToParcel(A key, android.os.Parcel parcel) {
            parcel.writeParcelable(Parcels.wrap(key), 0);
        }

        @Override
        public void mapValueToParcel(A value, android.os.Parcel parcel) {
            parcel.writeParcelable(Parcels.wrap(value), 0);
        }

        @Override
        public A mapKeyFromParcel(android.os.Parcel parcel) {
            return Parcels.unwrap(parcel.readParcelable(A.class.getClassLoader()));
        }

        @Override
        public A mapValueFromParcel(android.os.Parcel parcel) {
            return Parcels.unwrap(parcel.readParcelable(A.class.getClassLoader()));
        }
    }

    public static class AConverter implements TypeRangeParcelConverter<B, A>{

        @Override
        public void toParcel(B input, android.os.Parcel parcel) {
            parcel.writeParcelable(Parcels.wrap(input), 0);
        }

        @Override
        public A fromParcel(android.os.Parcel parcel) {
            return Parcels.unwrap(parcel.readParcelable(A.class.getClassLoader()));
        }
    }


}
