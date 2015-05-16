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
package org.parceler.internal.generator;

import com.sun.codemodel.*;
import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.gen.ClassGenerationUtil;
import org.androidtransfuse.gen.UniqueVariableNamer;
import org.parceler.internal.ParcelableAnalysis;
import org.parceler.internal.ParcelableDescriptor;
import org.parceler.internal.ParcelableGenerator;

import javax.inject.Inject;
import javax.inject.Provider;

/**
* @author John Ericksen
*/
public class ParcelReadWriteGenerator extends ReadWriteGeneratorBase {

    private static final String WRITE_METHOD = "write";
    private static final String READ_METHOD = "read";
    private static final String ANDROID_PARCEL = "android.os.Parcel";

    private final ClassGenerationUtil generationUtil;
    private final ParcelableAnalysis analysis;
    private final Provider<ParcelableGenerator> generator;
    private final UniqueVariableNamer variableNamer;

    @Inject
    public ParcelReadWriteGenerator(ClassGenerationUtil generationUtil, ParcelableAnalysis analysis, Provider<ParcelableGenerator> generator, UniqueVariableNamer variableNamer) {
        super("readParcelable", new String[]{ClassLoader.class.getName()}, "writeParcelable", new String[]{"android.os.Parcelable", int.class.getName()});
        this.generationUtil = generationUtil;
        this.analysis = analysis;
        this.generator = generator;
        this.variableNamer = variableNamer;
    }

    @Override
    public JExpression generateReader(JBlock body, JVar parcel, ASTType type, JClass returnJClassRef, JDefinedClass parcelableClass) {

        JType inputType = generationUtil.ref(type);
        String readMethodName = READ_METHOD + type.getPackageClass().getFullyQualifiedName().replace(".", "_");
        JType parcelType = generationUtil.ref(ANDROID_PARCEL);
        //write method
        JMethod readMethod = findMethodByName(parcelableClass, readMethodName);
        if(readMethod == null){
            readMethod = parcelableClass.method(JMod.PRIVATE, generationUtil.ref(type), readMethodName);
            JVar parcelParam = readMethod.param(parcelType, variableNamer.generateName(parcelType));
            JVar readWrapped = readMethod.body().decl(inputType, variableNamer.generateName(type));
            ParcelableDescriptor parcelDescriptor = this.analysis.analyze(type);
            if(parcelDescriptor != null) {
                generator.get().buildParcelRead(parcelDescriptor, parcelableClass, readWrapped, type, inputType, parcelParam, readMethod.body());
            }
            readMethod.body()._return(readWrapped);
        }

        JVar wrapped = body.decl(inputType, variableNamer.generateName(type));
        JConditional nullCondition = body._if(parcel.invoke("readInt").eq(JExpr.lit(-1)));
        nullCondition._then().assign(wrapped, JExpr._null());
        nullCondition._else().assign(wrapped, JExpr.invoke(readMethod).arg(parcel));
        return wrapped;
    }

    @Override
    public void generateWriter(JBlock body, JExpression parcel, JVar flags, ASTType type, JExpression getExpression, JDefinedClass parcelableClass) {

        String writeMethodName = WRITE_METHOD + type.getPackageClass().getFullyQualifiedName().replace(".", "_");
        JType parcelType = generationUtil.ref(ANDROID_PARCEL);
        //write method
        JType inputType = generationUtil.ref(type);
        JMethod writeMethod = findMethodByName(parcelableClass, writeMethodName);
        if(writeMethod == null){
            writeMethod = parcelableClass.method(JMod.PRIVATE, Void.TYPE, writeMethodName);
            JVar writeInputVar = writeMethod.param(inputType, variableNamer.generateName(inputType));
            JVar parcelParam = writeMethod.param(parcelType, variableNamer.generateName(parcelType));
            JVar flagsParam = writeMethod.param(int.class, variableNamer.generateName("flags"));
            ParcelableDescriptor parcelDescriptor = this.analysis.analyze(type);
            if(parcelDescriptor != null) {
                generator.get().buildParcelWrite(parcelDescriptor, parcelableClass, writeInputVar, type, parcelParam, flagsParam, writeMethod.body());
            }
        }

        JConditional nullCondition = body._if(getExpression.eq(JExpr._null()));
        nullCondition._then().add(parcel.invoke("writeInt").arg(JExpr.lit(-1)));
        JBlock nonNullCondition = nullCondition._else();
        nonNullCondition.add(parcel.invoke("writeInt").arg(JExpr.lit(1)));
        nonNullCondition.invoke(writeMethod).arg(getExpression).arg(parcel).arg(flags);
    }

    private JMethod findMethodByName(JDefinedClass definedClass, String name){
        for (JMethod method : definedClass.methods()) {
            if(method.name().equals(name)){
                return method;
            }
        }
        return null;
    }
}
