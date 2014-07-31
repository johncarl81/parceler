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
package org.parceler.internal.generator;

import com.sun.codemodel.*;
import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.gen.ClassGenerationUtil;
import org.androidtransfuse.gen.UniqueVariableNamer;
import org.parceler.internal.ParcelableAnalysis;
import org.parceler.internal.ParcelableDescriptor;
import org.parceler.internal.ParcelableGenerator;

import javax.inject.Provider;

/**
* @author John Ericksen
*/
public class ParcelReadWriteGenerator extends ReadWriteGeneratorBase {
    public static final String WRAP_METHOD = "wrap";

    private final ClassGenerationUtil generationUtil;
    private final ParcelableAnalysis analysis;
    private final Provider<ParcelableGenerator> generator;
    private final UniqueVariableNamer variableNamer;

    public ParcelReadWriteGenerator(ClassGenerationUtil generationUtil, ParcelableAnalysis analysis, Provider<ParcelableGenerator> generator, UniqueVariableNamer variableNamer) {
        super("readParcelable", new String[]{ClassLoader.class.getName()}, "writeParcelable", new String[]{"android.os.Parcelable", int.class.getName()});
        this.generationUtil = generationUtil;
        this.analysis = analysis;
        this.generator = generator;
        this.variableNamer = variableNamer;
    }

    @Override
    public JExpression generateReader(JBlock body, JVar parcelParam, ASTType type, JClass returnJClassRef, JDefinedClass parcelableClass) {
        JType inputType = generationUtil.ref(type);
        JVar wrapped = body.decl(inputType, variableNamer.generateName(type));
        JConditional nullCondition = body._if(parcelParam.invoke("readInt").eq(JExpr.lit(-1)));
        nullCondition._then().assign(wrapped, JExpr._null());
        ParcelableDescriptor analysis = this.analysis.analyze(type, null);
        generator.get().buildParcelRead(analysis, parcelableClass, wrapped, type, inputType, parcelParam, nullCondition._else());
        return wrapped;
    }

    @Override
    public void generateWriter(JBlock body, JExpression parcel, JVar flags, ASTType type, JExpression getExpression, JDefinedClass parcelableClass) {
        JConditional nullCondition = body._if(getExpression.eq(JExpr._null()));
        nullCondition._then().add(parcel.invoke("writeInt").arg(JExpr.lit(-1)));
        JBlock nonNullcondition = nullCondition._else();
        nonNullcondition.add(parcel.invoke("writeInt").arg(JExpr.lit(1)));
        ParcelableDescriptor analysis = this.analysis.analyze(type, null);
        generator.get().buildParcelWrite(analysis, parcelableClass, getExpression, type, parcel, flags, nonNullcondition);
    }
}
