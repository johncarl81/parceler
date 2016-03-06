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
import org.parceler.ParcelerRuntimeException;
import org.parceler.internal.ParcelableAnalysis;
import org.parceler.internal.ParcelableDescriptor;
import org.parceler.internal.ParcelableGenerator;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Map;
import java.util.Set;

/**
* @author John Ericksen
*/
public class ParcelReadWriteGenerator extends ReadWriteGeneratorBase {

    public static final String WRITE_METHOD = "write";
    public static final String READ_METHOD = "read";
    private static final String ANDROID_PARCEL = "android.os.Parcel";

    private final ClassGenerationUtil generationUtil;
    private final ParcelableAnalysis analysis;
    private final Provider<ParcelableGenerator> generator;
    private final UniqueVariableNamer variableNamer;
    private final JCodeModel codeModel;

    @Inject
    public ParcelReadWriteGenerator(ClassGenerationUtil generationUtil, ParcelableAnalysis analysis, Provider<ParcelableGenerator> generator, UniqueVariableNamer variableNamer, JCodeModel codeModel) {
        super("readParcelable", new String[]{ClassLoader.class.getName()}, "writeParcelable", new String[]{"android.os.Parcelable", int.class.getName()});
        this.generationUtil = generationUtil;
        this.analysis = analysis;
        this.generator = generator;
        this.variableNamer = variableNamer;
        this.codeModel = codeModel;
    }

    @Override
    public JExpression generateReader(JBlock body, JVar parcel, ASTType type, JClass returnJClassRef, JDefinedClass parcelableClass, JVar readIdentityMap) {
        JType inputType = generationUtil.ref(type);
        JType parcelType = generationUtil.ref(ANDROID_PARCEL);
        //write method
        JMethod readMethod = findMethodByName(parcelableClass, READ_METHOD);
        if(readMethod == null){
            readMethod = parcelableClass.method(JMod.PUBLIC | JMod.STATIC, generationUtil.ref(type), READ_METHOD);
            JBlock readMethodBody = readMethod.body();
            JVar readWrapped = readMethodBody.decl(inputType, variableNamer.generateName(type));
            JVar parcelParam = readMethod.param(parcelType, variableNamer.generateName(parcelType));
            JVar identityParam = readMethod.param(codeModel.ref(Map.class).narrow(Integer.class, Object.class), variableNamer.generateName("identityMap"));

            JVar identity = readMethodBody.decl(codeModel.INT, variableNamer.generateName("identity"), parcelParam.invoke("readInt"));
            JBlock containsKeyBody = readMethodBody._if(identityParam.invoke("containsKey").arg(identity))._then();
            JVar value = containsKeyBody.decl(inputType, variableNamer.generateName(inputType), JExpr.cast(inputType, identityParam.invoke("get").arg(identity)));
            containsKeyBody._if(value.eq(JExpr._null()))._then()._throw(JExpr._new(generationUtil.ref(ParcelerRuntimeException.class))
                    .arg("An instance loop was detected whild building Parcelable and deseralization cannot continue.  This error is most likely due to using @ParcelConstructor or @ParcelFactory."));
            containsKeyBody._return(value);

            ParcelableDescriptor parcelDescriptor = this.analysis.analyze(type);
            if(parcelDescriptor != null) {
                generator.get().buildParcelRead(parcelDescriptor, parcelableClass, readWrapped, type, inputType, identity, parcelParam, readMethodBody, identityParam);
            }

            readMethodBody._return(readWrapped);
        }

        JVar wrapped = body.decl(inputType, variableNamer.generateName(type));
        JConditional nullCondition = body._if(parcel.invoke("readInt").eq(JExpr.lit(-1)));
        nullCondition._then().assign(wrapped, JExpr._null());
        nullCondition._else().assign(wrapped, JExpr.invoke(readMethod).arg(parcel).arg(readIdentityMap));
        return wrapped;
    }

    @Override
    public void generateWriter(JBlock body, JExpression parcel, JVar flags, ASTType type, JExpression getExpression, JDefinedClass parcelableClass, JVar writeIdentitySet) {

        JType parcelType = generationUtil.ref(ANDROID_PARCEL);
        //write method
        JType inputType = generationUtil.ref(type);
        JMethod writeMethod = findMethodByName(parcelableClass, WRITE_METHOD);
        if(writeMethod == null){
            writeMethod = parcelableClass.method(JMod.PUBLIC | JMod.STATIC, Void.TYPE, WRITE_METHOD);
            JBlock writeMethodBody = writeMethod.body();
            JVar writeInputVar = writeMethod.param(inputType, variableNamer.generateName(inputType));
            JVar parcelParam = writeMethod.param(parcelType, variableNamer.generateName(parcelType));
            JVar flagsParam = writeMethod.param(int.class, variableNamer.generateName("flags"));
            JVar identityParam = writeMethod.param(codeModel.ref(Set.class).narrow(Integer.class), variableNamer.generateName("identitySet"));

            JVar identity = writeMethodBody.decl(codeModel.INT, variableNamer.generateName("identity"), generationUtil.ref(System.class).staticInvoke("identityHashCode").arg(writeInputVar));
            writeMethodBody.add(parcelParam.invoke("writeInt").arg(identity));

            JBlock buildBody = writeMethodBody._if(identityParam.invoke("contains").arg(identity).not())._then();
            buildBody.add(identityParam.invoke("add").arg(identity));

            ParcelableDescriptor parcelDescriptor = this.analysis.analyze(type);
            if(parcelDescriptor != null) {
                generator.get().buildParcelWrite(parcelDescriptor, parcelableClass, writeInputVar, type, parcelParam, flagsParam, buildBody, identityParam);
            }
        }

        JConditional nullCondition = body._if(getExpression.eq(JExpr._null()));
        nullCondition._then().add(parcel.invoke("writeInt").arg(JExpr.lit(-1)));
        JBlock nonNullCondition = nullCondition._else();
        nonNullCondition.add(parcel.invoke("writeInt").arg(JExpr.lit(1)));
        nonNullCondition.invoke(writeMethod).arg(getExpression).arg(parcel).arg(flags).arg(writeIdentitySet);
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
