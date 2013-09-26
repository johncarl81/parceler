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
            Map<String, List<ASTMethod>> defaultWriteMethods = new HashMap<String, List<ASTMethod>>();
            Map<String, List<ASTMethod>> defaultReadMethods = new HashMap<String, List<ASTMethod>>();
            Map<String, List<ASTField>> defaultFields = new HashMap<String, List<ASTField>>();
            if(Parcel.Serialization.METHOD.equals(serialization)){
                defaultWriteMethods = combine(defaultWriteMethods, findWriteMethods(astType, false));
                defaultReadMethods = combine(defaultReadMethods, findReadMethods(astType, false));
            }
            else{
                defaultFields = combine(defaultFields, findFields(astType, false));
            }
            Set<ASTConstructor> constructors = findConstructors(astType);
            Map<String, ASTParameter> writeParameters = new HashMap<String, ASTParameter>();
            if(constructors.size() == 1){
                writeParameters.putAll(findConstructorParameters(constructors.iterator().next()));
            }

            Map<String, List<ASTMethod>> propertyWriteMethods = findWriteMethods(astType, true);
            Map<String, List<ASTMethod>> propertyReadMethods = findReadMethods(astType, true);
            Map<String, List<ASTField>> propertyFields = findFields(astType, true);

            //check for > 1 properties
            validateSingleProperty(combine(defaultWriteMethods, propertyWriteMethods));
            validateSingleProperty(combine(defaultReadMethods, propertyReadMethods));
            validateSingleProperty(combine(defaultFields, propertyFields));

            //todo: check for colliding properties

            Map<String, AccessibleReference> readReferences = new HashMap<String, AccessibleReference>();
            Map<String, FieldReference> fieldWriteReferences = new HashMap<String, FieldReference>();
            Map<String, MethodReference> methodWriteReferences = new HashMap<String, MethodReference>();

            for (Map.Entry<String, List<ASTMethod>> methodEntry : defaultReadMethods.entrySet()) {
                readReferences.put(methodEntry.getKey(), new MethodReference(methodEntry.getValue().get(0).getReturnType(), methodEntry.getValue().get(0)));
            }
            //overwrite with field accessor
            for (Map.Entry<String, List<ASTField>> fieldEntry : defaultFields.entrySet()) {
                readReferences.put(fieldEntry.getKey(), new FieldReference(fieldEntry.getValue().get(0)));
                fieldWriteReferences.put(fieldEntry.getKey(), new FieldReference(fieldEntry.getValue().get(0)));
            }
            //overwrite with property methods
            for (Map.Entry<String, List<ASTMethod>> methodEntry : propertyReadMethods.entrySet()) {
                readReferences.put(methodEntry.getKey(), new MethodReference(methodEntry.getValue().get(0).getReturnType(), methodEntry.getValue().get(0)));
            }
            //overwrite with property fields
            for (Map.Entry<String, List<ASTField>> fieldEntry : propertyFields.entrySet()) {
                readReferences.put(fieldEntry.getKey(), new FieldReference(fieldEntry.getValue().get(0)));
                fieldWriteReferences.put(fieldEntry.getKey(), new FieldReference(fieldEntry.getValue().get(0)));
            }
            //default write via methods
            for (Map.Entry<String, List<ASTMethod>> methodEntry : defaultWriteMethods.entrySet()) {
                methodWriteReferences.put(methodEntry.getKey(), new MethodReference(methodEntry.getValue().get(0).getParameters().get(0).getASTType(), methodEntry.getValue().get(0)));
            }
            //overwrite with property methods
            for (Map.Entry<String, List<ASTMethod>> methodEntry : propertyWriteMethods.entrySet()) {
                methodWriteReferences.put(methodEntry.getKey(), new MethodReference(methodEntry.getValue().get(0).getParameters().get(0).getASTType(), methodEntry.getValue().get(0)));
            }

            parcelableDescriptor = new ParcelableDescriptor();

            //constructor
            if(constructors.size() == 1){
                ConstructorReference constructorReference = new ConstructorReference(constructors.iterator().next());

                for (Map.Entry<String, ASTParameter> parameterEntry : writeParameters.entrySet()) {
                    validateReadReference(readReferences, parameterEntry.getValue(), parameterEntry.getKey());
                    constructorReference.getWriteReferences().put(parameterEntry.getValue(), readReferences.get(parameterEntry.getKey()));
                }

                parcelableDescriptor.setConstructorPair(constructorReference);
            }

            //fields
            for (Map.Entry<String, FieldReference> fieldReferenceEntry : fieldWriteReferences.entrySet()) {
                if(!writeParameters.containsKey(fieldReferenceEntry.getKey()) && readReferences.containsKey(fieldReferenceEntry.getKey())){
                    validateReadReference(readReferences, fieldReferenceEntry.getValue().getField(), fieldReferenceEntry.getKey());
                    parcelableDescriptor.getFieldPairs().add(new ReferencePair<FieldReference>(fieldReferenceEntry.getKey(), fieldReferenceEntry.getValue(), readReferences.get(fieldReferenceEntry.getKey())));
                }
            }

            //methods
            for (Map.Entry<String, MethodReference> methodReferenceEntry : methodWriteReferences.entrySet()) {
                if(!writeParameters.containsKey(methodReferenceEntry.getKey()) && readReferences.containsKey(methodReferenceEntry.getKey())){
                    validateReadReference(readReferences, methodReferenceEntry.getValue().getMethod(), methodReferenceEntry.getKey());
                    parcelableDescriptor.getMethodPairs().add(new ReferencePair<MethodReference>(methodReferenceEntry.getKey(), methodReferenceEntry.getValue(), readReferences.get(methodReferenceEntry.getKey())));
                }
            }
        }

        return parcelableDescriptor;
    }

    public Map<String, List<ASTMethod>> findWriteMethods(ASTType astType, boolean declaredProperty){
        Map<String, List<ASTMethod>> writeMethods = new HashMap<String, List<ASTMethod>>();

        for (ASTMethod astMethod : astType.getMethods()) {
            if(!astMethod.isAnnotated(Transient.class) && (declaredProperty == astMethod.isAnnotated(ParcelProperty.class)) && isSetter(astMethod)){
                String propertyName = getPropertyName(astMethod);
                if(!writeMethods.containsKey(propertyName)){
                    writeMethods.put(propertyName, new ArrayList<ASTMethod>());
                }
                writeMethods.get(propertyName).add(astMethod);
            }
        }

        return writeMethods;
    }

    public Map<String, List<ASTMethod>> findReadMethods(ASTType astType, boolean declaredProperty){
        Map<String, List<ASTMethod>> writeMethods = new HashMap<String, List<ASTMethod>>();

        for (ASTMethod astMethod : astType.getMethods()) {
            if(!astMethod.isAnnotated(Transient.class) && (declaredProperty == astMethod.isAnnotated(ParcelProperty.class)) && isGetter(astMethod)){
                String propertyName = getPropertyName(astMethod);
                if(!writeMethods.containsKey(propertyName)){
                    writeMethods.put(propertyName, new ArrayList<ASTMethod>());
                }
                writeMethods.get(propertyName).add(astMethod);
            }
        }

        return writeMethods;
    }

    public Map<String, List<ASTField>> findFields(ASTType astType, boolean declaredProperty){
        Map<String, List<ASTField>> fields = new HashMap<String, List<ASTField>>();

        for (ASTField astField : astType.getFields()) {
            if(!astField.isAnnotated(Transient.class) && (declaredProperty == astField.isAnnotated(ParcelProperty.class))){
                String name = astField.getName();
                if(astField.isAnnotated(ParcelProperty.class)){
                    name = astField.getAnnotation(ParcelProperty.class).value();
                }
                if(!fields.containsKey(name)){
                    fields.put(name, new ArrayList<ASTField>());
                }
                fields.get(name).add(astField);
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

    private <T extends ASTBase> void validateSingleProperty(Map<String, List<T>> input){
        for (Map.Entry<String, List<T>> entry : input.entrySet()) {
            if(entry.getValue().size() != 1){
                for (ASTBase astBase : entry.getValue()) {
                    validator.error("Too many properties defined under " + entry.getKey())
                            .element(astBase)
                            .build();
                }
            }
        }
    }

    private <T extends ASTBase> void validateReadReference(Map<String, AccessibleReference> references, ASTBase mutator, String name){
        if(!references.containsKey(name)){
            validator.error("Accessor not found for property " + name)
                    .element(mutator)
                    .build();
        }
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

    private <T> Map<String, List<T>> combine(Map<String, List<T>> one, Map<String, List<T>> two){
        Map<String, List<T>> result = new HashMap<String, List<T>>();

        result.putAll(one);

        for (Map.Entry<String, List<T>> twoEntry : two.entrySet()) {
            if(!result.containsKey(twoEntry.getKey())){
                result.put(twoEntry.getKey(), twoEntry.getValue());
            }
            else{
                result.get(twoEntry.getKey()).addAll(twoEntry.getValue());
            }
        }
        return result;
     }
}
