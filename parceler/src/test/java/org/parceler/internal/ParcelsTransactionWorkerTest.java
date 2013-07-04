package org.parceler.internal;

import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author John Ericksen
 */
public class ParcelsTransactionWorkerTest {

    private ParcelsTransactionWorker parcelsTransaction;
    private ParcelsGenerator mockGenerator;
    private Map input;

    @Before
    public void setup() {
        mockGenerator = mock(ParcelsGenerator.class);
        input = mock(Map.class);
        parcelsTransaction = new ParcelsTransactionWorker(mockGenerator);
    }

    @Test
    public void testRun() {

        assertFalse(parcelsTransaction.isComplete());

        assertNull(parcelsTransaction.run(input));

        assertTrue(parcelsTransaction.isComplete());
        verify(mockGenerator).generate(input);
    }
}
