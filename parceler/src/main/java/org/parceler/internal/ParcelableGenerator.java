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

import com.google.common.collect.UnmodifiableIterator;
import com.sun.codemodel.*;
import org.androidtransfuse.TransfuseAnalysisException;
import org.androidtransfuse.adapter.*;
import org.androidtransfuse.adapter.classes.ASTClassFactory;
import org.androidtransfuse.gen.ClassGenerationUtil;
import org.androidtransfuse.gen.ClassNamer;
import org.androidtransfuse.gen.InvocationBuilder;
import org.androidtransfuse.gen.UniqueVariableNamer;
import org.androidtransfuse.util.matcher.Matcher;
import org.androidtransfuse.util.matcher.Matchers;
import org.parceler.ParcelConverter;
import org.parceler.ParcelWrapper;
import org.parceler.ParcelerRuntimeException;
import org.parceler.Parcels;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.*;


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
    private final ReadReferenceVisitor readFromParcelVisitor;
    private final WriteReferenceVisitor writeToParcelVisitor;
    private final InvocationBuilder invocationBuilder;

    private final Map<Matcher<ASTType>, ReadWriteGenerator> generators = new LinkedHashMap<Matcher<ASTType>, ReadWriteGenerator>();

    @Inject
    public ParcelableGenerator(JCodeModel codeModel,
                               UniqueVariableNamer variableNamer,
                               ClassNamer classNamer,
                               ASTClassFactory astClassFactory,
                               ClassGenerationUtil generationUtil,
                               ExternalParcelRepository externalParcelRepository,
                               ReadReferenceVisitor readFromParcelVisitor,
                               WriteReferenceVisitor writeToParcelVisitor,
                               InvocationBuilder invocationBuilder) {
        this.codeModel = codeModel;
        this.variableNamer = variableNamer;
        this.classNamer = classNamer;
        this.astClassFactory = astClassFactory;
        this.generationUtil = generationUtil;
        this.externalParcelRepository = externalParcelRepository;
        this.readFromParcelVisitor = readFromParcelVisitor;
        this.writeToParcelVisitor = writeToParcelVisitor;
        this.invocationBuilder = invocationBuilder;

        setup();
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
                if(parcelableDescriptor.getConstructorPair() == null){
                    JInvocation constructorInvocation = JExpr._new(inputType);
                    parcelConstructorBody.assign(wrapped, constructorInvocation);
                }
                else {
                    buildReadFromParcel(parcelableClass, parcelConstructorBody, type, wrapped, parcelableDescriptor.getConstructorPair(), parcelParam);
                    for(ASTParameter parameter : parcelableDescriptor.getConstructorPair().getConstructor().getParameters()){
                        AccessibleReference reference = parcelableDescriptor.getConstructorPair().getWriteReference(parameter);
                        buildWriteToParcel(writeToParcelMethod.body(), wtParcelParam, flags, reference, type, wrapped);
                    }
                }
                //field
                for (ReferencePair<FieldReference> fieldPair : parcelableDescriptor.getFieldPairs()) {
                    buildReadFromParcel(parcelableClass, parcelConstructorBody, wrapped, fieldPair.getSetter(), parcelParam);
                    buildWriteToParcel(writeToParcelMethod.body(), wtParcelParam, flags, fieldPair.getAccessor(), type, wrapped);
                }
                //method
                for (ReferencePair<MethodReference> methodPair : parcelableDescriptor.getMethodPairs()) {
                    buildReadFromParcel(parcelableClass, parcelConstructorBody, wrapped, methodPair.getSetter(), parcelParam);
                    buildWriteToParcel(writeToParcelMethod.body(), wtParcelParam, flags, methodPair.getAccessor(), type, wrapped);
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

    private void buildReadFromParcel(JDefinedClass parcelableClass, JBlock parcelConstructorBody, JFieldVar wrapped, MethodReference propertyAccessor, JVar parcelParam) {
        //invocation
        propertyAccessor.accept(readFromParcelVisitor,
                new ReadContext(parcelConstructorBody, wrapped, propertyAccessor.getType(), buildReadFromParcelExpression(parcelConstructorBody, parcelParam, parcelableClass, propertyAccessor.getType())));
    }

    private void buildReadFromParcel(JDefinedClass parcelableClass, JBlock parcelConstructorBody, JFieldVar wrapped, FieldReference propertyAccessor, JVar parcelParam) {
        //invocation
        propertyAccessor.accept(readFromParcelVisitor,
                new ReadContext(parcelConstructorBody, wrapped, propertyAccessor.getType(), buildReadFromParcelExpression(parcelConstructorBody, parcelParam, parcelableClass, propertyAccessor.getType())));
    }
    
    private void buildReadFromParcel(JDefinedClass parcelableClass, JBlock parcelConstructorBody, ASTType wrappedType,  JFieldVar wrapped, ConstructorReference propertyAccessor, JVar parcelParam){

        ASTConstructor constructor = propertyAccessor.getConstructor();
        List<ASTType> parameterTypes = new ArrayList<ASTType>();
        List<JExpression> inputExpression = new ArrayList<JExpression>();

        for (ASTParameter parameter : constructor.getParameters()) {
            parameterTypes.add(parameter.getASTType());
            inputExpression.add(buildReadFromParcelExpression(parcelConstructorBody, parcelParam, parcelableClass, parameter.getASTType()));
        }

        parcelConstructorBody.assign(wrapped, invocationBuilder.buildConstructorCall(constructor.getAccessModifier(), parameterTypes, inputExpression, wrappedType));
    }

    private JExpression buildReadFromParcelExpression(JBlock body, JVar parcelParam, JDefinedClass parcelableClass, ASTType type){
        JClass returnJClassRef = generationUtil.ref(type);

        ReadWriteGenerator generator = getGenerator(type);

        return generator.generateReader(body, parcelParam, type, returnJClassRef, parcelableClass);
    }

    private void buildWriteToParcel(JBlock body, JVar parcel, JVar flags, AccessibleReference reference, ASTType wrappedType, JFieldVar wrapped) {
        ASTType type = reference.getType();
        JExpression getExpression = reference.accept(writeToParcelVisitor, new WriteContext(wrapped, wrappedType));

        ReadWriteGenerator generator = getGenerator(type);

        generator.generateWriter(body, parcel, flags, type, getExpression);
    }

    private ReadWriteGenerator getGenerator(ASTType type) {
        for (Map.Entry<Matcher<ASTType>, ReadWriteGenerator> generatorEntry : generators.entrySet()) {
            if(generatorEntry.getKey().matches(type)){
                return generatorEntry.getValue();
            }
        }
        throw new ParcelerRuntimeException("Unable to find appropriate Parcel method to write " + type.getName());
    }

    public interface ReadWriteGenerator{

        JExpression generateReader(JBlock body, JVar parcelParam, ASTType type, JClass returnJClassRef, JDefinedClass parcelableClass);

        void generateWriter(JBlock body, JVar parcel, JVar flags, ASTType type, JExpression getExpression);
    }

    public static class SimpleReadWriteGenerator extends ReadWriteGeneratorBase{

        public SimpleReadWriteGenerator(String readMethod, String[] readMethodParams, String writeMethod, String[] writeMethodParams) {
            super(readMethod, readMethodParams, writeMethod, writeMethodParams);
        }

        @Override
        public JExpression generateReader(JBlock body, JVar parcelParam, ASTType type, JClass returnJClassRef, JDefinedClass parcelableClass) {
            return parcelParam.invoke(getReadMethod());
        }

        @Override
        public void generateWriter(JBlock body, JVar parcel, JVar flags, ASTType type, JExpression getExpression) {
            body.invoke(parcel, getWriteMethod()).arg(getExpression);
        }
    }

    public static class ClassloaderReadWriteGenerator extends ReadWriteGeneratorBase {

        public ClassloaderReadWriteGenerator(String readMethod, String writeMethod, Class writeMethodType) {
            super(readMethod, new String[]{ClassLoader.class.getName()}, writeMethod, new String[]{writeMethodType.getName()});
        }

        public ClassloaderReadWriteGenerator(String readMethod, String writeMethod, String writeMethodType) {
            super(readMethod, new String[]{ClassLoader.class.getName()}, writeMethod, new String[]{writeMethodType});
        }

        @Override
        public JExpression generateReader(JBlock body, JVar parcelParam, ASTType type, JClass returnJClassRef, JDefinedClass parcelableClass) {
            return parcelParam.invoke(getReadMethod()).arg(returnJClassRef.dotclass().invoke("getClassLoader"));
        }

        @Override
        public void generateWriter(JBlock body, JVar parcel, JVar flags, ASTType type, JExpression getExpression) {
            body.invoke(parcel, getWriteMethod()).arg(getExpression);
        }
    }

    public static abstract class ReadWriteGeneratorBase implements ReadWriteGenerator{
        private final String readMethod;
        private final String[] readMethodParams;
        private final String writeMethod;
        private final String[] writeMethodParams;

        private static final Map<String,Class> PRIMITIVE_CLASSES = new HashMap<String,Class>(){{
            put("int", Integer.TYPE );
            put("long", Long.TYPE );
            put("double", Double.TYPE );
            put("float", Float.TYPE );
            put("bool", Boolean.TYPE );
            put("char", Character.TYPE );
            put("byte", Byte.TYPE );
            put("void", Void.TYPE );
            put("short", Short.TYPE );
        }};

        public ReadWriteGeneratorBase(String readMethod, Class[] readMethodParams, String writeMethod, Class[] writeMethodParams) {
            this(readMethod, classArrayToStringArray(readMethodParams), writeMethod, classArrayToStringArray(writeMethodParams));
        }

        public ReadWriteGeneratorBase(String readMethod, String[] readMethodParams, String writeMethod, String[] writeMethodParams) {
            this.readMethod = readMethod;
            this.readMethodParams = readMethodParams;
            this.writeMethod = writeMethod;
            this.writeMethodParams = writeMethodParams;
        }

        private static String[] classArrayToStringArray(Class[] input){
            String[] output = new String[input.length];

            for(int i = 0; i < input.length; i++){
                output[i] = input[i].getName();
            }

            return output;
        }

        private static Class[] stringArrayToClassArray(String[] input) {
            Class[] output = new Class[input.length];

            for(int i = 0; i < input.length; i++){
                if(PRIMITIVE_CLASSES.containsKey(input[i])){
                    output[i] = PRIMITIVE_CLASSES.get(input[i]);
                }
                else{
                    try{
                        output[i] = Class.forName(input[i]);
                    } catch (ClassNotFoundException e) {
                        throw new ParcelerRuntimeException("Unable to find class " + input[i], e);
                    }
                }
            }

            return output;
        }

        public String getReadMethod() {
            return readMethod;
        }

        public Class[] getReadMethodParams() {
            return stringArrayToClassArray(readMethodParams);
        }

        public String getWriteMethod() {
            return writeMethod;
        }

        public Class[] getWriteMethodParams() {
            return stringArrayToClassArray(writeMethodParams);
        }
    }

    public static class ParcelableReadWriteGenerator extends ReadWriteGeneratorBase {

        public ParcelableReadWriteGenerator(String readMethod, String writeMethod, String parcelableType) {
            super(readMethod, new String[]{ClassLoader.class.getName()}, writeMethod, new String[]{parcelableType, int.class.getName()});
        }

        @Override
        public JExpression generateReader(JBlock body, JVar parcelParam, ASTType type, JClass returnJClassRef, JDefinedClass parcelableClass) {
            return JExpr.cast(returnJClassRef, parcelParam.invoke(getReadMethod()).arg(returnJClassRef.dotclass().invoke("getClassLoader")));
        }

        @Override
        public void generateWriter(JBlock body, JVar parcel, JVar flags, ASTType type, JExpression getExpression) {
            body.invoke(parcel, getWriteMethod()).arg(getExpression).arg(flags);
        }
    }

    public static class SerializableReadWriteGenerator extends ReadWriteGeneratorBase {

        public SerializableReadWriteGenerator() {
            super("readSerializable", new Class[0], "writeSerializable", new Class[]{Serializable.class});
        }

        @Override
        public JExpression generateReader(JBlock body, JVar parcelParam, ASTType type, JClass returnJClassRef, JDefinedClass parcelableClass) {
            return JExpr.cast(returnJClassRef, parcelParam.invoke(getReadMethod()));
        }

        @Override
        public void generateWriter(JBlock body, JVar parcel, JVar flags, ASTType type, JExpression getExpression) {
            body.invoke(parcel, getWriteMethod()).arg(getExpression);
        }
    }

    public static class ParcelReadWriteGenerator extends ReadWriteGeneratorBase {

        private final ClassGenerationUtil generationUtil;

        public ParcelReadWriteGenerator(ClassGenerationUtil generationUtil) {
            super("readParcelable", new String[]{ClassLoader.class.getName()}, "writeParcelable", new String[]{"android.os.Parcelable", int.class.getName()});
            this.generationUtil = generationUtil;
        }

        @Override
        public JExpression generateReader(JBlock body, JVar parcelParam, ASTType type, JClass returnJClassRef, JDefinedClass parcelableClass) {
            JClass wrapperRef = generationUtil.ref(ParcelWrapper.class).narrow(generationUtil.ref(type));
            return ((JExpression) JExpr.cast(wrapperRef, parcelParam.invoke(getReadMethod())
                    .arg(parcelableClass.dotclass().invoke("getClassLoader")))).invoke(ParcelWrapper.GET_PARCEL);
        }

        @Override
        public void generateWriter(JBlock body, JVar parcel, JVar flags, ASTType type, JExpression getExpression) {
            JInvocation wrappedParcel = generationUtil.ref(ParcelsGenerator.PARCELS_NAME).staticInvoke(WRAP_METHOD).arg(getExpression);
            body.invoke(parcel, getWriteMethod()).arg(wrappedParcel).arg(flags);
        }
    }

    public static class ListReadWriteGenerator extends ReadWriteGeneratorBase {

        private final ClassGenerationUtil generationUtil;
        private final UniqueVariableNamer namer;
        private final Map<Matcher<ASTType>, ReadWriteGenerator> generators;
        private final ASTClassFactory astClassFactory;
        private final JCodeModel codeModel;

        public ListReadWriteGenerator(ClassGenerationUtil generationUtil, UniqueVariableNamer namer, Map<Matcher<ASTType>, ReadWriteGenerator> generators, ASTClassFactory astClassFactory, JCodeModel codeModel) {
            super("readArrayList", new Class[]{ClassLoader.class}, "writeList", new Class[]{List.class});
            this.generationUtil = generationUtil;
            this.generators = generators;
            this.namer = namer;
            this.astClassFactory = astClassFactory;
            this.codeModel = codeModel;
        }

        @Override
        public JExpression generateReader(JBlock body, JVar parcelParam, ASTType type, JClass returnJClassRef, JDefinedClass parcelableClass) {

            JClass outputType = generationUtil.ref(List.class);
            JClass arrayListType = generationUtil.ref(ArrayList.class);

            ASTType componentType = astClassFactory.getType(Object.class);

            if(type.getGenericParameters().size() == 1){
                componentType = type.getGenericParameters().iterator().next();
                outputType = outputType.narrow(generationUtil.narrowRef(componentType));
                arrayListType = arrayListType.narrow(generationUtil.narrowRef(componentType));
            }

            JVar sizeVar = body.decl(codeModel.INT, namer.generateName(codeModel.INT), parcelParam.invoke("readInt"));

            JVar outputVar = body.decl(outputType, namer.generateName(List.class));

            JConditional nullInputConditional = body._if(sizeVar.lt(JExpr.lit(0)));

            JBlock nullBody = nullInputConditional._then();

            nullBody.assign(outputVar, JExpr._null());

            JBlock nonNullBody = nullInputConditional._else();

            nonNullBody.assign(outputVar, JExpr._new(arrayListType));

            JForLoop forLoop = nonNullBody._for();
            JVar nVar = forLoop.init(codeModel.INT, namer.generateName(codeModel.INT), JExpr.lit(0));
            forLoop.test(nVar.lt(sizeVar));
            forLoop.update(nVar.incr());
            JBlock readLoopBody = forLoop.body();

            ReadWriteGenerator generator = getGenerator(componentType);

            JExpression readExpression = generator.generateReader(readLoopBody, parcelParam, componentType, generationUtil.ref(componentType), parcelableClass);

            readLoopBody.invoke(outputVar, "add").arg(readExpression);

            return outputVar;
        }

        @Override
        public void generateWriter(JBlock body, JVar parcel, JVar flags, ASTType type, JExpression getExpression) {

            ASTType componentType = astClassFactory.getType(Object.class);

            if(type.getGenericParameters().size() == 1){
                componentType = type.getGenericParameters().iterator().next();
            }
            JClass inputType = generationUtil.narrowRef(componentType);


            JConditional nullConditional = body._if(getExpression.eq(JExpr._null()));
            nullConditional._then().invoke(parcel, "writeInt").arg(JExpr.lit(-1));

            JBlock writeBody = nullConditional._else();

            writeBody.invoke(parcel, "writeInt").arg(getExpression.invoke("size"));
            JForEach forEach = writeBody.forEach(inputType, namer.generateName(inputType), getExpression);

            ReadWriteGenerator generator = getGenerator(componentType);

            generator.generateWriter(forEach.body(), parcel, flags, componentType, forEach.var());
        }

        private ReadWriteGenerator getGenerator(ASTType type) {
            for (Map.Entry<Matcher<ASTType>, ReadWriteGenerator> generatorEntry : generators.entrySet()) {
                if(generatorEntry.getKey().matches(type)){
                    return generatorEntry.getValue();
                }
            }
            throw new ParcelerRuntimeException("Unable to find appropriate Parcel method to write " + type.getName());
        }
    }

    public static class MapReadWriteGenerator extends ReadWriteGeneratorBase {

        private final ClassGenerationUtil generationUtil;
        private final UniqueVariableNamer namer;
        private final Map<Matcher<ASTType>, ReadWriteGenerator> generators;
        private final ASTClassFactory astClassFactory;
        private final JCodeModel codeModel;

        public MapReadWriteGenerator(ClassGenerationUtil generationUtil, UniqueVariableNamer namer, Map<Matcher<ASTType>, ReadWriteGenerator> generators, ASTClassFactory astClassFactory, JCodeModel codeModel) {
            super("readHashMap", new Class[]{ClassLoader.class}, "writeMap", new Class[]{Map.class});
            this.generationUtil = generationUtil;
            this.generators = generators;
            this.namer = namer;
            this.astClassFactory = astClassFactory;
            this.codeModel = codeModel;
        }

        @Override
        public JExpression generateReader(JBlock body, JVar parcelParam, ASTType type, JClass returnJClassRef, JDefinedClass parcelableClass) {

            JClass outputType = generationUtil.ref(Map.class);
            JClass hashMapType = generationUtil.ref(HashMap.class);

            ASTType keyComponentType = astClassFactory.getType(Object.class);
            ASTType valueComponentType = astClassFactory.getType(Object.class);

            if(type.getGenericParameters().size() == 2){
                UnmodifiableIterator<ASTType> iterator = type.getGenericParameters().iterator();
                keyComponentType = iterator.next();
                valueComponentType = iterator.next();
                JClass keyType = generationUtil.narrowRef(keyComponentType);
                JClass valueType = generationUtil.narrowRef(valueComponentType);
                outputType = outputType.narrow(keyType, valueType);
                hashMapType = hashMapType.narrow(keyType, valueType);
            }

            JVar sizeVar = body.decl(codeModel.INT, namer.generateName(codeModel.INT), parcelParam.invoke("readInt"));

            JVar outputVar = body.decl(outputType, namer.generateName(Map.class));

            JConditional nullInputConditional = body._if(sizeVar.lt(JExpr.lit(0)));

            JBlock nullBody = nullInputConditional._then();

            nullBody.assign(outputVar, JExpr._null());

            JBlock nonNullBody = nullInputConditional._else();

            nonNullBody.assign(outputVar, JExpr._new(hashMapType));

            JForLoop forLoop = nonNullBody._for();
            JVar nVar = forLoop.init(codeModel.INT, namer.generateName(codeModel.INT), JExpr.lit(0));
            forLoop.test(nVar.lt(sizeVar));
            forLoop.update(nVar.incr());
            JBlock readLoopBody = forLoop.body();

            ReadWriteGenerator keyGenerator = getGenerator(keyComponentType);
            ReadWriteGenerator valueGenerator = getGenerator(valueComponentType);

            JExpression readKeyExpression = keyGenerator.generateReader(readLoopBody, parcelParam, keyComponentType, generationUtil.ref(keyComponentType), parcelableClass);
            JExpression readValueExpression = valueGenerator.generateReader(readLoopBody, parcelParam, valueComponentType, generationUtil.ref(valueComponentType), parcelableClass);

            readLoopBody.invoke(outputVar, "put").arg(readKeyExpression).arg(readValueExpression);

            return outputVar;
        }

        @Override
        public void generateWriter(JBlock body, JVar parcel, JVar flags, ASTType type, JExpression getExpression) {

            ASTType keyComponentType = astClassFactory.getType(Object.class);
            ASTType valueComponentType = astClassFactory.getType(Object.class);

            if(type.getGenericParameters().size() == 2){
                UnmodifiableIterator<ASTType> iterator = type.getGenericParameters().iterator();
                keyComponentType = iterator.next();
                valueComponentType = iterator.next();
            }
            JClass keyType = generationUtil.narrowRef(keyComponentType);
            JClass valueType = generationUtil.narrowRef(valueComponentType);

            JClass inputType = generationUtil.ref(Map.Entry.class).narrow(keyType, valueType);


            JConditional nullConditional = body._if(getExpression.eq(JExpr._null()));
            nullConditional._then().invoke(parcel, "writeInt").arg(JExpr.lit(-1));

            JBlock writeBody = nullConditional._else();

            writeBody.invoke(parcel, "writeInt").arg(getExpression.invoke("size"));

            JForEach forEach = writeBody.forEach(inputType, namer.generateName(inputType), getExpression.invoke("entrySet"));

            ReadWriteGenerator keyGenerator = getGenerator(keyComponentType);
            ReadWriteGenerator valueGenerator = getGenerator(valueComponentType);

            keyGenerator.generateWriter(forEach.body(), parcel, flags, keyComponentType, forEach.var().invoke("getKey"));
            valueGenerator.generateWriter(forEach.body(), parcel, flags, valueComponentType, forEach.var().invoke("getValue"));
        }

        private ReadWriteGenerator getGenerator(ASTType type) {
            for (Map.Entry<Matcher<ASTType>, ReadWriteGenerator> generatorEntry : generators.entrySet()) {
                if(generatorEntry.getKey().matches(type)){
                    return generatorEntry.getValue();
                }
            }
            throw new ParcelerRuntimeException("Unable to find appropriate Parcel method to write " + type.getName());
        }
    }

    public static class ImplementsMatcher implements Matcher<ASTType>{

        private final ASTType superType;

        public ImplementsMatcher(ASTType superType) {
            this.superType = superType;
        }

        @Override
        public boolean matches(ASTType astType) {
            return astType.implementsFrom(superType);
        }
    }

    public static class InheritsMatcher implements Matcher<ASTType>{

        private final ASTType superType;

        public InheritsMatcher(ASTType superType) {
            this.superType = superType;
        }

        @Override
        public boolean matches(ASTType astType) {
            return astType.inheritsFrom(superType);
        }
    }

    public static class ParcelMatcher implements Matcher<ASTType>{

        private final ExternalParcelRepository externalParcelRepository;

        public ParcelMatcher(ExternalParcelRepository externalParcelRepository) {
            this.externalParcelRepository = externalParcelRepository;
        }

        @Override
        public boolean matches(ASTType type) {
            return type.isAnnotated(org.parceler.Parcel.class) || externalParcelRepository.contains(type);
        }
    }

    private void setup() {
        addPair(byte.class, "readByte", "writeByte");
        addPair(Byte.class, "readByte", "writeByte", byte.class);
        addPair(double.class, "readDouble", "writeDouble");
        addPair(Double.class, "readDouble", "writeDouble", double.class);
        addPair(float.class, "readFloat", "writeFloat");
        addPair(Float.class, "readFloat", "writeFloat", float.class);
        addPair(int.class, "readInt", "writeInt");
        addPair(Integer.class, "readInt", "writeInt", int.class);
        addPair(long.class, "readLong", "writeLong");
        addPair(Long.class, "readLong", "writeLong", long.class);
        addPair(byte[].class, "createByteArray", "writeByteArray");
        addPair(char[].class, "createCharArray", "writeCharArray");
        addPair(boolean[].class, "createBooleanArray", "writeBooleanArray");
        addPair(int[].class, "createIntArray", "writeIntArray");
        addPair(long[].class, "createLongArray", "writeLongArray");
        addPair(float[].class, "createFloatArray", "writeFloatArray");
        addPair(double[].class, "createDoubleArray", "writeDoubleArray");
        addPair(String[].class, "createStringArray", "writeStringArray");
        addPair(String.class, "readString", "writeString");
        addPair("android.os.IBinder", "readStrongBinder", "writeStrongBinder");
        addPair("android.os.Bundle", "readBundle", "writeBundle");
        addPair(Exception.class, "readException", "writeException");
        addPair("android.util.SparseBooleanArray", "readSparseBooleanArray", "writeSparseBooleanArray");
        generators.put(Matchers.type(new ASTStringType("android.util.SparseArray")).ignoreGenerics().build(), new ClassloaderReadWriteGenerator("readSparseArray", "writeSparseArray", "android.util.SparseArray"));
        generators.put(new ImplementsMatcher(new ASTStringType("android.os.Parcelable")), new ParcelableReadWriteGenerator("readParcelable", "writeParcelable", "android.os.Parcelable"));
        generators.put(new ImplementsMatcher(new ASTArrayType(new ASTStringType("android.os.Parcelable"))), new ParcelableReadWriteGenerator("readParcelableArray", "writeParcelableArray", "[Landroid.os.Parcelable;"));
        generators.put(new ParcelMatcher(externalParcelRepository), new ParcelReadWriteGenerator(generationUtil));
        generators.put(Matchers.type(astClassFactory.getType(List.class)).ignoreGenerics().build(), new ListReadWriteGenerator(generationUtil, variableNamer, generators, astClassFactory, codeModel));
        generators.put(Matchers.type(astClassFactory.getType(ArrayList.class)).ignoreGenerics().build(), new ListReadWriteGenerator(generationUtil, variableNamer, generators, astClassFactory, codeModel));
        generators.put(Matchers.type(astClassFactory.getType(Map.class)).ignoreGenerics().build(), new MapReadWriteGenerator(generationUtil, variableNamer, generators, astClassFactory, codeModel));
        generators.put(Matchers.type(astClassFactory.getType(HashMap.class)).ignoreGenerics().build(), new MapReadWriteGenerator(generationUtil, variableNamer, generators, astClassFactory, codeModel));
        generators.put(new InheritsMatcher(astClassFactory.getType(Serializable.class)), new SerializableReadWriteGenerator());
        generators.put(Matchers.type(astClassFactory.getType(Object[].class)).build(), new ClassloaderReadWriteGenerator("readArray", "writeArray", Object[].class));
    }

    private void addPair(Class clazz, String readMethod, String writeMethod) {
        addPair(clazz, readMethod, writeMethod, clazz);
    }

    private void addPair(Class clazz, String readMethod, String writeMethod, Class writeParam) {
        addPair(astClassFactory.getType(clazz), readMethod, writeMethod, writeParam.getName());
    }

    private void addPair(String clazzName, String readMethod, String writeMethod) {
        addPair(clazzName, readMethod, writeMethod, clazzName);
    }

    private void addPair(String clazzName, String readMethod, String writeMethod, String writeParam) {
        addPair(new ASTStringType(clazzName), readMethod, writeMethod, writeParam);
    }

    private void addPair(ASTType type, String readMethod, String writeMethod, String writeParam){
        generators.put(Matchers.type(type).build(), new SimpleReadWriteGenerator(readMethod, new String[0], writeMethod, new String[]{writeParam}));
    }

    protected Map<Matcher<ASTType>, ReadWriteGenerator> getGenerators() {
        return generators;
    }
}
