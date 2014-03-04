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
