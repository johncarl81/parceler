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

import org.androidtransfuse.TransfuseAnalysisException;
import org.androidtransfuse.adapter.ASTMethod;
import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.adapter.ASTVoidType;
import org.androidtransfuse.validation.Validator;
import org.parceler.Transient;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author John Ericksen
 */
public class ParcelableAnalysis {

    private static final String GET = "get";
    private static final String IS = "is";
    private static final String SET = "set";
    private static final String[] PREPENDS = {GET, IS, SET};
    private final Map<ASTType, ParcelableDescriptor> parcelableCache = new HashMap<ASTType, ParcelableDescriptor>();
    private final Validator validator;

    @Inject
    public ParcelableAnalysis(Validator validator) {
        this.validator = validator;
    }

    public ParcelableDescriptor analyze(ASTType astType, ASTType converter) {
        if (!parcelableCache.containsKey(astType)) {
            ParcelableDescriptor parcelableDescriptor = innerAnalyze(astType, converter);
            parcelableCache.put(astType, parcelableDescriptor);
        }
        return parcelableCache.get(astType);
    }

    private ParcelableDescriptor innerAnalyze(ASTType astType, ASTType converter) {

        ParcelableDescriptor parcelableDescriptor;

        if (converter != null) {
            parcelableDescriptor = new ParcelableDescriptor(converter);
        } else {
            Map<String, ASTMethod> methodNameMap = new HashMap<String, ASTMethod>();
            parcelableDescriptor = new ParcelableDescriptor();

            for (ASTMethod astMethod : astType.getMethods()) {
                methodNameMap.put(astMethod.getName(), astMethod);
            }

            //find all applicable getters
            for (ASTMethod astMethod : astType.getMethods()) {
                if (!astMethod.isAnnotated(Transient.class) && isGetter(astMethod)) {
                    String setterName = SET + astMethod.getName().substring(GET.length());
                    ASTMethod setterMethod = methodNameMap.get(setterName);

                    if (setterMethod != null && !setterMethod.isAnnotated(Transient.class)) {
                        if (setterMethod.getParameters().size() > 1){
                            validator.error("Setter has too few parameters.")
                                    .element(astMethod).build();
                        }
                        else if (setterMethod.getParameters().size() > 1){
                            validator.error("Setter has too many parameters.")
                                    .element(astMethod).build();
                        }
                        else if(!setterMethod.getParameters().get(0).getASTType().equals(astMethod.getReturnType())) {
                            validator.error("Setter parameter does not match corresponding Getter return type")
                                            .element(astMethod).build();
                        }
                        else{
                            parcelableDescriptor.getMethodPairs().add(new ReferencePair<MethodReference>(getPropertyName(astMethod), new MethodReference(astMethod.getReturnType(), setterMethod), new MethodReference(astMethod.getReturnType(), astMethod)));
                        }
                    }
                }
            }
        }

        return parcelableDescriptor;
    }

    private boolean isGetter(ASTMethod astMethod) {
        boolean isGetter = astMethod.getParameters().size() == 0 &&
                (astMethod.getName().startsWith(GET) || astMethod.getName().startsWith(IS));
        if (isGetter && astMethod.getReturnType().equals(ASTVoidType.VOID)) {
            validator.error("Bean setter in parcel must not return void.")
                    .element(astMethod).build();
        }
        return isGetter;
    }

    private String getPropertyName(ASTMethod astMethod) {
        String methodName = astMethod.getName();

        for (String prepend : PREPENDS) {
            if (methodName.startsWith(prepend)) {
                String name = methodName.substring(prepend.length());
                return name.substring(0, 1).toLowerCase(Locale.getDefault()) + name.substring(1);
            }
        }
        throw new TransfuseAnalysisException("Unable to convert Method name " + methodName);
    }
}
