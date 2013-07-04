package org.parceler;

import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.assertNotNull;

/**
 * @author John Ericksen
 */
public class ParcelWrapperTest {

    @Test
    public void verifyMethodNames() throws NoSuchMethodException {

        Method getWrappedMethod = ParcelWrapper.class.getMethod(ParcelWrapper.GET_PARCEL);
        assertNotNull(getWrappedMethod);
    }
}
