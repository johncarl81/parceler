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

import com.sun.codemodel.*;
import org.androidtransfuse.TransfuseAnalysisException;
import org.androidtransfuse.adapter.*;
import org.androidtransfuse.gen.ClassGenerationUtil;
import org.androidtransfuse.gen.ClassNamer;
import org.androidtransfuse.gen.InvocationBuilder;
import org.androidtransfuse.gen.UniqueVariableNamer;
import org.androidtransfuse.model.TypedExpression;
import org.parceler.*;
import org.parceler.internal.generator.ConverterWrapperReadWriteGenerator;
import org.parceler.internal.generator.EnumReadWriteGenerator;
import org.parceler.internal.generator.ParcelReadWriteGenerator;
import org.parceler.internal.generator.ReadWriteGenerator;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * @author John Ericksen
 */
public class ParcelableGenerator {

    public static final String WRITE_METHOD = "write";
    public static final String READ_METHOD = "read";
    private static final String ANDROID_PARCEL = "android.os.Parcel";
    private static final String CREATOR_CLASS_NAME = "CREATOR";
    private static final String CREATE_FROM_PARCEL = "createFromParcel";
    private static final String NEW_ARRAY = "newArray";
    private static final String WRITE_TO_PARCEL = "writeToParcel";
    private static final String DESCRIBE_CONTENTS = "describeContents";

    private final JCodeModel codeModel;
    private final UniqueVariableNamer variableNamer;
    private final ClassNamer classNamer;
    private final ClassGenerationUtil generationUtil;
    private final ReadReferenceVisitor readFromParcelVisitor;
    private final WriteReferenceVisitor writeToParcelVisitor;
    private final InvocationBuilder invocationBuilder;
    private final Generators generators;
    private final EnumReadWriteGenerator enumReadWriteGenerator;
    private final ParcelReadWriteGenerator parcelReadWriteGenerator;


    @Inject
    public ParcelableGenerator(JCodeModel codeModel,
                               UniqueVariableNamer variableNamer,
                               ClassNamer classNamer,
                               ClassGenerationUtil generationUtil,
                               ReadReferenceVisitor readFromParcelVisitor,
                               WriteReferenceVisitor writeToParcelVisitor,
                               InvocationBuilder invocationBuilder,
                               Generators generators,
                               EnumReadWriteGenerator enumReadWriteGenerator, ParcelReadWriteGenerator parcelReadWriteGenerator) {
        this.codeModel = codeModel;
        this.variableNamer = variableNamer;
        this.classNamer = classNamer;
        this.generationUtil = generationUtil;
        this.readFromParcelVisitor = readFromParcelVisitor;
        this.writeToParcelVisitor = writeToParcelVisitor;
        this.invocationBuilder = invocationBuilder;
        this.generators = generators;
        this.enumReadWriteGenerator = enumReadWriteGenerator;
        this.parcelReadWriteGenerator = parcelReadWriteGenerator;
    }

