/**
 * Copyright 2011-2015 John Ericksen
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
import org.androidtransfuse.adapter.ASTAnnotation;
import org.androidtransfuse.adapter.ASTType;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Provider;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
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
    private ASTAnnotation mockASTAnnotation;
    private JDefinedClass output;

    @Before
    public void setup() {
        mockAnalysis = mock(ParcelableAnalysis.class);
        mockGenerator = mock(ParcelableGenerator.class);
        mockDescriptor = mock(ParcelableDescriptor.class);
        mockASTAnnotation = mock(ASTAnnotation.class);
        inputProvider = mock(Provider.class);
        input = mock(ASTType.class);
        output = mock(JDefinedClass.class);

        parcelTransaction = new ParcelTransactionWorker(mockAnalysis, mockGenerator);
    }

    @Test
    public void test() {
        when(inputProvider.get()).thenReturn(input);
        when(input.getASTAnnotation(any(Class.class))).thenReturn(mockASTAnnotation);
        when(mockAnalysis.analyze(input, mockASTAnnotation)).thenReturn(mockDescriptor);
        when(mockGenerator.generateParcelable(input, mockDescriptor)).thenReturn(output);

        assertFalse(parcelTransaction.isComplete());

        assertEquals(output, parcelTransaction.run(inputProvider).getDefinedClass());

        assertTrue(parcelTransaction.isComplete());
    }
}
