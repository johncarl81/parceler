/**
 * Copyright 2013-2015 John Ericksen
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

import com.google.common.base.Joiner;
import org.androidtransfuse.TransfuseAnalysisException;
import org.androidtransfuse.adapter.*;
import org.androidtransfuse.validation.Validator;
import org.parceler.*;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.*;

/**
 * @author John Ericksen
 */
@Singleton
public class ParcelableAnalysis {

    private static final ASTType EMPTY_CONVERTER_TYPE = new ASTStringType(ParcelConverter.EmptyConverter.class.getCanonicalName());
    private static final ASTType OBJECT_TYPE = new ASTStringType(Object.class.getName());
    private static final String GET = "get";
    private static final String IS = "is";
    private static final String SET = "set";
    private static final String[] PREPENDS = {GET, IS, SET};

    private final Map<ASTType, ParcelableDescriptor> parcelableCache = new HashMap<ASTType, ParcelableDescriptor>();
    private final Validator validator;
    private final Provider<Generators> generatorsProvider;

    @Inject
    public ParcelableAnalysis(Validator validator, Provider<Generators> generatorsProvider) {
        this.validator = validator;
        this.generatorsProvider = generatorsProvider;
    }

    public ParcelableDescriptor analyze(ASTType astType) {
        return analyze(astType, null);
    }

    public ParcelableDescriptor analyze(ASTType astType, ASTAnnotation parcelASTAnnotation) {
        if (!parcelableCache.containsKey(astType)) {
            ParcelableDescriptor parcelableDescriptor = innerAnalyze(astType, parcelASTAnnotation);
            parcelableCache.put(astType, parcelableDescriptor);
        }
        return parcelableCache.get(astType);
    }

