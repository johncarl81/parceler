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