    public JDefinedClass generateParcelable(final ASTType type, ParcelableDescriptor parcelableDescriptor) {
        try {
            JType inputType = generationUtil.ref(type);

            JDefinedClass parcelableClass = generationUtil.defineClass(ClassNamer.className(type).append(Parcels.IMPL_EXT).build());
            parcelableClass._implements(generationUtil.ref("android.os.Parcelable"))
                    ._implements(generationUtil.ref(ParcelWrapper.class).narrow(inputType));

            //wrapped @Parcel
            JFieldVar wrapped = parcelableClass.field(JMod.PRIVATE, inputType, variableNamer.generateName(type));

            //writeToParcel(android.os.Parcel,int)
            JMethod writeToParcelMethod = parcelableClass.method(JMod.PUBLIC, codeModel.VOID, WRITE_TO_PARCEL);
            writeToParcelMethod.annotate(Override.class);
            JVar wtParcelParam = writeToParcelMethod.param(generationUtil.ref("android.os.Parcel"), variableNamer.generateName("android.os.Parcel"));
            JVar flags = writeToParcelMethod.param(codeModel.INT, "flags");

            ReadWriteGenerator rootGenerator = getRootReadWriteGenerator(type);

            JBlock writeToParcelMethodBody = writeToParcelMethod.body();
            buildWriteMethod(parcelableClass, writeToParcelMethodBody, wtParcelParam, flags, type, wrapped, parcelableDescriptor.getParcelConverterType(), rootGenerator, JExpr._new(codeModel.ref(IdentityCollection.class)));

            //@Parcel input
            JMethod inputConstructor = parcelableClass.constructor(JMod.PUBLIC);
            JVar inputParam = inputConstructor.param(inputType, variableNamer.generateName(type));
            inputConstructor.body().assign(wrapped, inputParam);

            //describeContents()
            JMethod describeContentsMethod = parcelableClass.method(JMod.PUBLIC, codeModel.INT, DESCRIBE_CONTENTS);
            describeContentsMethod.annotate(Override.class);
            describeContentsMethod.body()._return(JExpr.lit(0));

            //ParcelWrapper.getParcel()
            JMethod getWrappedMethod = parcelableClass.method(JMod.PUBLIC, inputType, ParcelWrapper.GET_PARCEL);
            getWrappedMethod.annotate(Override.class);
            getWrappedMethod.body()._return(wrapped);

            //public static final CREATOR = ...
            JDefinedClass creatorClass = parcelableClass._class(JMod.PUBLIC | JMod.STATIC | JMod.FINAL, classNamer.numberedClassName(new ASTStringType("android.os.Parcelable.Creator")).build().getClassName());

            creatorClass._implements(generationUtil.ref("android.os.Parcelable.Creator").narrow(parcelableClass));

            //createFromParcel method
            JMethod createFromParcelMethod = creatorClass.method(JMod.PUBLIC, parcelableClass, CREATE_FROM_PARCEL);
            createFromParcelMethod.annotate(Override.class);
            JVar cfpParcelParam = createFromParcelMethod.param(generationUtil.ref("android.os.Parcel"), variableNamer.generateName(generationUtil.ref("android.os.Parcel")));

            createFromParcelMethod.body()._return(
                    JExpr._new(parcelableClass).arg(
                            buildReadMethod(cfpParcelParam, parcelableClass, type, parcelableDescriptor.getParcelConverterType(), rootGenerator, JExpr._new(codeModel.ref(IdentityCollection.class)))));

            //newArray method
            JMethod newArrayMethod = creatorClass.method(JMod.PUBLIC, parcelableClass.array(), NEW_ARRAY);
            newArrayMethod.annotate(Override.class);
            JVar sizeParam = newArrayMethod.param(codeModel.INT, "size");

            newArrayMethod.body()._return(JExpr.newArray(parcelableClass, sizeParam));

            //public static final Creator<type> CREATOR
            JFieldVar creatorField = parcelableClass.field(JMod.PUBLIC | JMod.STATIC | JMod.FINAL, creatorClass, CREATOR_CLASS_NAME, JExpr._new(creatorClass));
            creatorField.annotate(SuppressWarnings.class).param("value", "UnusedDeclaration");

            return parcelableClass;
        } catch (JClassAlreadyExistsException e) {
            throw new TransfuseAnalysisException("Class Already Exists: " + ClassNamer.className(type).append(Parcels.IMPL_EXT).build(), e);
        }
    }

    public void buildParcelRead(ParcelableDescriptor parcelableDescriptor, JDefinedClass parcelableClass, JVar wrapped, ASTType type, JType inputType, JVar identity, JVar parcelParam, JBlock readFromParcelBody, JVar writeIdentityMap) {
        if (parcelableDescriptor.getParcelConverterType() == null) {

            //constructor
            ConstructorReference constructorPair = parcelableDescriptor.getConstructorPair();
            if(constructorPair == null){
                JInvocation constructorInvocation = JExpr._new(inputType);
                readFromParcelBody.assign(wrapped, constructorInvocation);
            }
            else {
                JVar reservationId = readFromParcelBody.decl(codeModel.INT, variableNamer.generateName("reservation"), writeIdentityMap.invoke("reserve"));
                if(constructorPair.getConstructor() != null){
                    buildReadFromParcelConstructor(parcelableClass, readFromParcelBody, wrapped, constructorPair, type, parcelParam, identity, writeIdentityMap);
                }
                else if(constructorPair.getFactoryMethod() != null){
                    buildReadFromParcelFactoryMethod(parcelableClass, readFromParcelBody, wrapped, constructorPair, type, parcelParam, identity, writeIdentityMap);
                }
                //add to identity map
                readFromParcelBody.add(writeIdentityMap.invoke("put").arg(reservationId).arg(wrapped));
            }
            //field
            for (ReferencePair<FieldReference> fieldPair : parcelableDescriptor.getFieldPairs()) {
                buildReadFromParcel(parcelableClass, readFromParcelBody, type, wrapped, fieldPair.getReference(), parcelParam, fieldPair.getConverter(), identity, writeIdentityMap);
            }
            //method
            for (ReferencePair<MethodReference> methodPair : parcelableDescriptor.getMethodPairs()) {
                buildReadFromParcel(parcelableClass, readFromParcelBody, type, wrapped, methodPair.getReference(), parcelParam, methodPair.getConverter(), identity, writeIdentityMap);
            }
        } else {
            JClass converterType = generationUtil.ref(parcelableDescriptor.getParcelConverterType());
            JFieldVar converterField = parcelableClass.field(JMod.PRIVATE, converterType,
                    variableNamer.generateName(parcelableDescriptor.getParcelConverterType()), JExpr._new(converterType));

            readFromParcelBody.assign(wrapped, JExpr.invoke(converterField, ParcelConverter.CONVERT_FROM_PARCEL).arg(parcelParam));
        }

        for (ASTMethod unwrapMethod : parcelableDescriptor.getUnwrapCallbacks()) {
            readFromParcelBody.add(invocationBuilder.buildMethodCall(new ASTJDefinedClassType(parcelableClass), type, unwrapMethod, Collections.<JExpression>emptyList(), new TypedExpression(type, wrapped)));
        }
    }

