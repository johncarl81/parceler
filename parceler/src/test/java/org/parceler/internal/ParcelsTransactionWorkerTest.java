/**
 * Copyright 2013 John Ericksen
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
package org.parceler.internal;

import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

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
