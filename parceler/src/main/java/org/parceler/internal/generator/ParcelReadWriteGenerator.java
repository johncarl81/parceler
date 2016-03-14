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
    public JExpression generateReader(JBlock body, JVar parcel, ASTType type, JClass returnJClassRef, JDefinedClass parcelableClass, JVar identity, JVar readIdentityMap) {
        JType inputType = generationUtil.ref(type);

        //JVar identity = body.decl(codeModel.INT, variableNamer.generateName("identity"), parcel.invoke("readInt"));
        JVar wrapped = body.decl(inputType, variableNamer.generateName(type));

        ParcelableDescriptor parcelDescriptor = this.analysis.analyze(type);
        if(parcelDescriptor != null) {
            generator.get().buildParcelRead(parcelDescriptor, parcelableClass, wrapped, type, inputType, identity, parcel, body, readIdentityMap);
        }

        return wrapped;
    }

    @Override
    public void generateWriter(JBlock body, JExpression parcel, JVar flags, ASTType type, JExpression getExpression, JDefinedClass parcelableClass, JVar writeIdentitySet) {
        ParcelableDescriptor parcelDescriptor = this.analysis.analyze(type);
        if(parcelDescriptor != null) {
            generator.get().buildParcelWrite(parcelDescriptor, parcelableClass, getExpression, type, parcel, flags, body, writeIdentitySet);
        }
    }
}
