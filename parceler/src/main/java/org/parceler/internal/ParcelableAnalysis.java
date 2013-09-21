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
import org.androidtransfuse.adapter.*;
import org.androidtransfuse.validation.Validator;
import org.parceler.Parcel;
import org.parceler.ParcelConstructor;
import org.parceler.ParcelProperty;
import org.parceler.Transient;

import javax.inject.Inject;
import java.util.*;

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

        Parcel parcelAnnotation = astType.getAnnotation(Parcel.class);
        Parcel.Serialization serialization = parcelAnnotation != null ? parcelAnnotation.value() : null;

        ParcelableDescriptor parcelableDescriptor;

        if (converter != null) {
            parcelableDescriptor = new ParcelableDescriptor(converter);
        }
        else {
            Map<String, ASTMethod> defaultWriteMethods = new HashMap<String, ASTMethod>();
            Map<String, ASTMethod> defaultReadMethods = new HashMap<String, ASTMethod>();
            Map<String, ASTField> defaultFields = new HashMap<String, ASTField>();
            if(Parcel.Serialization.METHOD.equals(serialization)){
                defaultWriteMethods.putAll(findWriteMethods(astType, false));
                defaultReadMethods.putAll(findReadMethods(astType, false));
            }
            else{
                defaultFields.putAll(findFields(astType, false));
            }
            Set<ASTConstructor> constructors = findConstructors(astType);
            Map<String, ASTParameter> writeParameters = new HashMap<String, ASTParameter>();
            if(constructors.size() == 1){
                writeParameters.putAll(findConstructorParameters(constructors.iterator().next()));
            }

            Map<String, ASTMethod> propertyWriteMethods = findWriteMethods(astType, true);
            Map<String, ASTMethod> propertyReadMethods = findReadMethods(astType, true);
            Map<String, ASTField> propertyFields = findFields(astType, true);

            //todo: check for > 1 properties


            Map<String, AccessibleReference> readReferences = new HashMap<String, AccessibleReference>();
            Map<String, FieldReference> fieldWriteReferences = new HashMap<String, FieldReference>();
            Map<String, MethodReference> methodWriteReferences = new HashMap<String, MethodReference>();

            for (Map.Entry<String, ASTMethod> methodEntry : defaultReadMethods.entrySet()) {
                readReferences.put(methodEntry.getKey(), new MethodReference(methodEntry.getValue().getReturnType(), methodEntry.getValue()));
            }
            //overwrite with field accessor
            for (Map.Entry<String, ASTField> fieldEntry : defaultFields.entrySet()) {
                readReferences.put(fieldEntry.getKey(), new FieldReference(fieldEntry.getValue()));
                fieldWriteReferences.put(fieldEntry.getKey(), new FieldReference(fieldEntry.getValue()));
            }
            //overwrite with property methods
            for (Map.Entry<String, ASTMethod> methodEntry : propertyReadMethods.entrySet()) {
                readReferences.put(methodEntry.getKey(), new MethodReference(methodEntry.getValue().getReturnType(), methodEntry.getValue()));
            }
            //overwrite with property fields
            for (Map.Entry<String, ASTField> fieldEntry : propertyFields.entrySet()) {
                readReferences.put(fieldEntry.getKey(), new FieldReference(fieldEntry.getValue()));
                fieldWriteReferences.put(fieldEntry.getKey(), new FieldReference(fieldEntry.getValue()));
            }
            //default write via methods
            for (Map.Entry<String, ASTMethod> methodEntry : defaultWriteMethods.entrySet()) {
                methodWriteReferences.put(methodEntry.getKey(), new MethodReference(methodEntry.getValue().getParameters().get(0).getASTType(), methodEntry.getValue()));
            }
            //overwrite with property methods
            for (Map.Entry<String, ASTMethod> methodEntry : propertyWriteMethods.entrySet()) {
                methodWriteReferences.put(methodEntry.getKey(), new MethodReference(methodEntry.getValue().getParameters().get(0).getASTType(), methodEntry.getValue()));
            }

            parcelableDescriptor = new ParcelableDescriptor();

            //constructor
            if(constructors.size() == 1){
                ConstructorReference constructorReference = new ConstructorReference(constructors.iterator().next());

                for (Map.Entry<String, ASTParameter> parameterEntry : writeParameters.entrySet()) {
                    //todo: check for missing read reference
                    constructorReference.getWriteReferences().put(parameterEntry.getValue(), readReferences.get(parameterEntry.getKey()));
                }

                parcelableDescriptor.setConstructorPair(constructorReference);
            }

            //fields
            for (Map.Entry<String, FieldReference> fieldReferenceEntry : fieldWriteReferences.entrySet()) {
                if(!writeParameters.containsKey(fieldReferenceEntry.getKey()) && readReferences.containsKey(fieldReferenceEntry.getKey())){
                    parcelableDescriptor.getFieldPairs().add(new ReferencePair<FieldReference>(fieldReferenceEntry.getKey(), fieldReferenceEntry.getValue(), readReferences.get(fieldReferenceEntry.getKey())));
                }
            }

            //methods
            for (Map.Entry<String, MethodReference> methodReferenceEntry : methodWriteReferences.entrySet()) {
                if(!writeParameters.containsKey(methodReferenceEntry.getKey()) && readReferences.containsKey(methodReferenceEntry.getKey())){
                    parcelableDescriptor.getMethodPairs().add(new ReferencePair<MethodReference>(methodReferenceEntry.getKey(), methodReferenceEntry.getValue(), readReferences.get(methodReferenceEntry.getKey())));
                }
            }
        }

        return parcelableDescriptor;
    }

    public Map<String, ASTMethod> findWriteMethods(ASTType astType, boolean declaredProperty){
        Map<String, ASTMethod> writeMethods = new HashMap<String, ASTMethod>();

        for (ASTMethod astMethod : astType.getMethods()) {
            if(!astMethod.isAnnotated(Transient.class) && (declaredProperty == astMethod.isAnnotated(ParcelProperty.class)) && isSetter(astMethod)){
                writeMethods.put(getPropertyName(astMethod), astMethod);
            }
        }

        return writeMethods;
    }

    public Map<String, ASTMethod> findReadMethods(ASTType astType, boolean declaredProperty){
        Map<String, ASTMethod> writeMethods = new HashMap<String, ASTMethod>();

        for (ASTMethod astMethod : astType.getMethods()) {
            if(!astMethod.isAnnotated(Transient.class) && (declaredProperty == astMethod.isAnnotated(ParcelProperty.class)) && isGetter(astMethod)){
                writeMethods.put(getPropertyName(astMethod), astMethod);
            }
        }

        return writeMethods;
    }

    public Map<String, ASTField> findFields(ASTType astType, boolean declaredProperty){
        Map<String, ASTField> fields = new HashMap<String, ASTField>();

        for (ASTField astField : astType.getFields()) {
            if(!astField.isAnnotated(Transient.class) && (declaredProperty == astField.isAnnotated(ParcelProperty.class))){
                String name = astField.getName();
                if(astField.isAnnotated(ParcelProperty.class)){
                    name = astField.getAnnotation(ParcelProperty.class).value();
                }
                fields.put(name, astField);
            }
        }

        return fields;
    }

    public Set<ASTConstructor> findConstructors(ASTType astType){
        Set<ASTConstructor> constructorResult = new HashSet<ASTConstructor>();
        if(astType.getConstructors().size() == 1 && astType.getConstructors().iterator().next().getParameters().size() != 0){
            constructorResult.addAll(astType.getConstructors());
            return constructorResult;
        }
        for(ASTConstructor constructor : astType.getConstructors()){
            if(constructor.isAnnotated(ParcelConstructor.class)){
                constructorResult.add(constructor);
            }
        }

        return constructorResult;
    }

    private Map<String, ASTParameter> findConstructorParameters(ASTConstructor constructor) {
        Map<String, ASTParameter> parameters = new HashMap<String, ASTParameter>();

        for (ASTParameter parameter : constructor.getParameters()) {
            if(parameter.isAnnotated(ParcelProperty.class)){
                ParcelProperty parcelProperty = parameter.getAnnotation(ParcelProperty.class);
                parameters.put(parcelProperty.value(), parameter);
            }
            else{
                parameters.put(parameter.getName(), parameter);
            }
        }

        return parameters;
    }

    private boolean isGetter(ASTMethod astMethod) {
        return astMethod.getParameters().size() == 0 && (astMethod.getName().startsWith(GET) || astMethod.getName().startsWith(IS));
    }

    private boolean isSetter(ASTMethod astMethod) {
        return astMethod.getParameters().size() == 1 && astMethod.getName().startsWith(SET) && astMethod.getReturnType().equals(ASTVoidType.VOID);
    }

    private String getPropertyName(ASTMethod astMethod) {
        String methodName = astMethod.getName();

        if(astMethod.isAnnotated(ParcelProperty.class)){
            return astMethod.getAnnotation(ParcelProperty.class).value();
        }

        for (String prepend : PREPENDS) {
            if (methodName.startsWith(prepend)) {
                String name = methodName.substring(prepend.length());
                return name.substring(0, 1).toLowerCase(Locale.getDefault()) + name.substring(1);
            }
        }
        throw new TransfuseAnalysisException("Unable to convert Method name " + methodName);
    }
}