    public void buildParcelWrite(ParcelableDescriptor parcelableDescriptor, JDefinedClass parcelableClass, JExpression wrapped, ASTType type, JExpression wtParcelParam, JVar flags, JBlock writeToParcelBody, JVar writeIdentityMap){

        for (ASTMethod wrapMethod : parcelableDescriptor.getWrapCallbacks()) {
            writeToParcelBody.add(invocationBuilder.buildMethodCall(new ASTJDefinedClassType(parcelableClass), type, wrapMethod, Collections.<JExpression>emptyList(), new TypedExpression(type, wrapped)));
        }

        if (parcelableDescriptor.getParcelConverterType() == null) {

            //constructor
            ConstructorReference constructorPair = parcelableDescriptor.getConstructorPair();
            if(constructorPair != null){
                if(constructorPair.getConstructor() != null){
                    for(ASTParameter parameter : constructorPair.getConstructor().getParameters()){
                        AccessibleReference reference = constructorPair.getWriteReference(parameter);
                        ASTType converter = constructorPair.getConverters().containsKey(parameter) ? constructorPair.getConverters().get(parameter) : null;
                        buildWriteToParcel(parcelableClass, writeToParcelBody, wtParcelParam, flags, reference, type, wrapped, converter, writeIdentityMap);
                    }
                }
                else if(constructorPair.getFactoryMethod() != null){
                    for(ASTParameter parameter : constructorPair.getFactoryMethod().getParameters()){
                        AccessibleReference reference = constructorPair.getWriteReference(parameter);
                        ASTType converter = constructorPair.getConverters().containsKey(parameter) ? constructorPair.getConverters().get(parameter) : null;
                        buildWriteToParcel(parcelableClass, writeToParcelBody, wtParcelParam, flags, reference, type, wrapped, converter, writeIdentityMap);
                    }
                }
            }
            //field
            for (ReferencePair<FieldReference> fieldPair : parcelableDescriptor.getFieldPairs()) {
                buildWriteToParcel(parcelableClass, writeToParcelBody, wtParcelParam, flags, fieldPair.getAccessor(), type, wrapped, fieldPair.getConverter(), writeIdentityMap);
            }
            //method
            for (ReferencePair<MethodReference> methodPair : parcelableDescriptor.getMethodPairs()) {
                buildWriteToParcel(parcelableClass, writeToParcelBody, wtParcelParam, flags, methodPair.getAccessor(), type, wrapped, methodPair.getConverter(), writeIdentityMap);
            }
        } else {
            JClass converterType = generationUtil.ref(parcelableDescriptor.getParcelConverterType());
            JFieldVar converterField = parcelableClass.field(JMod.PRIVATE, converterType,
                    variableNamer.generateName(parcelableDescriptor.getParcelConverterType()), JExpr._new(converterType));

            writeToParcelBody.invoke(converterField, ParcelConverter.CONVERT_TO_PARCEL).arg(wrapped).arg(wtParcelParam);
        }
    }

