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

import android.util.SparseArray;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.*;

import static org.junit.Assert.*;

/**
 * @author John Ericksen
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE)
public class NonParcelTest {

    @Test
    public void testUnmodifiableList(){
        List<SubParcel> input = new ArrayList<SubParcel>();

        input.add(new SubParcel("name"));
        input.add(null);

        List<SubParcel> output = Parcels.unwrap(ParcelsTestUtil.wrap(List.class, Collections.unmodifiableList(input)));
        assertEquals(2, output.size());

        assertEquals("name", output.get(0).getName());
        assertNull(output.get(1));
    }

    @Test
    public void testSublist(){
        List<SubParcel> input = new ArrayList<SubParcel>();

        input.add(new SubParcel("one"));
        input.add(new SubParcel("two"));
        input.add(new SubParcel("three"));
        input.add(null);

        List<SubParcel> output = Parcels.unwrap(ParcelsTestUtil.wrap(List.class, input.subList(0,2)));
        assertEquals(2, output.size());

        assertEquals("one", output.get(0).getName());
        assertEquals("two", output.get(1).getName());
    }

    @Test
    public void testList(){
        List<SubParcel> input = new ArrayList<SubParcel>();

        input.add(new SubParcel("name"));
        input.add(null);

        List<SubParcel> output = Parcels.unwrap(ParcelsTestUtil.wrap(input));
        assertEquals(2, output.size());

        assertEquals("name", output.get(0).getName());
        assertNull(output.get(1));
    }

    @Test
    public void testSet(){

        Set<SubParcel> input = new HashSet<SubParcel>();

        SubParcel subParcel = new SubParcel("name");
        input.add(subParcel);
        input.add(null);

        Set<SubParcel> output = Parcels.unwrap(ParcelsTestUtil.wrap(input));
        assertEquals(2, output.size());

        assertTrue(output.contains(subParcel));
        assertTrue(output.contains(null));
    }

    @Test
    public void testMap(){

        Map<SubParcel, SubParcel> input = new HashMap<SubParcel, SubParcel>();

        SubParcel key1 = new SubParcel("key");
        SubParcel key2 = new SubParcel("key2");
        input.put(key1, new SubParcel("name"));
        input.put(null, new SubParcel("null"));
        input.put(key2, null);

        Map<SubParcel, SubParcel> output = Parcels.unwrap(ParcelsTestUtil.wrap(input));
        assertEquals(3, output.size());

        assertEquals("name", output.get(key1).getName());
        assertEquals("null", output.get(null).getName());
        assertNull(output.get(key2));
    }

    @Test
    public void testPrimitiveKeyMap(){

        Map<Integer, SubParcel> input = new HashMap<Integer, SubParcel>();

        Integer key1 = 1;
        Integer key2 = 2;
        input.put(key1, new SubParcel("name"));
        input.put(null, new SubParcel("null"));
        input.put(key2, null);

        Map<Integer, SubParcel> output = Parcels.unwrap(ParcelsTestUtil.wrap(input));
        assertEquals(3, output.size());

        assertEquals("name", output.get(key1).getName());
        assertEquals("null", output.get(null).getName());
        assertNull(output.get(key2));
    }

    @Test
    public void testPrimitiveValueMap(){

        Map<SubParcel, Integer> input = new HashMap<SubParcel, Integer>();

        Integer value = 42;
        Integer value2 = 43;
        SubParcel subParcel = new SubParcel("name");
        SubParcel nullParcel = new SubParcel("null");
        input.put(subParcel, value);
        input.put(nullParcel, null);
        input.put(null, value2);

        Map<SubParcel, Integer> output = Parcels.unwrap(ParcelsTestUtil.wrap(input));
        assertEquals(3, output.size());

        assertEquals(value, output.get(subParcel));
        assertEquals(value2, output.get(null));
        assertNull(output.get(nullParcel));
    }


    @Test
    public void testSparseArray(){

        SparseArray<SubParcel> input = new SparseArray<SubParcel>();

        input.append(1, new SubParcel("name"));
        input.append(2, null);

        SparseArray<SubParcel> exampleSparseArray = Parcels.unwrap(ParcelsTestUtil.wrap(input));
        assertEquals(2, exampleSparseArray.size());

        SubParcel output = exampleSparseArray.get(1);
        assertEquals("name", output.getName());
        assertNull(exampleSparseArray.get(2));
    }

    @Test
    public void testInteger(){

        Integer integerOuput = Parcels.unwrap(ParcelsTestUtil.wrap(42));
        Integer integerNullOuput = Parcels.unwrap(ParcelsTestUtil.wrap(null));
        assertEquals(Integer.valueOf(42), integerOuput);
        assertNull(integerNullOuput);
    }

    @Test
    public void testLong(){

        Long longOuput = Parcels.unwrap(ParcelsTestUtil.wrap(42L));
        Long longNullOuput = Parcels.unwrap(ParcelsTestUtil.wrap(null));
        assertEquals(Long.valueOf(42L), longOuput);
        assertNull(longNullOuput);
    }

    @Test
    public void testDouble(){

        Double doubleOutput = Parcels.unwrap(ParcelsTestUtil.wrap(42.42));
        Double doubleNullOuput = Parcels.unwrap(ParcelsTestUtil.wrap(null));
        assertEquals(Double.valueOf(42.42), doubleOutput);
        assertNull(doubleNullOuput);
    }

    @Test
    public void testByte(){

        Byte byteOutput = Parcels.unwrap(ParcelsTestUtil.wrap((byte)0x42));
        Byte byteNullOuput = Parcels.unwrap(ParcelsTestUtil.wrap(null));
        assertEquals(Byte.valueOf((byte)0x42), byteOutput);
        assertNull(byteNullOuput);
    }

    @Test
    public void testFloat(){

        Float floatOutput = Parcels.unwrap(ParcelsTestUtil.wrap(42.42F));
        Float floatNullOutput = Parcels.unwrap(ParcelsTestUtil.wrap(null));
        assertEquals(Float.valueOf(42.42F), floatOutput);
        assertNull(floatNullOutput);
    }

    @Test
    public void testString(){

        String stringOutput = Parcels.unwrap(ParcelsTestUtil.wrap("42"));
        String stringNullOutput = Parcels.unwrap(ParcelsTestUtil.wrap(null));
        assertEquals("42", stringOutput);
        assertNull(stringNullOutput);
    }
}
