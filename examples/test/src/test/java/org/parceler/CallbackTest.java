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

import android.os.Parcelable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author John Ericksen
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE)
public class CallbackTest {

    @Parcel
    public static class ReferencedClass {
        boolean wrapCallbackCalled = false;
        boolean unwrapCallbackCalled = false;

        @OnWrap
        public void wrap(){
            wrapCallbackCalled = true;
        }

        @OnUnwrap
        public void unwrap(){
            unwrapCallbackCalled = true;
        }
    }

    public static class BaseClass {
        boolean baseWrapCallbackCalled = false;
        boolean baseUnwrapCallbackCalled = false;

        @OnWrap
        public void baseWrap(){
            baseWrapCallbackCalled = true;
        }

        @OnUnwrap
        public void baseUnwrap(){
            baseUnwrapCallbackCalled = true;
        }
    }

    @Parcel
    public static class CallbackExample extends BaseClass {
        boolean wrapCallbackCalled = false;
        boolean unwrapCallbackCalled = false;
        ReferencedClass referenced = new ReferencedClass();

        @OnWrap
        public void wrap(){
            wrapCallbackCalled = true;
        }

        @OnUnwrap
        public void unwrap(){
            unwrapCallbackCalled = true;
        }
    }

    @Test
    public void testCallbacks() {
        CallbackExample example = new CallbackExample();

        assertFalse(example.wrapCallbackCalled);
        assertFalse(example.unwrapCallbackCalled);
        assertFalse(example.baseWrapCallbackCalled);
        assertFalse(example.baseUnwrapCallbackCalled);
        assertFalse(example.referenced.wrapCallbackCalled);
        assertFalse(example.referenced.unwrapCallbackCalled);

        Parcelable wrappedExample = ParcelsTestUtil.wrap(example);

        assertTrue(example.wrapCallbackCalled);
        assertFalse(example.unwrapCallbackCalled);
        assertTrue(example.baseWrapCallbackCalled);
        assertFalse(example.baseUnwrapCallbackCalled);
        assertTrue(example.referenced.wrapCallbackCalled);
        assertFalse(example.referenced.unwrapCallbackCalled);

        CallbackExample output = Parcels.unwrap(wrappedExample);

        assertTrue(output.wrapCallbackCalled);
        assertTrue(output.unwrapCallbackCalled);
        assertTrue(output.baseWrapCallbackCalled);
        assertTrue(output.baseUnwrapCallbackCalled);
        assertTrue(output.referenced.wrapCallbackCalled);
        assertTrue(output.referenced.unwrapCallbackCalled);
    }
}