    private void buildReadFromParcel(JDefinedClass parcelableClass, JBlock parcelConstructorBody, ASTType type, JVar wrapped, AccessibleReference propertyAccessor, JVar parcelParam, ASTType converter, JVar identity, JVar writeIdentityMap) {
        //invocation
        propertyAccessor.accept(readFromParcelVisitor,
                new ReadContext(new ASTJDefinedClassType(parcelableClass),
                        parcelConstructorBody,
                        new TypedExpression(type, wrapped),
                        buildReadFromParcelExpression(parcelConstructorBody, parcelParam, parcelableClass, propertyAccessor.getType(), converter, identity, writeIdentityMap)));
    }

    private void buildReadFromParcelFactoryMethod(JDefinedClass parcelableClass, JBlock parcelConstructorBody, JVar wrapped, ConstructorReference propertyAccessor, ASTType wrappedType, JVar parcelParam, JVar identity, JVar writeIdentityMap){

        ASTMethod factoryMethod = propertyAccessor.getFactoryMethod();
        Map<ASTParameter, ASTType> converters = propertyAccessor.getConverters();

        JInvocation invocation = generationUtil.ref(wrappedType).staticInvoke(factoryMethod.getName());

        for (ASTParameter parameter : factoryMethod.getParameters()) {
            ASTType converter = converters.containsKey(parameter) ? converters.get(parameter) : null;
            JVar var = parcelConstructorBody.decl(generationUtil.ref(parameter.getASTType()),
                    variableNamer.generateName(parameter.getASTType()),
                    buildReadFromParcelExpression(parcelConstructorBody, parcelParam, parcelableClass, parameter.getASTType(), converter, identity, writeIdentityMap).getExpression());
            invocation.arg(var);
        }

        parcelConstructorBody.assign(wrapped, invocation);
    }

    private void buildReadFromParcelConstructor(JDefinedClass parcelableClass, JBlock parcelConstructorBody, JVar wrapped, ConstructorReference propertyAccessor, ASTType wrappedType, JVar parcelParam, JVar identity, JVar writeIdentityMap){

        ASTConstructor constructor = propertyAccessor.getConstructor();
        List<JExpression> inputExpression = new ArrayList<JExpression>();
        Map<ASTParameter, ASTType> converters = propertyAccessor.getConverters();

        for (ASTParameter parameter : constructor.getParameters()) {
            ASTType converter = converters.containsKey(parameter) ? converters.get(parameter) : null;
            JVar var = parcelConstructorBody.decl(generationUtil.ref(parameter.getASTType()),
                    variableNamer.generateName(parameter.getASTType()),
                    buildReadFromParcelExpression(parcelConstructorBody, parcelParam, parcelableClass, parameter.getASTType(), converter, identity, writeIdentityMap).getExpression());
            inputExpression.add(var);
        }

        parcelConstructorBody.assign(wrapped, invocationBuilder.buildConstructorCall(new ASTJDefinedClassType(parcelableClass), constructor, wrappedType, inputExpression));
    }

    private TypedExpression buildReadFromParcelExpression(JBlock body, JVar parcelParam, JDefinedClass parcelableClass, ASTType type, ASTType converter, JVar identity, JVar writeIdentityMap){
        return buildReadFromParcelExpression(body, parcelParam, parcelableClass, type, converter, null, identity, writeIdentityMap);
    }

    private TypedExpression buildReadFromParcelExpression(JBlock body, JVar parcelParam, JDefinedClass parcelableClass, ASTType type, ASTType converter, ReadWriteGenerator overrideGenerator, JVar identity, JVar readIdentityMap){
        JClass returnJClassRef = generationUtil.ref(type);

        ReadWriteGenerator generator;
        if(converter != null){
            generator = new ConverterWrapperReadWriteGenerator(generationUtil.ref(converter));
        }
        else if(overrideGenerator != null) {
            generator = overrideGenerator;
        }
        else{
            generator = generators.getGenerator(type);
        }

        return new TypedExpression(type, generator.generateReader(body, parcelParam, type, returnJClassRef, parcelableClass, identity, readIdentityMap));
    }

    private void buildWriteToParcel(JDefinedClass parcelableClass, JBlock body, JExpression parcel, JVar flags, AccessibleReference reference, ASTType wrappedType, JExpression wrapped, ASTType converter, JVar writeIdentityMap) {
        ASTType type = reference.getType();
        JExpression getExpression = reference.accept(writeToParcelVisitor, new WriteContext(new ASTJDefinedClassType(parcelableClass), new TypedExpression(wrappedType, wrapped)));

        buildWriteToParcelExpression(parcelableClass, body, parcel, flags, type, getExpression, converter, null, writeIdentityMap);
    }

