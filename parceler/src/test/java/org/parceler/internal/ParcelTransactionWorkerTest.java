package org.parceler.internal;

import com.sun.codemodel.JDefinedClass;
import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.adapter.classes.ASTClassFactory;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Provider;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author John Ericksen
 */
public class ParcelTransactionWorkerTest {

    private ParcelTransactionWorker parcelTransaction;
    private ParcelableAnalysis mockAnalysis;
    private ParcelableGenerator mockGenerator;
    private ParcelableDescriptor mockDescriptor;
    private Provider inputProvider;
    private ASTType input;
    private JDefinedClass output;

    @Before
    public void setup() {
        mockAnalysis = mock(ParcelableAnalysis.class);
        mockGenerator = mock(ParcelableGenerator.class);
        mockDescriptor = mock(ParcelableDescriptor.class);
        inputProvider = mock(Provider.class);
        input = mock(ASTType.class);
        output = mock(JDefinedClass.class);
        ASTClassFactory astClassFactory = mock(ASTClassFactory.class);

        parcelTransaction = new ParcelTransactionWorker(mockAnalysis, mockGenerator, astClassFactory);
    }

    @Test
    public void test() {
        when(inputProvider.get()).thenReturn(input);
        when(mockAnalysis.analyze(input, null)).thenReturn(mockDescriptor);
        when(mockGenerator.generateParcelable(input, mockDescriptor)).thenReturn(output);

        assertFalse(parcelTransaction.isComplete());

        assertEquals(output, parcelTransaction.run(inputProvider));

        assertTrue(parcelTransaction.isComplete());
    }
}
