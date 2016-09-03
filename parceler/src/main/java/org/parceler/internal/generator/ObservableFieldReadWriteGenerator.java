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
import org.androidtransfuse.adapter.ASTStringType;
import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.gen.ClassGenerationUtil;
import org.parceler.internal.Generators;

import javax.inject.Inject;

/**
 * @author John Ericksen
 */
public class ObservableFieldReadWriteGenerator extends ReadWriteGeneratorBase {

    private static final ASTType OBSERVABLE_FIELD_TYPE = new ASTStringType("android.databinding.ObservableField");

    private final Generators generators;
    private final ClassGenerationUtil generationUtil;

    @Inject
    public ObservableFieldReadWriteGenerator(Generators generators, ClassGenerationUtil generationUtil) {
        super("readParcelable", new String[]{ClassLoader.class.getName()}, "writeParcelable", new String[]{"android.os.Parcelable", int.class.getName()});
        this.generators = generators;
        this.generationUtil = generationUtil;
    }

    @Override
    public JExpression generateReader(JBlock body, JVar parcelParam, ASTType type, JClass returnJClassRef, JDefinedClass parcelableClass, JVar identity, JVar readIdentityMap) {

        ASTType componentType = type.getGenericParameters().get(0);

        ReadWriteGenerator generator = generators.getGenerator(componentType);

        JExpression readExpression = generator.generateReader(body, parcelParam, componentType, generationUtil.ref(componentType), parcelableClass, identity, readIdentityMap);

        return JExpr._new(generationUtil.ref(OBSERVABLE_FIELD_TYPE)).arg(readExpression);
    }

    @Override
    public void generateWriter(JBlock body, JExpression parcel, JVar flags, ASTType type, JExpression getExpression, JDefinedClass parcelableClass, JVar writeIdentitySet) {

        ASTType componentType = type.getGenericParameters().get(0);

        ReadWriteGenerator generator = generators.getGenerator(componentType);

        generator.generateWriter(body, parcel, flags, componentType, getExpression.invoke("get"), parcelableClass, writeIdentitySet);
    }
}
