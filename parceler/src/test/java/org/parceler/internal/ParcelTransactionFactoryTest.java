package org.parceler.internal;

import com.sun.codemodel.JDefinedClass;
import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.transaction.CodeGenerationScopedTransactionWorker;
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
        Provider<CodeGenerationScopedTransactionWorker<Provider<ASTType>, JDefinedClass>> workerProvider = mock(Provider.class);
        factory = new ParcelTransactionFactory(scopedTransactionFactory, workerProvider);
    }

    @Test
    public void testBuild() {
        assertNotNull(factory.buildTransaction(input));
    }
}