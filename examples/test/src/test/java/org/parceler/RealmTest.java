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

import static org.junit.Assert.assertEquals;

/**
 * @author John Ericksen
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE)
public class RealmTest {

    public static class Base{}
    public static class StampProxy extends Stamp{}

    @Parcel(implementations = StampProxy.class,
        value = Parcel.Serialization.BEAN,
        analyze = Stamp.class)
    public static class Stamp extends Base {
        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    @Test
    public void testRealm() {
        Stamp stamp = new StampProxy();

        stamp.setId("123");

        Stamp unwrapped = Parcels.unwrap(ParcelsTestUtil.wrap(stamp));

        assertEquals(stamp.getId(), unwrapped.getId());
    }

}
