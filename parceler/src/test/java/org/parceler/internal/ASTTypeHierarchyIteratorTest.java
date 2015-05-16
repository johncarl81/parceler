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
package org.parceler.internal;

import com.google.common.collect.ImmutableSet;
import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.adapter.classes.ASTClassFactory;
import org.androidtransfuse.bootstrap.Bootstrap;
import org.androidtransfuse.bootstrap.Bootstraps;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.util.Iterator;

import static org.junit.Assert.*;

@Bootstrap
public class ASTTypeHierarchyIteratorTest {

    static class A extends B{}
    static class B extends C{}
    static class C {}

    private ASTType a;
    private ASTType b;
    private ASTType c;
    private ASTType object;

    @Inject
    ASTClassFactory astClassFactory;

    @Before
    public void setup(){
        Bootstraps.inject(this);
        a = astClassFactory.getType(A.class);
        b = astClassFactory.getType(B.class);
        c = astClassFactory.getType(C.class);
        object = astClassFactory.getType(Object.class);
    }

    @Test
    public void testRegularIterator(){
        Iterator<ASTType> iterator = new ASTTypeHierarchyIterator(a, ImmutableSet.<ASTType>of());

        assertTrue(iterator.hasNext());
        assertEquals(a, iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(b, iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(c, iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(object, iterator.next());
        assertFalse(iterator.hasNext());
        assertEquals(null, iterator.next());
    }

    @Test
    public void testSkipIterator(){
        Iterator<ASTType> iterator = new ASTTypeHierarchyIterator(a, ImmutableSet.<ASTType>of(b, c));

        assertTrue(iterator.hasNext());
        assertEquals(b, iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(c, iterator.next());
        assertFalse(iterator.hasNext());
        assertEquals(null, iterator.next());
    }

    @Test
    public void testSkipIncludingObjectIterator(){
        Iterator<ASTType> iterator = new ASTTypeHierarchyIterator(a, ImmutableSet.<ASTType>of(c, object));

        assertTrue(iterator.hasNext());
        assertEquals(c, iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(object, iterator.next());
        assertFalse(iterator.hasNext());
        assertEquals(null, iterator.next());
    }
}