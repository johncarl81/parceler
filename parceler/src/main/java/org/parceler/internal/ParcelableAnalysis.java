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
import org.parceler.*;

import javax.inject.Inject;
import java.util.*;

/**
 * @author John Ericksen
 */
public class ParcelableAnalysis {

    private static final ASTType OBJECT_TYPE = new ASTStringType(Object.class.getName());
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
        ASTAnnotation parcelASTAnnotation = astType.getASTAnnotation(Parcel.class);

        ASTType[] interfaces = parcelASTAnnotation != null ? parcelASTAnnotation.getProperty("implementations", ASTType[].class) : new ASTType[0];

        ParcelableDescriptor parcelableDescriptor= new ParcelableDescriptor(interfaces);

        if (converter != null) {
            parcelableDescriptor = new ParcelableDescriptor(interfaces, converter);
        }
        else {
            Set<MethodSignature> definedMethods = new HashSet<MethodSignature>();
            Map<String, ASTReference<ASTParameter>> writeParameters = new HashMap<String, ASTReference<ASTParameter>>();

            Set<ASTConstructor> constructors = findConstructors(astType);
            Set<ASTMethod> factoryMethods = findFactoryMethods(astType);
            ConstructorReference constructorReference = null;
            if(factoryMethods.size() == 1){
                writeParameters.putAll(findMethodParameters(factoryMethods.iterator().next()));
                constructorReference = new ConstructorReference(factoryMethods.iterator().next());

                parcelableDescriptor.setConstructorPair(constructorReference);
            }
            else if(factoryMethods.size() > 1){
                validator.error("Too many @ParcelFactory annotated factory methods.").element(astType).build();
            }
            else if(constructors.size() == 1){
                writeParameters.putAll(findConstructorParameters(constructors.iterator().next()));
                constructorReference = new ConstructorReference(constructors.iterator().next());

                parcelableDescriptor.setConstructorPair(constructorReference);
            }
            else if(constructors.size() == 0){
                validator.error("No @ParcelConstructor annotated constructor and no default empty bean constructor found.").element(astType).build();
            }
            else {
                validator.error("Too many @ParcelConstructor annotated constructors found.").element(astType).build();
            }

            for(ASTType hierarchyLoop = astType; hierarchyLoop != null && !hierarchyLoop.equals(OBJECT_TYPE); hierarchyLoop = hierarchyLoop.getSuperClass()){
                Map<String, List<ASTReference<ASTMethod>>> defaultWriteMethods = new HashMap<String, List<ASTReference<ASTMethod>>>();
                Map<String, List<ASTReference<ASTMethod>>> defaultReadMethods = new HashMap<String, List<ASTReference<ASTMethod>>>();
                Map<String, List<ASTReference<ASTField>>> defaultFields = new HashMap<String, List<ASTReference<ASTField>>>();

                if(Parcel.Serialization.METHOD.equals(serialization)){
                    defaultWriteMethods.putAll(findWriteMethods(hierarchyLoop, definedMethods, false));
                    defaultReadMethods.putAll(findReadMethods(hierarchyLoop, definedMethods, false));
                }
                else{
                    defaultFields.putAll(findFields(hierarchyLoop, false));
                }

                Map<String, List<ASTReference<ASTMethod>>> propertyWriteMethods = findWriteMethods(hierarchyLoop, definedMethods, true);
                Map<String, List<ASTReference<ASTMethod>>> propertyReadMethods = findReadMethods(hierarchyLoop, definedMethods, true);
                Map<String, List<ASTReference<ASTField>>> propertyFields = findFields(hierarchyLoop, true);

                //check for > 1 properties
                Map<String, List<ASTReference<ASTMethod>>> writeCombination = combine(defaultWriteMethods, propertyWriteMethods);
                Map<String, List<ASTReference<ASTMethod>>> readCombination = combine(defaultReadMethods, propertyReadMethods);
                Map<String, List<ASTReference<ASTField>>> fieldCombination = combine(defaultFields, propertyFields);
                validateSingleProperty(writeCombination);
                validateSingleProperty(readCombination);
                validateSingleProperty(fieldCombination);

                validateConverters(combine(readCombination, writeCombination), fieldCombination, writeParameters);

                Map<String, AccessibleReference> readReferences = new HashMap<String, AccessibleReference>();
                Map<String, FieldReference> fieldWriteReferences = new HashMap<String, FieldReference>();
                Map<String, MethodReference> methodWriteReferences = new HashMap<String, MethodReference>();
                Map<String, ASTType> converters = new HashMap<String, ASTType>();

                for (Map.Entry<String, List<ASTReference<ASTMethod>>> methodEntry : defaultReadMethods.entrySet()) {
                    readReferences.put(methodEntry.getKey(), new MethodReference(astType, hierarchyLoop, methodEntry.getKey(), methodEntry.getValue().get(0).getReference().getReturnType(), methodEntry.getValue().get(0).getReference()));
                }
                //overwrite with field accessor
                for (Map.Entry<String, List<ASTReference<ASTField>>> fieldEntry : defaultFields.entrySet()) {
                    readReferences.put(fieldEntry.getKey(), new FieldReference(hierarchyLoop, fieldEntry.getKey(), fieldEntry.getValue().get(0).getReference()));
                    fieldWriteReferences.put(fieldEntry.getKey(), new FieldReference(hierarchyLoop, fieldEntry.getKey(), fieldEntry.getValue().get(0).getReference()));
                }
                //overwrite with property methods
                for (Map.Entry<String, List<ASTReference<ASTMethod>>> methodEntry : propertyReadMethods.entrySet()) {
                    readReferences.put(methodEntry.getKey(), new MethodReference(astType, hierarchyLoop, methodEntry.getKey(), methodEntry.getValue().get(0).getReference().getReturnType(), methodEntry.getValue().get(0).getReference()));
                    if(methodEntry.getValue().get(0).getConverter() != null){
                        converters.put(methodEntry.getKey(), methodEntry.getValue().get(0).getConverter());
                    }
                }
                //overwrite with property fields
                for (Map.Entry<String, List<ASTReference<ASTField>>> fieldEntry : propertyFields.entrySet()) {
                    readReferences.put(fieldEntry.getKey(), new FieldReference(hierarchyLoop, fieldEntry.getKey(), fieldEntry.getValue().get(0).getReference()));
                    fieldWriteReferences.put(fieldEntry.getKey(), new FieldReference(hierarchyLoop, fieldEntry.getKey(), fieldEntry.getValue().get(0).getReference()));
                    if(fieldEntry.getValue().get(0).getConverter() != null){
                        converters.put(fieldEntry.getKey(), fieldEntry.getValue().get(0).getConverter());
                    }
                }
                //default write via methods
                for (Map.Entry<String, List<ASTReference<ASTMethod>>> methodEntry : defaultWriteMethods.entrySet()) {
                    methodWriteReferences.put(methodEntry.getKey(), new MethodReference(astType, hierarchyLoop, methodEntry.getKey(), methodEntry.getValue().get(0).getReference().getParameters().get(0).getASTType(), methodEntry.getValue().get(0).getReference()));
                }
                //overwrite with property methods
                for (Map.Entry<String, List<ASTReference<ASTMethod>>> methodEntry : propertyWriteMethods.entrySet()) {
                    methodWriteReferences.put(methodEntry.getKey(), new MethodReference(astType, hierarchyLoop, methodEntry.getKey(), methodEntry.getValue().get(0).getReference().getParameters().get(0).getASTType(), methodEntry.getValue().get(0).getReference()));
                    if(methodEntry.getValue().get(0).getConverter() != null){
                        converters.put(methodEntry.getKey(), methodEntry.getValue().get(0).getConverter());
                    }
                }

                //constructor
                if(constructorReference != null){

                    for (Map.Entry<String, ASTReference<ASTParameter>> parameterEntry : writeParameters.entrySet()) {
                        if(readReferences.containsKey(parameterEntry.getKey())){
                            if(constructorReference.getWriteReferences().containsKey(parameterEntry.getValue().getReference())){
                                validator.error("More than one property found in inheritance hierarchy to match constructor parameter " + parameterEntry.getKey() +
                                        ".  Consider renaming or using a manual ParcelConverter.").element(parameterEntry.getValue().getReference()).build();
                            }
                            else{
                                validateReadReference(readReferences, parameterEntry.getValue().getReference(), parameterEntry.getKey());
                                constructorReference.putReference(parameterEntry.getValue().getReference(), readReferences.get(parameterEntry.getKey()));
                                if(parameterEntry.getValue().getConverter() != null){
                                    constructorReference.putConverter(parameterEntry.getValue().getReference(), parameterEntry.getValue().getConverter());
                                }
                            }
                        }
                    }
                }

                //methods
                for (Map.Entry<String, MethodReference> methodReferenceEntry : methodWriteReferences.entrySet()) {
                    if(!writeParameters.containsKey(methodReferenceEntry.getKey()) && readReferences.containsKey(methodReferenceEntry.getKey())){
                        validateReadReference(readReferences, methodReferenceEntry.getValue().getMethod(), methodReferenceEntry.getKey());
                        ASTType propertyConverter = converters.containsKey(methodReferenceEntry.getKey()) ? converters.get(methodReferenceEntry.getKey()) : null;
                        parcelableDescriptor.getMethodPairs().add(new ReferencePair<MethodReference>(methodReferenceEntry.getKey(), methodReferenceEntry.getValue(), readReferences.get(methodReferenceEntry.getKey()), propertyConverter));
                    }
                }

                //fields
                for (Map.Entry<String, FieldReference> fieldReferenceEntry : fieldWriteReferences.entrySet()) {
                    if(!writeParameters.containsKey(fieldReferenceEntry.getKey()) &&
                            !methodWriteReferences.containsKey(fieldReferenceEntry.getKey()) &&
                            readReferences.containsKey(fieldReferenceEntry.getKey())){
                        validateReadReference(readReferences, fieldReferenceEntry.getValue().getField(), fieldReferenceEntry.getKey());
                        ASTType propertyConverter = converters.containsKey(fieldReferenceEntry.getKey()) ? converters.get(fieldReferenceEntry.getKey()) : null;
                        parcelableDescriptor.getFieldPairs().add(new ReferencePair<FieldReference>(fieldReferenceEntry.getKey(), fieldReferenceEntry.getValue(), readReferences.get(fieldReferenceEntry.getKey()), propertyConverter));
                    }
                }

                //Add all public methods for the ability to determine if they have been overridden in a lower subclass
                for (ASTMethod astMethod : astType.getMethods()) {
                    if(astMethod.getAccessModifier().equals(ASTAccessModifier.PUBLIC)){
                        definedMethods.add(new MethodSignature(astMethod));
                    }
                }
            }

            //validate all constructor parameters have a matching read converter
            if(constructorReference != null && constructorReference.getConstructor() != null){
                for (ASTParameter parameter : constructorReference.getConstructor().getParameters()) {
                    if(constructorReference.getWriteReference(parameter) == null){
                        validator.error("No corresponding property found for constructor parameter " + parameter.getName())
                                .element(parameter).build();
                    }
                }
            }
        }

