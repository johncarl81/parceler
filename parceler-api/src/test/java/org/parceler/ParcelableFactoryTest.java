package org.parceler;

import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.assertNotNull;

/**
 * @author John Ericksen
 */
public class ParcelableFactoryTest {

    @Test
    public void testMethodName() throws NoSuchMethodException {
        Method buildParcelableMethod = Parcels.ParcelableFactory.class.getMethod(Parcels.ParcelableFactory.BUILD_PARCELABLE, Object.class);
        assertNotNull(buildParcelableMethod);
    }
}