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

import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.transaction.ScopedTransactionBuilder;
import org.androidtransfuse.transaction.TransactionProcessor;
import org.androidtransfuse.transaction.TransactionProcessorPool;
import org.androidtransfuse.util.Logger;
import org.parceler.Parcel;
import org.parceler.ParcelClass;
import org.parceler.ParcelClasses;

import javax.inject.Provider;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @author John Ericksen
 */
public class ParcelProcessor {

    private final TransactionProcessor processor;
    private final TransactionProcessorPool<Provider<ASTType>, Provider<ASTType>> externalParcelRepositoryProcessor;
    private final TransactionProcessorPool<Provider<ASTType>, Map<Provider<ASTType>, ParcelImplementations>> externalParcelProcessor;
    private final TransactionProcessorPool<Provider<ASTType>, ParcelImplementations> parcelProcessor;
    private final Provider<ExternalParcelRepositoryTransactionWorker> externalParcelRepositoryTransactionWorkerProvider;
    private final Provider<ExternalParcelTransactionWorker> externalParcelTransactionWorkerProvider;
    private final Provider<ParcelTransactionWorker> parcelTransactionWorkerProvider;
    private final ScopedTransactionBuilder scopedTransactionBuilder;
    private final Logger logger;
    private final boolean stacktrace;

    public ParcelProcessor(TransactionProcessor processor,
                           TransactionProcessorPool<Provider<ASTType>, Provider<ASTType>> externalParcelRepositoryProcessor,
                           TransactionProcessorPool<Provider<ASTType>, Map<Provider<ASTType>, ParcelImplementations>> externalParcelProcessor,
                           TransactionProcessorPool<Provider<ASTType>, ParcelImplementations> parcelProcessor,
                           Provider<ExternalParcelRepositoryTransactionWorker> externalParcelRepositoryTransactionWorkerProvider,
                           Provider<ExternalParcelTransactionWorker> externalParcelTransactionWorkerProvider,
                           Provider<ParcelTransactionWorker> parcelTransactionWorkerProvider,
                           ScopedTransactionBuilder scopedTransactionBuilder,
                           Logger logger,
                           boolean stacktrace) {
        this.processor = processor;
        this.externalParcelRepositoryProcessor = externalParcelRepositoryProcessor;
        this.externalParcelProcessor = externalParcelProcessor;
        this.parcelProcessor = parcelProcessor;
        this.externalParcelRepositoryTransactionWorkerProvider = externalParcelRepositoryTransactionWorkerProvider;
        this.externalParcelTransactionWorkerProvider = externalParcelTransactionWorkerProvider;
        this.parcelTransactionWorkerProvider = parcelTransactionWorkerProvider;
        this.scopedTransactionBuilder = scopedTransactionBuilder;
        this.logger = logger;
        this.stacktrace = stacktrace;
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

    public void logErrors() {
        if (!processor.isComplete()) {
            if (stacktrace) {
                for (Exception exception : (Set<Exception>) processor.getErrors()) {
                    logger.error("Code generation did not complete successfully.", exception);
                }
            }
            else{
                logger.error("Code generation did not complete successfully.  For more details add the compiler argument -AparcelerStacktrace");
            }
        }
    }
}