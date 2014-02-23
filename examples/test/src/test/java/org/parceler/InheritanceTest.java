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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author John Ericksen
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE)
public class InheritanceTest {

    @Test
    public void testFieldModifiers(){

        FieldSubClass prewrap = new FieldSubClass();

        prewrap.setOne("1a");
        prewrap.setSuperOne("1b");
        prewrap.setTwo("2a");
        prewrap.setSuperTwo("2b");
        prewrap.setThree("3a");
        prewrap.setSuperThree("3b");
        prewrap.setFour("4a");
        prewrap.setSuperFour("4b");
        prewrap.setFinal("9");

        FieldSubClass unwrapped = Parcels.unwrap(ParcelsTestUtil.wrap(prewrap));

        assertEquals("1a", unwrapped.getOne());
        assertEquals("1b", unwrapped.getSuperOne());
        assertEquals("2a", unwrapped.getTwo());
        assertEquals("2b", unwrapped.getSuperTwo());
        assertEquals("3a", unwrapped.getThree());
        assertEquals("3b", unwrapped.getSuperThree());
        assertEquals("4a", unwrapped.getFour());
        assertEquals("4b", unwrapped.getSuperFour());
        assertEquals("9", unwrapped.getFinal());
    }

    @Test
    public void testMethodModifiers(){

        MethodSubClass prewrap = new MethodSubClass();

        prewrap.setSubOne("1a");
        prewrap.setSuperOne("1b");
        prewrap.setSubTwo("2a");
        prewrap.setSuperTwo("2b");
        prewrap.setSubThree("3a");
        prewrap.setSuperThree("3b");
        prewrap.setSubFour("4a");
        prewrap.setSuperFour("4b");
        prewrap.setFinal("9");

        MethodSubClass unwrapped = Parcels.unwrap(ParcelsTestUtil.wrap(prewrap));

        assertEquals("1a", unwrapped.getSubOne());
        assertNull(unwrapped.getSuperOne());
        assertNull(unwrapped.getSubTwo());
        assertNull(unwrapped.getSuperTwo());
        assertNull(unwrapped.getSubThree());
        assertNull(unwrapped.getSuperThree());
        assertNull(unwrapped.getSubFour());
        assertNull(unwrapped.getSuperFour());
        assertEquals("9", unwrapped.getFinal());
    }
}
