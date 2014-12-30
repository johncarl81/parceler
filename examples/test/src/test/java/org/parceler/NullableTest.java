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
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.lang.reflect.Field;

import static org.junit.Assert.assertNull;

/**
 * @author John Ericksen
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE)
public class NullableTest{

    @Test
    public void testNullable() throws IllegalAccessException {
        ConverterTarget target = new ConverterTarget();

        ConverterTarget output = Parcels.unwrap(ParcelsTestUtil.wrap(target));

        for (Field field : ConverterTarget.class.getDeclaredFields()) {
            if(!field.getType().isPrimitive() && !field.getName().equals("FIELDS_EXCLUDED")){
                assertNull(field.get(output));
            }
        }
    }

}
