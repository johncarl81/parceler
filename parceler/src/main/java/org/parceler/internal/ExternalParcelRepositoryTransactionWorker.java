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

import org.androidtransfuse.adapter.ASTAnnotation;
import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.transaction.AbstractCompletionTransactionWorker;
import org.parceler.ParcelClass;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * Executes the analysis and generation of an annotated @Parcel class.
 *
 * @author John Ericksen
 */
public class ExternalParcelRepositoryTransactionWorker extends AbstractCompletionTransactionWorker<Provider<ASTType>, Provider<ASTType>> {

    private final ExternalParcelRepository repository;

    @Inject
    public ExternalParcelRepositoryTransactionWorker(ExternalParcelRepository repository) {
        this.repository = repository;
    }

    @Override
    public Provider<ASTType> innerRun(Provider<ASTType> valueProvider) {

        ASTType value = valueProvider.get();

        ASTAnnotation astAnnotation = value.getASTAnnotation(ParcelClass.class);
        ASTType[] parcelTypes = astAnnotation.getProperty("value", ASTType[].class);

        for (ASTType parcelType : parcelTypes) {
            repository.add(parcelType);
        }

        return valueProvider;
    }
}
