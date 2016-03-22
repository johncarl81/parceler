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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
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
        enums.enumArray = new Enums.Values[]{A, C};

        Enums unwrapped = Parcels.unwrap(ParcelsTestUtil.wrap(enums));


        assertEquals(A, unwrapped.one);
        assertNull(unwrapped.two);
        assertEquals(B, unwrapped.three);
        assertArrayEquals(enums.enumArray, unwrapped.enumArray);
    }
}