        return parcelableDescriptor;
    }

    private Set<ASTMethod> findFactoryMethods(ASTType astType) {
        Set<ASTMethod> methodResult = new HashSet<ASTMethod>();
        for(ASTMethod method : astType.getMethods()){
            if(method.isAnnotated(ParcelFactory.class) /*&& method.isStatic()*/){
                methodResult.add(method);
            }
        }

        return methodResult;
    }

    public Map<String, List<ASTReference<ASTMethod>>> findWriteMethods(ASTType astType, Set<MethodSignature> definedMethods, boolean declaredProperty){
        Map<String, List<ASTReference<ASTMethod>>> writeMethods = new HashMap<String, List<ASTReference<ASTMethod>>>();

        for (ASTMethod astMethod : astType.getMethods()) {
            if(!astMethod.isAnnotated(Transient.class) &&
                    !definedMethods.contains(new MethodSignature(astMethod)) &&
                    (declaredProperty == astMethod.isAnnotated(ParcelProperty.class) &&
                    isSetter(astMethod))){
                String propertyName = getPropertyName(astMethod);
                ASTType converter = getConverter(astMethod);
                if(!writeMethods.containsKey(propertyName)){
                    writeMethods.put(propertyName, new ArrayList<ASTReference<ASTMethod>>());
                }
                writeMethods.get(propertyName).add(new ASTReference<ASTMethod>(astMethod, converter));
            }
        }

        return writeMethods;
    }

    public Map<String, List<ASTReference<ASTMethod>>> findReadMethods(ASTType astType, Set<MethodSignature> definedMethods, boolean declaredProperty){
        Map<String, List<ASTReference<ASTMethod>>> writeMethods = new HashMap<String, List<ASTReference<ASTMethod>>>();

        for (ASTMethod astMethod : astType.getMethods()) {
            if(!astMethod.isAnnotated(Transient.class) &&
                    !definedMethods.contains(new MethodSignature(astMethod)) &&
                    (declaredProperty == astMethod.isAnnotated(ParcelProperty.class) && isGetter(astMethod))){
                String propertyName = getPropertyName(astMethod);
                ASTType converter = getConverter(astMethod);
                if(!writeMethods.containsKey(propertyName)){
                    writeMethods.put(propertyName, new ArrayList<ASTReference<ASTMethod>>());
                }
                writeMethods.get(propertyName).add(new ASTReference<ASTMethod>(astMethod, converter));
            }
        }

        return writeMethods;
    }

    public Map<String, List<ASTReference<ASTField>>> findFields(ASTType astType, boolean declaredProperty){
        Map<String, List<ASTReference<ASTField>>> fields = new HashMap<String, List<ASTReference<ASTField>>>();

        for (ASTField astField : astType.getFields()) {
            if(!astField.isAnnotated(Transient.class) &&
                    !astField.isTransient() &&
                    (declaredProperty == astField.isAnnotated(ParcelProperty.class))){
                String name = astField.getName();
                ASTType converter = null;
                if(astField.isAnnotated(ParcelProperty.class)){
                    name = astField.getAnnotation(ParcelProperty.class).value();
                }
                if(astField.isAnnotated(ParcelPropertyConverter.class)){
                    ASTAnnotation converterAnnotation = astField.getASTAnnotation(ParcelPropertyConverter.class);
                    converter = converterAnnotation.getProperty("value", ASTType.class);
                }
                if(!fields.containsKey(name)){
                    fields.put(name, new ArrayList<ASTReference<ASTField>>());
                }
                fields.get(name).add(new ASTReference<ASTField>(astField, converter));
            }
        }

        return fields;
    }

    public Set<ASTConstructor> findConstructors(ASTType astType){
        Set<ASTConstructor> constructorResult = new HashSet<ASTConstructor>();
        ASTConstructor emptyBeanConstructor = null;
        for(ASTConstructor constructor : astType.getConstructors()){
            if(constructor.getParameters().isEmpty()){
                emptyBeanConstructor = constructor;
            }
        }
        for(ASTConstructor constructor : astType.getConstructors()){
            if(constructor.isAnnotated(ParcelConstructor.class)){
                constructorResult.add(constructor);
            }
        }
        if(!constructorResult.isEmpty()){
            return constructorResult;
        }
        //if none are found, then try to find empty bean constructor
        if(emptyBeanConstructor != null){
            return Collections.singleton(emptyBeanConstructor);
        }
        return Collections.emptySet();
    }

    private static final class ASTReference<T extends ASTBase>{
        private final ASTType converter;
        private final T reference;

        private ASTReference(T reference, ASTType converter) {
            this.converter = converter;
            this.reference = reference;
        }

        public ASTType getConverter() {
            return converter;
        }

        public T getReference() {
            return reference;
        }
    }

    private Map<String, ASTReference<ASTParameter>> findConstructorParameters(ASTConstructor constructor) {
        Map<String, ASTReference<ASTParameter>> parameters = new HashMap<String, ASTReference<ASTParameter>>();

        for (ASTParameter parameter : constructor.getParameters()) {
            String name = parameter.getName();
            ASTType converter = null;
            if(parameter.isAnnotated(ParcelProperty.class)){
                name = parameter.getAnnotation(ParcelProperty.class).value();
            }
            if(parameter.isAnnotated(ParcelPropertyConverter.class)){
                ASTAnnotation conveterAnnotation = parameter.getASTAnnotation(ParcelPropertyConverter.class);
                converter = conveterAnnotation.getProperty("value", ASTType.class);
            }
            parameters.put(name, new ASTReference<ASTParameter>(parameter, converter));
        }

        return parameters;
    }

    private Map<String, ASTReference<ASTParameter>> findMethodParameters(ASTMethod method) {
        Map<String, ASTReference<ASTParameter>> parameters = new HashMap<String, ASTReference<ASTParameter>>();

        for (ASTParameter parameter : method.getParameters()) {
            String name = parameter.getName();
            ASTType converter = null;
            if(parameter.isAnnotated(ParcelProperty.class)){
                name = parameter.getAnnotation(ParcelProperty.class).value();
            }
            if(parameter.isAnnotated(ParcelPropertyConverter.class)){
                ASTAnnotation conveterAnnotation = parameter.getASTAnnotation(ParcelPropertyConverter.class);
                converter = conveterAnnotation.getProperty("value", ASTType.class);
            }
            parameters.put(name, new ASTReference<ASTParameter>(parameter, converter));
        }

        return parameters;
    }

    private void validateSingleProperty(Map<String, ? extends List<? extends ASTReference<? extends ASTBase>>> input){
        for (Map.Entry<String, ? extends List<? extends ASTReference<? extends ASTBase>>> entry : input.entrySet()) {
            if(entry.getValue().size() != 1){
                for (ASTReference<? extends ASTBase> reference : entry.getValue()) {
                    validator.error("Too many properties defined under " + entry.getKey())
                            .element(reference.getReference())
                            .build();
                }
            }
        }
    }

    private void validateConverters(Map<String, List<ASTReference<ASTMethod>>> input, Map<String, List<ASTReference<ASTField>>> fieldReferences, Map<String, ASTReference<ASTParameter>> parameterReferences){
        Set<String> keys = new HashSet<String>();
        keys.addAll(input.keySet());
        keys.addAll(fieldReferences.keySet());
        keys.addAll(parameterReferences.keySet());

        for (String key : keys) {
            boolean found = false;
            if(input.containsKey(key)){
                for (ASTReference<ASTMethod> reference : input.get(key)) {
                    if(reference.getConverter() != null){
                        if(found){
                            validator.error("Only one ParcelConverter may be declared per property")
                                    .element(reference.getReference())
                                    .build();
                        }
                        found = true;
                    }
                }
            }
            if(fieldReferences.containsKey(key)){
                for (ASTReference<ASTField> fieldReference : fieldReferences.get(key)) {
                    if(fieldReference.getConverter() != null){
                        if(found){
                            validator.error("Only one ParcelConverter may be declared per property")
                                    .element(fieldReference.getReference())
                                    .build();
                        }
                        found = true;
                    }
                }
            }
            if(parameterReferences.containsKey(key)){
                ASTReference<ASTParameter> parameterReference = parameterReferences.get(key);
                if(parameterReference.getConverter() != null){
                    if(found){
                        validator.error("Only one ParcelConverter may be declared per property")
                                .element(parameterReference.getReference())
                                .build();
                    }
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
        return astMethod.getParameters().size() == 0 &&
                (astMethod.getName().startsWith(GET) || astMethod.getName().startsWith(IS)) &&
                astMethod.getAccessModifier().equals(ASTAccessModifier.PUBLIC);
    }

    private boolean isSetter(ASTMethod astMethod) {
        return astMethod.getParameters().size() == 1 && astMethod.getName().startsWith(SET) &&
                astMethod.getReturnType().equals(ASTVoidType.VOID) &&
                astMethod.getAccessModifier().equals(ASTAccessModifier.PUBLIC);
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

    private ASTType getConverter(ASTMethod astMethod) {
        if(astMethod.isAnnotated(ParcelProperty.class) && astMethod.isAnnotated(ParcelPropertyConverter.class)){
            return astMethod.getASTAnnotation(ParcelPropertyConverter.class).getProperty("value", ASTType.class);
        }
        return null;
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
