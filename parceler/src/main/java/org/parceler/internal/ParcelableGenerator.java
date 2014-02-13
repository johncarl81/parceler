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

import com.sun.codemodel.*;
import org.androidtransfuse.TransfuseAnalysisException;
import org.androidtransfuse.adapter.ASTConstructor;
import org.androidtransfuse.adapter.ASTParameter;
import org.androidtransfuse.adapter.ASTStringType;
import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.gen.ClassGenerationUtil;
import org.androidtransfuse.gen.ClassNamer;
import org.androidtransfuse.gen.InvocationBuilder;
import org.androidtransfuse.gen.UniqueVariableNamer;
import org.androidtransfuse.model.TypedExpression;
import org.parceler.ParcelConverter;
import org.parceler.ParcelWrapper;
import org.parceler.Parcels;
import org.parceler.internal.generator.ConverterWrapperReadWriteGenerator;
import org.parceler.internal.generator.ReadWriteGenerator;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * @author John Ericksen
 */
public class ParcelableGenerator {

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


    @Inject
    public ParcelableGenerator(JCodeModel codeModel,
                               UniqueVariableNamer variableNamer,
                               ClassNamer classNamer,
                               ClassGenerationUtil generationUtil,
                               ReadReferenceVisitor readFromParcelVisitor,
                               WriteReferenceVisitor writeToParcelVisitor,
                               InvocationBuilder invocationBuilder,
                               Generators generators) {
        this.codeModel = codeModel;
        this.variableNamer = variableNamer;
        this.classNamer = classNamer;
        this.generationUtil = generationUtil;
        this.readFromParcelVisitor = readFromParcelVisitor;
        this.writeToParcelVisitor = writeToParcelVisitor;
        this.invocationBuilder = invocationBuilder;
        this.generators = generators;
    }

