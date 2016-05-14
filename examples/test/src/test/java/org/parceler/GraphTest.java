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
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author John Ericksen
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE)
public class GraphTest {

    private static final int DEPTH = 10;
    private static final Random RAND = new Random(1);
    private static final AtomicLong SEQUENCE = new AtomicLong();

    private List<A> as;
    private List<B> bs;
    private List<C> cs;
    private List<D> ds;

    @Before
    public void setup() {
        as = new ArrayList<A>();
        bs = new ArrayList<B>();
        cs = new ArrayList<C>();
        ds = new ArrayList<D>();

        as.add(null);
        bs.add(null);
        cs.add(null);
        ds.add(null);
    }

    @Parcel
    public static class A {
        B b;
        C c;
    }

    @Parcel
    public static class B {
        A a;
        List<C> c = new ArrayList<C>();
    }

    @Parcel
    public static class C {
        String name;
        D d;
    }

    @Parcel
    public static class D {
        A a;
    }

    @Test
    public void testGraph() {
        for(int i = 0; i < 100; i++) {
            A a = generateRandomA(0);
            A unwrap = Parcels.unwrap(ParcelsTestUtil.wrap(a));
        }
    }

    private A generateRandomA(int depth) {
        if(RAND.nextInt(3) > 1 || depth > DEPTH){
            return as.get(RAND.nextInt(as.size()));
        }
        A a = new A();
        as.add(a);

        a.b = generateRandomB(depth + 1);
        a.c = generateRandomC(depth + 1);


        return a;
    }

    private B generateRandomB(int depth) {
        if(RAND.nextInt(3) > 1 || depth > DEPTH){
            return bs.get(RAND.nextInt(bs.size()));
        }

        B b = new B();
        bs.add(b);

        b.a = generateRandomA(depth + 1);
        for(int i = 0; i < RAND.nextInt(100); i++){
            b.c.add(generateRandomC(depth + 1));
        }

        return b;
    }

    private C generateRandomC(int depth) {
        if(RAND.nextInt(3) > 1 || depth > DEPTH){
            return cs.get(RAND.nextInt(cs.size()));
        }


        C c = new C();
        cs.add(c);

        c.d = generateRandomD(depth + 1);
        c.name = "C" + SEQUENCE.get();

        return c;
    }

    private D generateRandomD(int depth) {
        if(RAND.nextInt(3) > 1 || depth > DEPTH){
            return ds.get(RAND.nextInt(ds.size()));
        }

        D d = new D();
        ds.add(d);

        d.a = generateRandomA(depth + 1);

        return d;
    }
}
