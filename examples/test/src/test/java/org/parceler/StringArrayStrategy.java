package org.parceler;

import org.apache.commons.lang.RandomStringUtils;
import uk.co.jemos.podam.api.AttributeStrategy;
import uk.co.jemos.podam.exceptions.PodamMockeryException;

/**
 * @author John Ericksen
 */
public class StringArrayStrategy implements AttributeStrategy<String[]> {
    @Override
    public String[] getValue() throws PodamMockeryException {
        return new String[]{RandomStringUtils.random(10), RandomStringUtils.random(10), RandomStringUtils.random(10)};
    }
}
