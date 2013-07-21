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
import org.androidtransfuse.adapter.classes.ASTClassFactory;
import org.androidtransfuse.transaction.AbstractCompletionTransactionWorker;
import org.parceler.Parcel;
import org.parceler.ParcelClass;
import org.parceler.ParcelClasses;

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
    private final ASTClassFactory astClassFactory;

    @Inject
    public ExternalParcelTransactionWorker(ParcelableAnalysis parcelableAnalysis, ParcelableGenerator parcelableGenerator, ASTClassFactory astClassFactory) {
        this.parcelableAnalysis = parcelableAnalysis;
        this.parcelableGenerator = parcelableGenerator;
        this.astClassFactory = astClassFactory;
    }

    @Override
    public Map<Provider<ASTType>, JDefinedClass> innerRun(Provider<ASTType> valueProvider) {

        ASTType value = valueProvider.get();
        Map<Provider<ASTType>, JDefinedClass> generatedSource = new HashMap<Provider<ASTType>, JDefinedClass>();

        ASTAnnotation parcelClassesAnnotation = value.getASTAnnotation(ParcelClasses.class);
        if(parcelClassesAnnotation != null){
            ASTAnnotation[] parcelTypes = parcelClassesAnnotation.getProperty("value", ASTAnnotation[].class);

            for(ASTAnnotation annotation : parcelTypes){
                ASTType parcelType = annotation.getProperty("value", ASTType.class);
                ParcelableDescriptor analysis = parcelableAnalysis.analyze(parcelType, getConverterType(annotation));
                generatedSource.put(new ASTTypeProvider(parcelType), parcelableGenerator.generateParcelable(parcelType, analysis));
            }
        }

        ASTAnnotation astAnnotation = value.getASTAnnotation(ParcelClass.class);
        if(astAnnotation != null){
            ASTType parcelType = astAnnotation.getProperty("value", ASTType.class);
            ParcelableDescriptor analysis = parcelableAnalysis.analyze(parcelType, getConverterType(astAnnotation));
            generatedSource.put(new ASTTypeProvider(parcelType), parcelableGenerator.generateParcelable(parcelType, analysis));
        }

        return generatedSource;
    }

    private ASTType getConverterType(ASTAnnotation parcelClassAnnotation) {
        ASTType converterType = parcelClassAnnotation.getProperty("converter", ASTType.class);
        ASTType emptyConverterType = astClassFactory.getType(Parcel.EmptyConverter.class);
        if(!emptyConverterType.equals(converterType)){
            return converterType;
        }
        return null;
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
