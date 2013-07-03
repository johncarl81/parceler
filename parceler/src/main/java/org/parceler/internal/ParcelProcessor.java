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
import org.androidtransfuse.TransfuseAnalysisException;
import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.transaction.TransactionProcessor;
import org.androidtransfuse.transaction.TransactionProcessorPool;

import javax.inject.Provider;
import java.util.Collection;

/**
 * @author John Ericksen
 */
public class ParcelProcessor {

    private final TransactionProcessor processor;
    private final TransactionProcessorPool<Provider<ASTType>, JDefinedClass> parcelProcessor;
    private final ParcelTransactionFactory parcelTransactionFactory;

    public ParcelProcessor(TransactionProcessor processor,
                           TransactionProcessorPool<Provider<ASTType>, JDefinedClass> parcelProcessor,
                           ParcelTransactionFactory parcelTransactionFactory) {
        this.processor = processor;
        this.parcelProcessor = parcelProcessor;
        this.parcelTransactionFactory = parcelTransactionFactory;
    }

    public void submit(Collection<Provider<ASTType>> parcels) {
        for (Provider<ASTType> parcel : parcels) {
            parcelProcessor.submit(parcelTransactionFactory.buildTransaction(parcel));
        }
    }

    public void execute() {
        processor.execute();
    }

    public void checkForErrors() {
        if (!processor.isComplete()) {
            throw new TransfuseAnalysisException("@Parcel code generation did not complete successfully.", processor.getErrors());
        }
    }
}
