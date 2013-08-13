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

import com.sun.codemodel.JDefinedClass;
import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.transaction.TransactionWorker;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Provider;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

/**
 * @author John Ericksen
 */
public class ParcelTransactionFactoryTest {

    private ParcelTransactionFactory factory;
    private Provider input;

    @Before
    public void setup() {
        input = mock(Provider.class);
        ThreadLocalScope simpleScope = new ThreadLocalScope();
        ScopedTransactionFactory scopedTransactionFactory = new ScopedTransactionFactory(simpleScope);
        Provider<TransactionWorker<Provider<ASTType>, JDefinedClass>> workerProvider = mock(Provider.class);
        factory = new ParcelTransactionFactory(scopedTransactionFactory, workerProvider);
    }

    @Test
    public void testBuild() {
        assertNotNull(factory.buildTransaction(input));
    }
}