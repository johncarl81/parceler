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

import javax.inject.Inject;

/**
 * @author John Ericksen
 */
public class EnumReadWriteGenerator extends ReadWriteGeneratorBase {

    private final ClassGenerationUtil generationUtil;
    private final UniqueVariableNamer namer;

    @Inject
    public EnumReadWriteGenerator(ClassGenerationUtil generationUtil, UniqueVariableNamer namer) {
        super("readString", new Class[0], "writeString", new Class[]{String.class});
        this.generationUtil = generationUtil;
        this.namer = namer;
    }

    @Override
    public JExpression generateReader(JBlock body, JVar parcelParam, ASTType type, JClass returnJClassRef, JDefinedClass parcelableClass, JVar identity, JVar readIdentityMap) {
        JClass enumRef = generationUtil.ref(Enum.class);
        JClass enumClassRef = generationUtil.ref(type);
        JClass stringRef = generationUtil.ref(String.class);

        JVar localVar = body.decl(stringRef, namer.generateName(enumClassRef), parcelParam.invoke(getReadMethod()));

        return JExpr.cast(returnJClassRef, JOp.cond(localVar.eq(JExpr._null()), JExpr._null(), enumRef.staticInvoke("valueOf").arg(enumClassRef.dotclass()).arg(localVar)));
    }

    @Override
    public void generateWriter(JBlock body, JExpression parcel, JVar flags, ASTType type, JExpression getExpression, JDefinedClass parcelableClass, JVar writeIdentitySet) {
        JClass enumClassRef = generationUtil.ref(type);

        JVar localVar = body.decl(enumClassRef, namer.generateName(enumClassRef), getExpression);
        
        body.invoke(parcel, getWriteMethod()).arg(JOp.cond(localVar.eq(JExpr._null()), JExpr._null(), localVar.invoke("name")));
    }
}
