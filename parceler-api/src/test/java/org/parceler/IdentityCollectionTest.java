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

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by john on 7/11/16.
 */
public class IdentityCollectionTest {

    private IdentityCollection identityCollection;

    @Before
    public void setup() {
        identityCollection = new IdentityCollection();
    }

    @Test
    public void testWriteLifecycle(){
        assertEquals(-1, identityCollection.getKey("test"));
        int id = identityCollection.put("test");
        assertEquals("test", identityCollection.get(id));
        identityCollection.put(id, "test2");
        assertEquals(id, identityCollection.getKey("test2"));
    }

    @Test
    public void testReadLifecycle() {

        for(int id = 1; id < 3; id++) {
            String value = "test " + id;
            assertFalse(identityCollection.containsKey(id));
            identityCollection.put(id, value);
            assertTrue(identityCollection.containsKey(id));
            assertFalse(identityCollection.isReserved(id));
            assertEquals(value, identityCollection.get(id));
        }
    }

    @Test
    public void testReservation() {
        int reservation = identityCollection.reserve();
        assertTrue(identityCollection.containsKey(reservation));
        assertTrue(identityCollection.isReserved(reservation));
        identityCollection.put(reservation, "test reservation");
        assertTrue(identityCollection.containsKey(reservation));
        assertFalse(identityCollection.isReserved(reservation));
    }

}