    private void buildWriteToParcelExpression(JDefinedClass parcelableClass, JBlock body, JExpression parcel, JVar flags, ASTType type, JExpression targetExpression, ASTType converter, ReadWriteGenerator overrideGenerator, JVar writeIdentitySet) {

        ReadWriteGenerator generator;
        if(converter != null){
            generator = new ConverterWrapperReadWriteGenerator(generationUtil.ref(converter));
        }
        else if(overrideGenerator != null) {
            generator = overrideGenerator;
        }
        else{
            generator = generators.getGenerator(type);
        }

        generator.generateWriter(body, parcel, flags, type, targetExpression, parcelableClass, writeIdentitySet);
    }

    private ReadWriteGenerator getRootReadWriteGenerator(ASTType type) {
        if(type.isEnum()){
            return enumReadWriteGenerator;
        }
        else {
            return parcelReadWriteGenerator;
        }
    }

    public JExpression buildReadMethod(JVar inputParcelParam, JDefinedClass parcelableClass, ASTType type, ASTType converter, ReadWriteGenerator overrideGenerator, JExpression readIdentityMap) {
        JType parcelType = generationUtil.ref(ANDROID_PARCEL);
        //write method
        JMethod readMethod = parcelableClass.method(JMod.PUBLIC | JMod.STATIC, generationUtil.ref(type), READ_METHOD);
        JBlock readMethodBody = readMethod.body();

        JVar parcelParam = readMethod.param(parcelType, variableNamer.generateName(parcelType));
        JVar identityParam = readMethod.param(codeModel.ref(IdentityCollection.class), variableNamer.generateName("identityMap"));

        JVar identity = readMethodBody.decl(codeModel.INT, variableNamer.generateName("identity"), parcelParam.invoke("readInt"));

        JConditional containsKeyConditional = readMethodBody._if(identityParam.invoke("containsKey").arg(identity));

        JBlock containsBlock = containsKeyConditional._then();

        containsBlock._if(identityParam.invoke("isReserved").arg(identity))._then()._throw(JExpr._new(generationUtil.ref(ParcelerRuntimeException.class))
                .arg("An instance loop was detected whild building Parcelable and deseralization cannot continue.  This error is most likely due to using @ParcelConstructor or @ParcelFactory."));
        containsBlock._return(identityParam.invoke("get").arg(identity));

        JBlock doesntContainBlock = containsKeyConditional._else();

        doesntContainBlock._return(buildReadFromParcelExpression(doesntContainBlock, parcelParam, parcelableClass, type, converter, overrideGenerator, identity, identityParam).getExpression());

        return JExpr.invoke(readMethod).arg(inputParcelParam).arg(readIdentityMap);
    }

    public void buildWriteMethod(JDefinedClass parcelableClass, JBlock body, JExpression parcel, JVar flags, ASTType type, JExpression targetExpression, ASTType converter, ReadWriteGenerator overrideGenerator, JExpression writeIdentitySet) {

        JType parcelType = generationUtil.ref(ANDROID_PARCEL);
        //write method
        JType inputType = generationUtil.ref(type);
        JMethod writeMethod = parcelableClass.method(JMod.PUBLIC | JMod.STATIC, Void.TYPE, WRITE_METHOD);
        JBlock writeMethodBody = writeMethod.body();

        JVar writeInputVar = writeMethod.param(inputType, variableNamer.generateName(inputType));
        JVar parcelParam = writeMethod.param(parcelType, variableNamer.generateName(parcelType));
        JVar flagsParam = writeMethod.param(int.class, variableNamer.generateName("flags"));
        JVar identityParam = writeMethod.param(codeModel.ref(IdentityCollection.class), variableNamer.generateName("identityMap"));

        JConditional containsValueConditional = writeMethodBody._if(identityParam.invoke("containsValue").arg(writeInputVar));

        containsValueConditional._then().invoke(parcelParam, "writeInt").arg(identityParam.invoke("getKey").arg(writeInputVar));

        JBlock notContainsBlock = containsValueConditional._else();

        notContainsBlock.invoke(parcelParam, "writeInt").arg(identityParam.invoke("put").arg(writeInputVar));

        buildWriteToParcelExpression(parcelableClass, notContainsBlock, parcelParam, flagsParam, type, writeInputVar, converter, overrideGenerator, identityParam);

        //invoke this generated method
        body.invoke(writeMethod).arg(targetExpression).arg(parcel).arg(flags).arg(writeIdentitySet);
    }
}
