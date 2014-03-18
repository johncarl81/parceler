package org.parceler;

import uk.co.jemos.podam.api.AttributeStrategy;
import uk.co.jemos.podam.exceptions.PodamMockeryException;

/**
 * @author John Ericksen
 */
public class ExceptionStrategy implements AttributeStrategy<Exception> {
    @Override
    public Exception getValue() throws PodamMockeryException {
        return new NullPointerException("Test Exception");
    }
}