    public JDefinedClass generateParcelable(ASTType type, ParcelableDescriptor parcelableDescriptor) {
        try {
            JType inputType = generationUtil.ref(type);

            JDefinedClass parcelableClass = generationUtil.defineClass(ClassNamer.className(type).append(Parcels.IMPL_EXT).build());
            parcelableClass._implements(generationUtil.ref("android.os.Parcelable"))
                    ._implements(generationUtil.ref(ParcelWrapper.class).narrow(inputType));

            //wrapped @Parcel
            JFieldVar wrapped = parcelableClass.field(JMod.PRIVATE, inputType, variableNamer.generateName(type));

            //Parcel constructor
            JMethod parcelConstructor = parcelableClass.constructor(JMod.PUBLIC);
            JVar parcelParam = parcelConstructor.param(generationUtil.ref("android.os.Parcel"), variableNamer.generateName("android.os.Parcel"));
            JBlock parcelConstructorBody = parcelConstructor.body();

            //writeToParcel(android.os.Parcel,int)
            JMethod writeToParcelMethod = parcelableClass.method(JMod.PUBLIC, codeModel.VOID, WRITE_TO_PARCEL);
            writeToParcelMethod.annotate(Override.class);
            JVar wtParcelParam = writeToParcelMethod.param(generationUtil.ref("android.os.Parcel"), variableNamer.generateName("android.os.Parcel"));
            JVar flags = writeToParcelMethod.param(codeModel.INT, "flags");

            if (parcelableDescriptor.getParcelConverterType() == null) {

                //constructor
                ConstructorReference constructorPair = parcelableDescriptor.getConstructorPair();
                if(constructorPair == null){
                    JInvocation constructorInvocation = JExpr._new(inputType);
                    parcelConstructorBody.assign(wrapped, constructorInvocation);
                }
                else {
                    buildReadFromParcel(parcelableClass, parcelConstructorBody, wrapped, constructorPair, type, parcelParam);
                    for(ASTParameter parameter : constructorPair.getConstructor().getParameters()){
                        AccessibleReference reference = constructorPair.getWriteReference(parameter);
                        ASTType converter = constructorPair.getConverters().containsKey(parameter) ? constructorPair.getConverters().get(parameter) : null;
                        buildWriteToParcel(writeToParcelMethod.body(), wtParcelParam, flags, reference, type, wrapped, converter);
                    }
                }
                //field
                for (ReferencePair<FieldReference> fieldPair : parcelableDescriptor.getFieldPairs()) {
                    buildReadFromParcel(parcelableClass, parcelConstructorBody, type, wrapped, fieldPair.getReference(), parcelParam, fieldPair.getConverter());
                    buildWriteToParcel(writeToParcelMethod.body(), wtParcelParam, flags, fieldPair.getAccessor(), type, wrapped, fieldPair.getConverter());
                }
                //method
                for (ReferencePair<MethodReference> methodPair : parcelableDescriptor.getMethodPairs()) {
                    buildReadFromParcel(parcelableClass, parcelConstructorBody, type, wrapped, methodPair.getReference(), parcelParam, methodPair.getConverter());
                    buildWriteToParcel(writeToParcelMethod.body(), wtParcelParam, flags, methodPair.getAccessor(), type, wrapped, methodPair.getConverter());
                }
            } else {
                JClass converterType = generationUtil.ref(parcelableDescriptor.getParcelConverterType());
                JFieldVar converterField = parcelableClass.field(JMod.PRIVATE, converterType,
                        variableNamer.generateName(parcelableDescriptor.getParcelConverterType()), JExpr._new(converterType));

                parcelConstructorBody.assign(wrapped, JExpr.invoke(converterField, ParcelConverter.CONVERT_FROM_PARCEL).arg(parcelParam));

                writeToParcelMethod.body().invoke(converterField, ParcelConverter.CONVERT_TO_PARCEL).arg(wrapped).arg(wtParcelParam);
            }

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
            JDefinedClass creatorClass = parcelableClass._class(JMod.PRIVATE | JMod.STATIC | JMod.FINAL, classNamer.numberedClassName(new ASTStringType("android.os.Parcelable.Creator")).build().getClassName());

            creatorClass._implements(generationUtil.ref("android.os.Parcelable.Creator").narrow(parcelableClass));

            //createFromParcel method
            JMethod createFromParcelMethod = creatorClass.method(JMod.PUBLIC, parcelableClass, CREATE_FROM_PARCEL);
            createFromParcelMethod.annotate(Override.class);
            JVar cfpParcelParam = createFromParcelMethod.param(generationUtil.ref("android.os.Parcel"), variableNamer.generateName(generationUtil.ref("android.os.Parcel")));

            createFromParcelMethod.body()._return(JExpr._new(parcelableClass).arg(cfpParcelParam));

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

    private void buildReadFromParcel(JDefinedClass parcelableClass, JBlock parcelConstructorBody, ASTType type, JFieldVar wrapped, MethodReference propertyAccessor, JVar parcelParam, ASTType converter) {
        //invocation
        propertyAccessor.accept(readFromParcelVisitor,
                new ReadContext(parcelConstructorBody,
                        new TypedExpression(type, wrapped),
                        buildReadFromParcelExpression(parcelConstructorBody, parcelParam, parcelableClass, propertyAccessor.getType(), converter)));
    }

    private void buildReadFromParcel(JDefinedClass parcelableClass, JBlock parcelConstructorBody, ASTType type, JFieldVar wrapped, FieldReference propertyAccessor, JVar parcelParam, ASTType converter) {
        //invocation
        propertyAccessor.accept(readFromParcelVisitor,
                new ReadContext(parcelConstructorBody,
                        new TypedExpression(type, wrapped),
                        buildReadFromParcelExpression(parcelConstructorBody, parcelParam, parcelableClass, propertyAccessor.getType(), converter)));
    }

    private void buildReadFromParcel(JDefinedClass parcelableClass, JBlock parcelConstructorBody, JFieldVar wrapped, ConstructorReference propertyAccessor, ASTType wrappedType, JVar parcelParam){

        ASTConstructor constructor = propertyAccessor.getConstructor();
        List<ASTType> parameterTypes = new ArrayList<ASTType>();
        List<JExpression> inputExpression = new ArrayList<JExpression>();
        Map<ASTParameter, ASTType> converters = propertyAccessor.getConverters();

        for (ASTParameter parameter : constructor.getParameters()) {
            parameterTypes.add(parameter.getASTType());
            ASTType converter = converters.containsKey(parameter) ? converters.get(parameter) : null;
            inputExpression.add(buildReadFromParcelExpression(parcelConstructorBody, parcelParam, parcelableClass, parameter.getASTType(), converter).getExpression());
        }

        parcelConstructorBody.assign(wrapped, invocationBuilder.buildConstructorCall(constructor, wrappedType, inputExpression));
    }

    private TypedExpression buildReadFromParcelExpression(JBlock body, JVar parcelParam, JDefinedClass parcelableClass, ASTType type, ASTType converter){
        JClass returnJClassRef = generationUtil.ref(type);

        ReadWriteGenerator generator;
        if(converter != null){
            generator = new ConverterWrapperReadWriteGenerator(generationUtil.ref(converter));
        }
        else{
            generator = generators.getGenerator(type);
        }

        return new TypedExpression(type, generator.generateReader(body, parcelParam, type, returnJClassRef, parcelableClass));
    }

    private void buildWriteToParcel(JBlock body, JVar parcel, JVar flags, AccessibleReference reference, ASTType wrappedType, JFieldVar wrapped, ASTType converter) {
        ASTType type = reference.getType();
        JExpression getExpression = reference.accept(writeToParcelVisitor, new WriteContext(new TypedExpression(wrappedType, wrapped)));

        ReadWriteGenerator generator;
        if(converter != null){
            generator = new ConverterWrapperReadWriteGenerator(generationUtil.ref(converter));
        }
        else{
            generator = generators.getGenerator(type);
        }

        generator.generateWriter(body, parcel, flags, type, getExpression);
    }
}
