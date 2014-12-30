/**
 * Copyright 2013-2015 John Ericksen
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

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author John Ericksen
 */
public class ABTest {

    @ParcelClasses({
            @ParcelClass(A.class),
            @ParcelClass(B.class)
    })
    public static class A {
    }

    public static class B {
        List<A> a;
    }

    @Test
    public void testList(){
        B b = new B();
        A a = new A();
        b.a = Collections.singletonList(a);

        B output = Parcels.unwrap(new ABTest$B$$Parcelable(b));
        assertEquals(1, output.a.size());
    }
}
