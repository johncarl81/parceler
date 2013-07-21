package org.parceler.internal;

import com.google.common.collect.ImmutableSet;
import org.androidtransfuse.TransfuseAnalysisException;
import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.transaction.Transaction;
import org.androidtransfuse.transaction.TransactionProcessor;
import org.androidtransfuse.transaction.TransactionProcessorPool;
import org.junit.Before;
import org.junit.Test;
import org.parceler.Parcel;

import javax.inject.Provider;
import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * @author John Ericksen
 */
public class ParcelProcessorTest {

    private ParcelProcessor parcelProcessor;
    private TransactionProcessor mockGlobalProcessor;
    private TransactionProcessorPool mockExternalParcelRepositoryProcessor;
    private TransactionProcessorPool mockSubmitProcessor;
    private ParcelTransactionFactory mockTransactionFactory;
    private Provider<ASTType> input;
    private Transaction mockTransaction;

    @Before
    public void setup() {
        TransactionProcessor processor = mock(TransactionProcessor.class);
        mockExternalParcelRepositoryProcessor = mock(TransactionProcessorPool.class);
        mockGlobalProcessor = mock(TransactionProcessorPool.class);
        mockSubmitProcessor = mock(TransactionProcessorPool.class);
        ExternalParcelRepositoryTransactionFactory externalParcelRepositoryTransactionFactory = mock(ExternalParcelRepositoryTransactionFactory.class);
        TransactionProcessorPool mockExternalParcelProcessor = mock(TransactionProcessorPool.class);
        mockTransactionFactory = mock(ParcelTransactionFactory.class);
        ExternalParcelTransactionFactory mockExternalTransactionFactory = mock(ExternalParcelTransactionFactory.class);
        input = mock(Provider.class);
        mockTransaction = mock(Transaction.class);

        parcelProcessor = new ParcelProcessor(mockGlobalProcessor, mockExternalParcelRepositoryProcessor, mockExternalParcelProcessor, mockSubmitProcessor, externalParcelRepositoryTransactionFactory, mockExternalTransactionFactory, mockTransactionFactory);
    }

    @Test
    public void testSubmit() {

        when(mockTransactionFactory.buildTransaction(input)).thenReturn(mockTransaction);

        parcelProcessor.submit(Parcel.class, Collections.singleton(input));

        verify(mockSubmitProcessor).submit(mockTransaction);
    }

    @Test
    public void testExecute() {

        parcelProcessor.execute();

        verify(mockGlobalProcessor).execute();
    }

    @Test
    public void testCheckForErrorsFailing() {
        testCheckForErrors(true);
    }

    @Test
    public void testCheckForErrorsPassing() {
        testCheckForErrors(false);
    }

    private void testCheckForErrors(boolean errored) {
        try {
            when(mockGlobalProcessor.isComplete()).thenReturn(!errored);
            when(mockGlobalProcessor.getErrors()).thenReturn(ImmutableSet.of(new Exception()));

            parcelProcessor.checkForErrors();

            // Should not get this far if errored.
            assertFalse(errored);
        } catch (TransfuseAnalysisException exception) {
            // Exception should be thrown if errored.
            assertTrue(errored);
        }
    }
}
