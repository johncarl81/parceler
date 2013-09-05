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

import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import com.sun.codemodel.*;
import org.androidtransfuse.TransfuseAnalysisException;
import org.androidtransfuse.adapter.ASTArrayType;
import org.androidtransfuse.adapter.ASTPrimitiveType;
import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.adapter.classes.ASTClassFactory;
import org.androidtransfuse.gen.ClassGenerationUtil;
import org.androidtransfuse.gen.ClassNamer;
import org.androidtransfuse.gen.UniqueVariableNamer;
import org.parceler.ParcelConverter;
import org.parceler.ParcelWrapper;
import org.parceler.Parcels;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.HashMap;
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
    public static final String WRAP_METHOD = "wrap";

    private final JCodeModel codeModel;
    private final UniqueVariableNamer variableNamer;
    private final ClassNamer classNamer;
    private final ASTClassFactory astClassFactory;
    private final ClassGenerationUtil generationUtil;
    private final ExternalParcelRepository externalParcelRepository;
    private final Map<ASTType, ReadWritePair> arrayParceableModfier = new HashMap<ASTType, ReadWritePair>();
    private final Map<ASTType, ReadWritePair> parceableModifier = new HashMap<ASTType, ReadWritePair>();
    private final Map<ASTType, ReadWritePair> classLoaderModifier = new HashMap<ASTType, ReadWritePair>();

    @Inject
    public ParcelableGenerator(JCodeModel codeModel, UniqueVariableNamer variableNamer, ClassNamer classNamer, ASTClassFactory astClassFactory, ClassGenerationUtil generationUtil, ExternalParcelRepository externalParcelRepository) {
        this.codeModel = codeModel;
        this.variableNamer = variableNamer;
        this.classNamer = classNamer;
        this.astClassFactory = astClassFactory;
        this.generationUtil = generationUtil;
        this.externalParcelRepository = externalParcelRepository;

        setup();
    }

    public JDefinedClass generateParcelable(ASTType type, ParcelableDescriptor parcelableDescriptor) {
        try {
            JType inputType = generationUtil.ref(type);

            JDefinedClass parcelableClass = generationUtil.defineClass(ClassNamer.className(type).append(Parcels.IMPL_EXT).build());
            parcelableClass._implements(Parcelable.class)
                    ._implements(codeModel.ref(ParcelWrapper.class).narrow(inputType));

            //wrapped @Parcel
            JFieldVar wrapped = parcelableClass.field(JMod.PRIVATE, inputType, variableNamer.generateName(type));

            //Parcel constructor
            JMethod parcelConstructor = parcelableClass.constructor(JMod.PUBLIC);
            JVar parcelParam = parcelConstructor.param(codeModel.ref(Parcel.class), variableNamer.generateName(Parcel.class));
            JBlock parcelConstructorBody = parcelConstructor.body();

            //writeToParcel(android.os.Parcel,int)
            JMethod writeToParcelMethod = parcelableClass.method(JMod.PUBLIC, codeModel.VOID, WRITE_TO_PARCEL);
            writeToParcelMethod.annotate(Override.class);
            JVar wtParcelParam = writeToParcelMethod.param(Parcel.class, variableNamer.generateName(Parcel.class));
            JVar flags = writeToParcelMethod.param(codeModel.INT, "flags");

            if (parcelableDescriptor.getParcelConverterType() == null) {

                parcelConstructorBody.assign(wrapped, JExpr._new(inputType));

                //read from parcel
                for (GetterSetterMethodPair propertyMutator : parcelableDescriptor.getGetterSetterPairs()) {
                    buildReadFromParcel(parcelableClass, parcelConstructorBody, wrapped, propertyMutator, parcelParam);
                }

                for (GetterSetterMethodPair propertyMutator : parcelableDescriptor.getGetterSetterPairs()) {
                    buildWriteToParcel(writeToParcelMethod.body(), wtParcelParam, flags, propertyMutator, wrapped);
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
            JDefinedClass creatorClass = parcelableClass._class(JMod.PRIVATE | JMod.STATIC | JMod.FINAL, classNamer.numberedClassName(Parcelable.Creator.class).build().getClassName());

            creatorClass._implements(codeModel.ref(Parcelable.Creator.class).narrow(parcelableClass));

            //createFromParcel method
            JMethod createFromParcelMethod = creatorClass.method(JMod.PUBLIC, parcelableClass, CREATE_FROM_PARCEL);
            createFromParcelMethod.annotate(Override.class);
            JVar cfpParcelParam = createFromParcelMethod.param(Parcel.class, variableNamer.generateName(Parcel.class));

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

    private void buildReadFromParcel(JDefinedClass parcelableClass, JBlock parcelConstructorBody, JFieldVar wrapped, GetterSetterMethodPair propertyGetter, JVar parcelParam) {
        ASTType returnType = propertyGetter.getGetter().getReturnType();
        JClass returnJClassRef = generationUtil.ref(returnType);
        if (parceableModifier.containsKey(returnType)) {
            parcelConstructorBody.invoke(wrapped, propertyGetter.getSetter().getName())
                    .arg(parcelParam.invoke(parceableModifier.get(returnType).getReadMethod()));
        } else if (arrayParceableModfier.containsKey(returnType)) {
            ReadWritePair readWritePair = arrayParceableModfier.get(returnType);
            parcelConstructorBody.invoke(wrapped, propertyGetter.getSetter().getName()).arg(JExpr.invoke(parcelParam, readWritePair.getReadMethod()));

        } else if (classLoaderModifier.containsKey(returnType)) {
            ReadWritePair readWritePair = classLoaderModifier.get(returnType);
            parcelConstructorBody.invoke(wrapped, propertyGetter.getSetter().getName())
                    .arg(parcelParam.invoke(readWritePair.getReadMethod()).arg(returnJClassRef.dotclass().invoke("getClassLoader")));
        } else if (returnType.implementsFrom(astClassFactory.getType(Parcelable.class))) {
            parcelConstructorBody.invoke(wrapped, propertyGetter.getSetter().getName())
                    .arg(JExpr.cast(returnJClassRef, parcelParam.invoke("readParcelable").arg(returnJClassRef.dotclass().invoke("getClassLoader"))));
        } else if (returnType.inheritsFrom(astClassFactory.getType(Serializable.class))) {
            parcelConstructorBody.invoke(wrapped, propertyGetter.getSetter().getName())
                    .arg(JExpr.cast(returnJClassRef, parcelParam.invoke("readSerializable")));
        } else if (returnType.isAnnotated(org.parceler.Parcel.class) || externalParcelRepository.contains(returnType)) {

            JClass wrapperRef = codeModel.ref(ParcelWrapper.class).narrow(generationUtil.ref(returnType));

            parcelConstructorBody.invoke(wrapped, propertyGetter.getSetter().getName())
                    .arg(((JExpression) JExpr.cast(wrapperRef, parcelParam.invoke("readParcelable")
                            .arg(parcelableClass.dotclass().invoke("getClassLoader")))).invoke(ParcelWrapper.GET_PARCEL));
        } else {
            throw new TransfuseAnalysisException("Unable to find appropriate Parcel method to read " + returnType.getName());
        }
    }

    private void buildWriteToParcel(JBlock body, JVar parcel, JVar flags, GetterSetterMethodPair propertyMutator, JFieldVar wrapped) {
        ASTType returnType = propertyMutator.getGetter().getReturnType();
        if (parceableModifier.containsKey(returnType)) {
            body.invoke(parcel,
                    parceableModifier.get((propertyMutator.getGetter().getReturnType())).getWriteMethod())
                    .arg(wrapped.invoke(propertyMutator.getGetter().getName()));
        } else if (arrayParceableModfier.containsKey(returnType)) {
            body.invoke(parcel,
                    arrayParceableModfier.get((propertyMutator.getGetter().getReturnType())).getWriteMethod())
                    .arg(wrapped.invoke(propertyMutator.getGetter().getName()));
        } else if (classLoaderModifier.containsKey(returnType)) {
            body.invoke(parcel,
                    classLoaderModifier.get((propertyMutator.getGetter().getReturnType())).getWriteMethod())
                    .arg(wrapped.invoke(propertyMutator.getGetter().getName()));
        } else if (returnType.implementsFrom(astClassFactory.getType(Parcelable.class))) {
            body.invoke(parcel, "writeParcelable")
                    .arg(wrapped.invoke(propertyMutator.getGetter().getName())).arg(flags);
        } else if (returnType.inheritsFrom(astClassFactory.getType(Serializable.class))) {
            body.invoke(parcel, "writeSerializable")
                    .arg(wrapped.invoke(propertyMutator.getGetter().getName()));
        } else if (returnType.isAnnotated(org.parceler.Parcel.class) || externalParcelRepository.contains(returnType)) {

            JInvocation wrappedParcel = generationUtil.ref(ParcelsGenerator.PARCELS_NAME)
                    .staticInvoke(WRAP_METHOD).arg(wrapped.invoke(propertyMutator.getGetter().getName()));

            body.invoke(parcel, "writeParcelable").arg(wrappedParcel)
                    .arg(flags);
        } else {
            throw new TransfuseAnalysisException("Unable to find appropriate Parcel method to write " + returnType.getName());
        }
    }

    public static final class ReadWritePair {
        private String readMethod;
        private String writeMethod;

        public ReadWritePair(String readMethod, String writeMethod) {
            this.readMethod = readMethod;
            this.writeMethod = writeMethod;
        }

        public String getReadMethod() {
            return readMethod;
        }

        public String getWriteMethod() {
            return writeMethod;
        }
    }

    private void setup() {
        addPrimitivePair(ASTPrimitiveType.BYTE, "readByte", "writeByte");
        addPrimitivePair(ASTPrimitiveType.DOUBLE, "readDouble", "writeDouble");
        addPrimitivePair(ASTPrimitiveType.FLOAT, "readFloat", "writeFloat");
        addPrimitivePair(ASTPrimitiveType.INT, "readInt", "writeInt");
        addPrimitivePair(ASTPrimitiveType.LONG, "readLong", "writeLong");
        addPrimitiveArrayPair(ASTPrimitiveType.BYTE, "createByteArray", "writeByteArray");
        addPrimitiveArrayPair(ASTPrimitiveType.CHAR, "createCharArray", "writeCharArray");
        addPrimitiveArrayPair(ASTPrimitiveType.BOOLEAN, "createBooleanArray", "writeBooleanArray");
        addPrimitiveArrayPair(ASTPrimitiveType.INT, "createByteArray", "writeIntArray");
        addPrimitiveArrayPair(ASTPrimitiveType.LONG, "createLongArray", "writeLongArray");
        addPrimitiveArrayPair(ASTPrimitiveType.FLOAT, "createFloatArray", "writeFloatArray");
        addPrimitiveArrayPair(ASTPrimitiveType.DOUBLE, "createDoubleArray", "writeDoubleArray");
        addArrayPair(String[].class, "createStringArray", "writeStringArray");
        addPair(String.class, "readString", "writeString");
        addPair(IBinder.class, "readStrongBinder", "writeStrongBinder");
        addPair(Bundle.class, "readBundle", "writeBundle");
        addArrayPair(Object[].class, "readArray", "writeArray");
        addClassloaderPair(SparseArray.class, "readSparseArray", "writeSparseArray");
        addPair(SparseBooleanArray.class, "readSparseBooleanArray", "writeSparseBooleanArray");
        addPair(Exception.class, "readException", "writeException");
    }

    private void addClassloaderPair(Class clazz, String readSparseArray, String writeSparseArray) {
        ASTType astType = astClassFactory.getType(clazz);
        classLoaderModifier.put(astType, new ReadWritePair(readSparseArray, writeSparseArray));
    }

    private void addPair(Class clazz, String readMethod, String writeMethod) {
        addPair(astClassFactory.getType(clazz), readMethod, writeMethod);
    }

    private void addPrimitiveArrayPair(ASTPrimitiveType primitiveType, String readMethod, String writeMethod) {
        addArrayPair(new ASTArrayType(primitiveType), readMethod, writeMethod);
        addArrayPair(new ASTArrayType(astClassFactory.getType(primitiveType.getObjectClass())), readMethod, writeMethod);
    }

    private void addArrayPair(Class clazz, String readMethod, String writeMethod) {
        addArrayPair(astClassFactory.getType(clazz), readMethod, writeMethod);
    }

    private void addArrayPair(ASTType astArrayType, String readMethod, String writeMethod) {
        arrayParceableModfier.put(astArrayType, new ReadWritePair(readMethod, writeMethod));
    }

    private void addPrimitivePair(ASTPrimitiveType primitiveType, String readMethod, String writeMethod) {
        addPair(primitiveType, readMethod, writeMethod);
        addPair(astClassFactory.getType(primitiveType.getObjectClass()), readMethod, writeMethod);
    }

    private void addPair(ASTType astType, String readMethod, String writeMethod) {
        parceableModifier.put(astType, new ReadWritePair(readMethod, writeMethod));
    }
}
