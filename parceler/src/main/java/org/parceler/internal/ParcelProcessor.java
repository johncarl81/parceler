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
import org.androidtransfuse.transaction.ScopedTransactionBuilder;
import org.androidtransfuse.transaction.TransactionProcessor;
import org.androidtransfuse.transaction.TransactionProcessorPool;
import org.parceler.Parcel;
import org.parceler.ParcelClass;
import org.parceler.ParcelClasses;

import javax.inject.Provider;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Map;

/**
 * @author John Ericksen
 */
public class ParcelProcessor {

    private final TransactionProcessor processor;
    private final TransactionProcessorPool<Provider<ASTType>, Provider<ASTType>> externalParcelRepositoryProcessor;
    private final TransactionProcessorPool<Provider<ASTType>, Map<Provider<ASTType>, JDefinedClass>> externalParcelProcessor;
    private final TransactionProcessorPool<Provider<ASTType>, JDefinedClass> parcelProcessor;
    private final Provider<ExternalParcelRepositoryTransactionWorker> externalParcelRepositoryTransactionWorkerProvider;
    private final Provider<ExternalParcelTransactionWorker> externalParcelTransactionWorkerProvider;
    private final Provider<ParcelTransactionWorker> parcelTransactionWorkerProvider;
    private final ScopedTransactionBuilder scopedTransactionBuilder;

    public ParcelProcessor(TransactionProcessor processor,
                           TransactionProcessorPool<Provider<ASTType>, Provider<ASTType>> externalParcelRepositoryProcessor,
                           TransactionProcessorPool<Provider<ASTType>, Map<Provider<ASTType>, JDefinedClass>> externalParcelProcessor,
                           TransactionProcessorPool<Provider<ASTType>, JDefinedClass> parcelProcessor,
                           Provider<ExternalParcelRepositoryTransactionWorker> externalParcelRepositoryTransactionWorkerProvider,
                           Provider<ExternalParcelTransactionWorker> externalParcelTransactionWorkerProvider,
                           Provider<ParcelTransactionWorker> parcelTransactionWorkerProvider,
                           ScopedTransactionBuilder scopedTransactionBuilder) {
        this.processor = processor;
        this.externalParcelRepositoryProcessor = externalParcelRepositoryProcessor;
        this.externalParcelProcessor = externalParcelProcessor;
        this.parcelProcessor = parcelProcessor;
        this.externalParcelRepositoryTransactionWorkerProvider = externalParcelRepositoryTransactionWorkerProvider;
        this.externalParcelTransactionWorkerProvider = externalParcelTransactionWorkerProvider;
        this.parcelTransactionWorkerProvider = parcelTransactionWorkerProvider;
        this.scopedTransactionBuilder = scopedTransactionBuilder;
    }

    public void submit(Class<? extends Annotation> annotation, Collection<Provider<ASTType>> parcelProviders) {
        for (Provider<ASTType> parcelProvider : parcelProviders) {
            if(annotation == ParcelClass.class || annotation == ParcelClasses.class){
                externalParcelRepositoryProcessor.submit(scopedTransactionBuilder.build(parcelProvider, externalParcelRepositoryTransactionWorkerProvider));
                externalParcelProcessor.submit(scopedTransactionBuilder.build(parcelProvider, externalParcelTransactionWorkerProvider));
            }
            if(annotation == Parcel.class){
                parcelProcessor.submit(scopedTransactionBuilder.build(parcelProvider, parcelTransactionWorkerProvider));
            }
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