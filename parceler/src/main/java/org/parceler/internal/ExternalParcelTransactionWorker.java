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
import org.androidtransfuse.adapter.ASTAnnotation;
import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.transaction.AbstractCompletionTransactionWorker;
import org.parceler.ParcelClass;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.HashMap;
import java.util.Map;

/**
 * Executes the analysis and generation of an annotated @Parcel class.
 *
 * @author John Ericksen
 */
public class ExternalParcelTransactionWorker extends AbstractCompletionTransactionWorker<Provider<ASTType>, Map<Provider<ASTType>, JDefinedClass>> {

    private final ParcelableAnalysis parcelableAnalysis;
    private final ParcelableGenerator parcelableGenerator;

    @Inject
    public ExternalParcelTransactionWorker(ParcelableAnalysis parcelableAnalysis, ParcelableGenerator parcelableGenerator) {
        this.parcelableAnalysis = parcelableAnalysis;
        this.parcelableGenerator = parcelableGenerator;
    }

    @Override
    public Map<Provider<ASTType>, JDefinedClass> innerRun(Provider<ASTType> valueProvider) {

        ASTType value = valueProvider.get();

        ASTAnnotation astAnnotation = value.getASTAnnotation(ParcelClass.class);
        ASTType[] parcelTypes = astAnnotation.getProperty("value", ASTType[].class);

        Map<Provider<ASTType>, JDefinedClass> generatedSource = new HashMap<Provider<ASTType>, JDefinedClass>();

        for (ASTType parcelType : parcelTypes) {
            ParcelableDescriptor analysis = parcelableAnalysis.analyze(parcelType);
            generatedSource.put(new ASTTypeProvider(parcelType), parcelableGenerator.generateParcelable(parcelType, analysis));
        }

        return generatedSource;
    }

    private static class ASTTypeProvider implements Provider<ASTType>{

        private ASTType parcelType;

        private ASTTypeProvider(ASTType parcelType) {
            this.parcelType = parcelType;
        }

        @Override
        public ASTType get() {
            return parcelType;
        }
    }
}
