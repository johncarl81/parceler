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
import org.androidtransfuse.transaction.AbstractCompletionTransactionWorker;
import org.parceler.Parcel;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * Executes the analysis and generation of an annotated @Parcel class.
 *
 * @author John Ericksen
 */
public class ParcelTransactionWorker extends AbstractCompletionTransactionWorker<Provider<ASTType>, ParcelImplementations> {

    private final ParcelableAnalysis parcelableAnalysis;
    private final ParcelableGenerator parcelableGenerator;

    @Inject
    public ParcelTransactionWorker(ParcelableAnalysis parcelableAnalysis, ParcelableGenerator parcelableGenerator) {
        this.parcelableAnalysis = parcelableAnalysis;
        this.parcelableGenerator = parcelableGenerator;
    }

    @Override
    public ParcelImplementations innerRun(Provider<ASTType> valueProvider) {

        ASTType value = valueProvider.get();
        ASTAnnotation parcelASTAnnotation = value.getASTAnnotation(Parcel.class);

        ParcelableDescriptor analysis = parcelableAnalysis.analyze(value, parcelASTAnnotation);

        if(analysis != null) {
            JDefinedClass definedClass = parcelableGenerator.generateParcelable(value, analysis);

            return new ParcelImplementations(definedClass, analysis.getExtraImplementations(), analysis.isParcelsIndex());
        }
        return null;
    }
}