    private ParcelableDescriptor innerAnalyze(ASTType astType, ASTAnnotation parcelASTAnnotation) {

        ASTType converter = getConverter(parcelASTAnnotation);
        Parcel.Serialization serialization = parcelASTAnnotation != null ? parcelASTAnnotation.getProperty("value", Parcel.Serialization.class) : null;
        boolean parcelsIndex = parcelASTAnnotation == null || defaultValue(parcelASTAnnotation.getProperty("parcelsIndex", boolean.class), true);
        ASTType[] interfaces = parcelASTAnnotation != null ? parcelASTAnnotation.getProperty("implementations", ASTType[].class) : new ASTType[0];

        ASTAnnotation configuration = parcelASTAnnotation != null ? parcelASTAnnotation.getProperty("configuration", ASTAnnotation.class) : null;

        validateConfiguration(astType, configuration);

        ParcelableDescriptor parcelableDescriptor;

        if (converter != null) {
            parcelableDescriptor = new ParcelableDescriptor(interfaces, converter, parcelsIndex);
        }
        else {
            parcelableDescriptor = new ParcelableDescriptor(interfaces, parcelsIndex);
            Set<MethodSignature> definedMethods = new HashSet<MethodSignature>();
            Map<String, ASTReference<ASTParameter>> writeParameters = new HashMap<String, ASTReference<ASTParameter>>();

            ASTConstructor configuredConstructor = findConfiguredConstructor(astType, configuration);
            Set<ASTConstructor> constructors = findConstructors(configuredConstructor, astType, true);
            Set<ASTMethod> factoryMethods = findFactoryMethods(astType);

            ConstructorReference constructorReference = null;
            if(!factoryMethods.isEmpty() && !findConstructors(configuredConstructor, astType, false).isEmpty()) {
                validator.error("Both @ParcelConstructor and @ParcelFactory may not be annotated on the same class.").element(astType).build();
            }
            else if(factoryMethods.size() == 1){
                ASTMethod factoryMethod = factoryMethods.iterator().next();
                if(!factoryMethod.isStatic()) {
                    validator.error("@ParcelFactory method must be static").element(factoryMethod).build();
                }
                else {
                    writeParameters.putAll(findMethodParameters(factoryMethod));
                    constructorReference = new ConstructorReference(factoryMethods.iterator().next());

                    parcelableDescriptor.setConstructorPair(constructorReference);
                }
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

                if(Parcel.Serialization.BEAN.equals(serialization) || Parcel.Serialization.METHOD.equals(serialization)){
                    defaultWriteMethods.putAll(findWriteMethods(hierarchyLoop, definedMethods, false));
                    defaultReadMethods.putAll(findReadMethods(hierarchyLoop, definedMethods, false));
                }
                else{
                    defaultFields.putAll(findFields(hierarchyLoop, false));
                }

                Map<String, List<ASTReference<ASTMethod>>> propertyWriteMethods = findWriteMethods(hierarchyLoop, definedMethods, true);
                Map<String, List<ASTReference<ASTMethod>>> propertyReadMethods = findReadMethods(hierarchyLoop, definedMethods, true);
                Map<String, List<ASTReference<ASTField>>> propertyFields = findFields(hierarchyLoop, true);
                Map<String, List<ASTReference<ASTMethod>>> propertyConfigurationWriteMethods = findConfigurationWriteMethods(configuration, hierarchyLoop);
                Map<String, List<ASTReference<ASTMethod>>> propertyConfigurationReadMethods = findConfigurationReadMethods(configuration, hierarchyLoop);
                Map<String, List<ASTReference<ASTField>>> propertyConfigurationFields = findConfigurationFields(configuration, hierarchyLoop);

                //check for > 1 properties
                Map<String, List<ASTReference<ASTMethod>>> writeCombination = combine(defaultWriteMethods, propertyWriteMethods, propertyConfigurationWriteMethods);
                Map<String, List<ASTReference<ASTMethod>>> readCombination = combine(defaultReadMethods, propertyReadMethods, propertyConfigurationReadMethods);
                Map<String, List<ASTReference<ASTField>>> fieldCombination = combine(defaultFields, propertyFields, propertyConfigurationFields);
                validateSingleProperty(writeCombination);
                validateSingleProperty(readCombination);
                validateSingleProperty(fieldCombination);

                validateConverters(combine(writeCombination, readCombination), fieldCombination, writeParameters);

                propertyWriteMethods = combine(propertyWriteMethods, propertyConfigurationWriteMethods);
                propertyReadMethods = combine(propertyReadMethods, propertyConfigurationReadMethods);
                propertyFields = combine(propertyFields, propertyConfigurationFields);

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
                    if(fieldEntry.getValue().get(0).getConverter() != null){
                        converters.put(fieldEntry.getKey(), fieldEntry.getValue().get(0).getConverter());
                    }
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
                    if(methodEntry.getValue().get(0).getConverter() != null){
                        converters.put(methodEntry.getKey(), methodEntry.getValue().get(0).getConverter());
                    }
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
                                else {
                                    validateType(parameterEntry.getValue().getReference().getASTType(), parameterEntry.getValue().getReference(), parameterEntry.getValue().getReference().toString());
                                }
                            }
                        }
                    }
                }

                //methods
                for (Map.Entry<String, MethodReference> methodReferenceEntry : methodWriteReferences.entrySet()) {
                    MethodReference methodReference = methodReferenceEntry.getValue();
                    String propertyName = methodReferenceEntry.getKey();
                    if(!writeParameters.containsKey(propertyName) && readReferences.containsKey(propertyName)){
                        validateReadReference(readReferences, methodReference.getMethod(), propertyName);
                        ASTType propertyConverter = converters.containsKey(propertyName) ? converters.get(propertyName) : null;
                        if(propertyConverter == null){
                            validateType(methodReference.getType(), methodReference.getMethod(), methodReference.getOwner().getName() + "#" + methodReference.getName());
                        }
                        parcelableDescriptor.getMethodPairs().add(new ReferencePair<MethodReference>(propertyName, methodReference, readReferences.get(propertyName), propertyConverter));
                    }
                }

                //fields
                for (Map.Entry<String, FieldReference> fieldReferenceEntry : fieldWriteReferences.entrySet()) {
                    FieldReference fieldReference = fieldReferenceEntry.getValue();
                    String propertyName = fieldReferenceEntry.getKey();
                    if(!writeParameters.containsKey(propertyName) &&
                            !methodWriteReferences.containsKey(propertyName) &&
                            readReferences.containsKey(propertyName)){
                        validateReadReference(readReferences, fieldReference.getField(), propertyName);
                        ASTType propertyConverter = converters.containsKey(propertyName) ? converters.get(propertyName) : null;
                        if(propertyConverter == null){
                            validateType(fieldReference.getType(), fieldReference.getField(), fieldReference.getOwner().getName() + "." + fieldReference.getName());
                        }
                        parcelableDescriptor.getFieldPairs().add(new ReferencePair<FieldReference>(propertyName, fieldReference, readReferences.get(propertyName), propertyConverter));
                    }
                }

                //Add all public methods for the ability to determine if they have been overridden.
                for (ASTMethod astMethod : astType.getMethods()) {
                    if(astMethod.getAccessModifier().equals(ASTAccessModifier.PUBLIC)){
                        definedMethods.add(new MethodSignature(astMethod));
                    }
                }
            }

            //validate all constructor parameters have a matching read converter
            if(constructorReference != null && constructorReference.getConstructor() != null){
                for (ASTParameter parameter : constructorReference.getConstructor().getParameters()) {
                    if(!constructorReference.containsWriteReference(parameter)){
                        validator.error("No corresponding property found for constructor parameter " + parameter.getName())
                                .element(parameter).build();
                    }
                }
            }
        }

        if(validator.isInError()){
            return null;
        }

        return parcelableDescriptor;
    }

    private ASTConstructor findConfiguredConstructor(ASTType astType, ASTAnnotation configuration) {
        if(configuration == null){
            return null;
        }
        ASTAnnotation constructorConfiguration = configuration.getProperty("constructor", ASTAnnotation.class);
        ASTType[] parameters = constructorConfiguration.getProperty("value", ASTType[].class);
        if(parameters.length > 0) {
            return ASTUtils.getInstance().findConstructor(astType, parameters);
        }
        return null;
    }

    private Map<String, List<ASTReference<ASTField>>> findConfigurationFields(ASTAnnotation configuration, ASTType hierarchyLoop) {
        Map<String, List<ASTReference<ASTField>>> fields = new HashMap<String, List<ASTReference<ASTField>>>();
        if(configuration != null) {
            ASTAnnotation[] fieldAnnotations = configuration.getProperty("fields", ASTAnnotation[].class);
            for (ASTAnnotation fieldAnnotation : fieldAnnotations) {
                ASTType containingType = fieldAnnotation.getProperty("type", ASTType.class);
                if(containingType.equals(hierarchyLoop)) {
                    String fieldName = fieldAnnotation.getProperty("name", String.class);
                    ASTType converter = checkDefault(fieldAnnotation.getProperty("converter", ASTType.class), EMPTY_CONVERTER_TYPE);

                    ASTField astField = ASTUtils.getInstance().findField(containingType, fieldName);

                    if(astField != null) {
                        if (!fields.containsKey(fieldName)) {
                            fields.put(fieldName, new ArrayList<ASTReference<ASTField>>());
                        }
                        fields.get(fieldName).add(new ASTReference<ASTField>(astField, converter));
                    }
                }
            }
        }
        return fields;
    }

    private Map<String, List<ASTReference<ASTMethod>>> findConfigurationReadMethods(ASTAnnotation configuration, ASTType hierarchyLoop) {
        Map<String, List<ASTReference<ASTMethod>>> methods = new HashMap<String, List<ASTReference<ASTMethod>>>();
        if(configuration != null) {
            ASTAnnotation[] methodAnnotations = configuration.getProperty("methods", ASTAnnotation[].class);
            for (ASTAnnotation methodAnnotation : methodAnnotations) {
                ASTType containingType = methodAnnotation.getProperty("type", ASTType.class);
                if(containingType.equals(hierarchyLoop)) {
                    String methodName = methodAnnotation.getProperty("name", String.class);
                    ASTType[] methodParameters = methodAnnotation.getProperty("parameters", ASTType[].class);
                    ASTType converter = checkDefault(methodAnnotation.getProperty("converter", ASTType.class), EMPTY_CONVERTER_TYPE);

                    ASTMethod astMethod = ASTUtils.getInstance().findMethod(containingType, methodName, methodParameters);

                    if(astMethod != null) {
                        if (astMethod.getParameters().size() == 0 && !astMethod.getReturnType().equals(ASTVoidType.VOID)) {
                            String propertyName = getPropertyName(astMethod.getName());
                            if (!methods.containsKey(propertyName)) {
                                methods.put(propertyName, new ArrayList<ASTReference<ASTMethod>>());
                            }
                            methods.get(propertyName).add(new ASTReference<ASTMethod>(astMethod, converter));
                        }
                    }
                }
            }
        }
        return methods;
    }

    private Map<String, List<ASTReference<ASTMethod>>> findConfigurationWriteMethods(ASTAnnotation configuration, ASTType hierarchyLoop) {
        Map<String, List<ASTReference<ASTMethod>>> methods = new HashMap<String, List<ASTReference<ASTMethod>>>();
        if(configuration != null) {
            ASTAnnotation[] methodAnnotations = configuration.getProperty("methods", ASTAnnotation[].class);
            for (ASTAnnotation methodAnnotation : methodAnnotations) {
                ASTType containingType = methodAnnotation.getProperty("type", ASTType.class);
                if(containingType.equals(hierarchyLoop)) {
                    String methodName = methodAnnotation.getProperty("name", String.class);
                    ASTType[] methodParameters = methodAnnotation.getProperty("parameters", ASTType[].class);
                    ASTType converter = checkDefault(methodAnnotation.getProperty("converter", ASTType.class), EMPTY_CONVERTER_TYPE);

                    ASTMethod astMethod = ASTUtils.getInstance().findMethod(containingType, methodName, methodParameters);

                    if(astMethod != null) {
                        if (astMethod.getParameters().size() == 1 && astMethod.getReturnType().equals(ASTVoidType.VOID)) {
                            String propertyName = getPropertyName(astMethod.getName());
                            if (!methods.containsKey(propertyName)) {
                                methods.put(propertyName, new ArrayList<ASTReference<ASTMethod>>());
                            }
                            methods.get(propertyName).add(new ASTReference<ASTMethod>(astMethod, converter));
                        }
                    }
                }
            }
        }
        return methods;
    }

    private void validateConfiguration(ASTType type, ASTAnnotation configuration) {

        if(configuration == null){
            return;
        }

        ASTAnnotation constructor = configuration.getProperty("constructor", ASTAnnotation.class);
        ASTType[] parameters = constructor.getProperty("value", ASTType[].class);
        if(parameters.length > 0) {
            if (!ASTUtils.getInstance().constructorExists(type, parameters)) {
                validator.error("Unable to find constructor on " + type + " with parameters " + Joiner.on(" ").join(parameters))
                        .element(type)
                        .annotation(constructor)
                        .build();
            }
        }

        ASTAnnotation[] fields = configuration.getProperty("fields", ASTAnnotation[].class);
        for (ASTAnnotation field : fields) {
            ASTType containingType = field.getProperty("type", ASTType.class);
            String fieldName = field.getProperty("name", String.class);
            if(!type.extendsFrom(containingType)){
                validator.error("Configured field type " + containingType + " is not a superclass of " + type)
                        .element(type)
                        .annotation(field)
                        .build();
            }

            if(!ASTUtils.getInstance().fieldExists(containingType, fieldName)){
                validator.error("Unable to find field " + containingType + "." + fieldName)
                        .element(type)
                        .annotation(field)
                        .build();
            }
        }

        ASTAnnotation[] methods = configuration.getProperty("methods", ASTAnnotation[].class);
        for (ASTAnnotation method : methods) {
            ASTType containingType = method.getProperty("type", ASTType.class);
            String methodName = method.getProperty("name", String.class);
            ASTType[] methodParameters = method.getProperty("parameters", ASTType[].class);

            if(!type.extendsFrom(containingType)){
                validator.error("Configured method type " + containingType + " is not a superclass of " + type)
                        .element(type)
                        .annotation(method)
                        .build();
            }

            if(!ASTUtils.getInstance().methodExists(containingType, methodName, methodParameters)){
                validator.error("Unable to find method " + containingType + "." + methodName + "(" + Joiner.on(", ").join(methodParameters) + ")")
                        .element(type)
                        .annotation(method)
                        .build();
            }
        }
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
            if(!astMethod.isStatic() &&
                    !astMethod.isAnnotated(Transient.class) &&
                    !definedMethods.contains(new MethodSignature(astMethod)) &&
                    (declaredProperty == astMethod.isAnnotated(ParcelProperty.class) &&
                    isSetter(astMethod, declaredProperty))){
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
            if(!astMethod.isStatic() &&
                    !astMethod.isAnnotated(Transient.class) &&
                    !definedMethods.contains(new MethodSignature(astMethod)) &&
                    (declaredProperty == astMethod.isAnnotated(ParcelProperty.class) &&
                    isGetter(astMethod, declaredProperty))){
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
            if(!astField.isStatic() &&
                    !astField.isAnnotated(Transient.class) &&
                    !astField.isTransient() &&
                    (declaredProperty == astField.isAnnotated(ParcelProperty.class))){
                String name = astField.getName();
                ASTType converter = null;
                if(astField.isAnnotated(ParcelProperty.class)){
                    name = astField.getAnnotation(ParcelProperty.class).value();
                }
                if(astField.isAnnotated(ParcelPropertyConverter.class)){
                    ASTAnnotation converterAnnotation = astField.getASTAnnotation(ParcelPropertyConverter.class);
                    converter = checkDefault(converterAnnotation.getProperty("value", ASTType.class), EMPTY_CONVERTER_TYPE);
                }
                if(!fields.containsKey(name)){
                    fields.put(name, new ArrayList<ASTReference<ASTField>>());
                }
                fields.get(name).add(new ASTReference<ASTField>(astField, converter));
            }
        }

        return fields;
    }

    public Set<ASTConstructor> findConstructors(ASTConstructor configuredConstructor, ASTType astType, boolean includeEmptyBeanConstructor){
        Set<ASTConstructor> constructorResult = new HashSet<ASTConstructor>();
        for(ASTConstructor constructor : astType.getConstructors()){
            if(constructor.isAnnotated(ParcelConstructor.class)){
                constructorResult.add(constructor);
            }
        }
        if(configuredConstructor != null){
            constructorResult.add(configuredConstructor);
        }
        if(!constructorResult.isEmpty()){
            return constructorResult;
        }
        //if none are found, then try to find empty bean constructor
        if(includeEmptyBeanConstructor && constructorResult.isEmpty()){
            for(ASTConstructor constructor : astType.getConstructors()){
                if(constructor.getParameters().isEmpty()){
                    constructorResult.add(constructor);
                }
            }
        }
        return constructorResult;
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

    private void validateType(ASTType type, ASTBase mutator, String where){
        if(!generatorsProvider.get().matches(type)) {
            validator.error("Unable to find read/write generator for type " + type + " for " + where)
                    .element(mutator)
                    .build();
        }
    }

    private boolean isGetter(ASTMethod astMethod, boolean ignoreModifier) {
        return astMethod.getParameters().size() == 0 &&
                (astMethod.getName().startsWith(GET) || astMethod.getName().startsWith(IS)) &&
                !astMethod.getReturnType().equals(ASTVoidType.VOID) &&
                (ignoreModifier || astMethod.getAccessModifier().equals(ASTAccessModifier.PUBLIC));
    }

    private boolean isSetter(ASTMethod astMethod, boolean ignoreModifier) {
        return astMethod.getParameters().size() == 1 &&
                astMethod.getName().startsWith(SET) &&
                astMethod.getReturnType().equals(ASTVoidType.VOID) &&
                (ignoreModifier || astMethod.getAccessModifier().equals(ASTAccessModifier.PUBLIC));
    }

    private String getPropertyName(ASTMethod astMethod) {
        String methodName = astMethod.getName();

        if(astMethod.isAnnotated(ParcelProperty.class)){
            return astMethod.getAnnotation(ParcelProperty.class).value();
        }

        String propertyName = getPropertyName(methodName);
        if(propertyName == null) {
            throw new TransfuseAnalysisException("Unable to convert Method name " + methodName);
        }
        return propertyName;
    }

    private String getPropertyName(String methodName){
        for (String prepend : PREPENDS) {
            if (methodName.startsWith(prepend)) {
                String name = methodName.substring(prepend.length());
                return name.substring(0, 1).toLowerCase(Locale.getDefault()) + name.substring(1);
            }
        }
        return null;
    }

    private ASTType getConverter(ASTMethod astMethod) {
        if(astMethod.isAnnotated(ParcelProperty.class) && astMethod.isAnnotated(ParcelPropertyConverter.class)) {
            return checkDefault(astMethod.getASTAnnotation(ParcelPropertyConverter.class).getProperty("value", ASTType.class), EMPTY_CONVERTER_TYPE);
        }
        return null;
    }

    private ASTType getConverter(ASTAnnotation astAnnotation) {
        if(astAnnotation != null){
            return checkDefault(astAnnotation.getProperty("converter", ASTType.class), EMPTY_CONVERTER_TYPE);
        }
        return null;
    }

    private <T> Map<String, List<T>> combine(Map<String, List<T>>... input){
        Map<String, List<T>> result = new HashMap<String, List<T>>();

        for(Map<String, List<T>> value : input) {
            for (Map.Entry<String, List<T>> twoEntry : value.entrySet()) {
                if (!result.containsKey(twoEntry.getKey())) {
                    result.put(twoEntry.getKey(), twoEntry.getValue());
                } else {
                    result.get(twoEntry.getKey()).addAll(twoEntry.getValue());
                }
            }
        }
        return result;
    }

    public static <T> T checkDefault(T input, T defaultValue) {
        if (input != null && input.equals(defaultValue)) {
            return null;
        }
        return input;
    }

    private static <T> T defaultValue(T value, T defaultValue){
        if(value == null){
            return defaultValue;
        }
        return value;
    }
}
