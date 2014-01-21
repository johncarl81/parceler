package org.parceler;

import org.apache.commons.lang.RandomStringUtils;
import uk.co.jemos.podam.api.AttributeStrategy;
import uk.co.jemos.podam.exceptions.PodamMockeryException;

/**
 * @author John Ericksen
 */
public class SubParcelStrategy implements AttributeStrategy<SubParcel> {
    @Override
    public SubParcel getValue() throws PodamMockeryException {
        return new SubParcel(RandomStringUtils.random(100));
    }
}
