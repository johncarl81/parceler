/**
 * Copyright 2011-2015 John Ericksen
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
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
import org.androidtransfuse.gen.UniqueVariableNamer;

import javax.inject.Inject;

/**
 * Generates the read/write for @Remoter
 *
 * @author js
 */
public class RemoterReadWriteGenerator extends ReadWriteGeneratorBase {

    private final ClassGenerationUtil generationUtil;
    private final UniqueVariableNamer namer;

    @Inject
    public RemoterReadWriteGenerator(ClassGenerationUtil generationUtil, UniqueVariableNamer namer) {
        super("readStrongBinder", new String[0], "writeStrongBinder", new String[]{"android.os.IBinder"});
        this.generationUtil = generationUtil;
        this.namer = namer;
    }

    @Override
    public JExpression generateReader(JBlock body, JVar parcelParam, ASTType type, JClass returnJClassRef, JDefinedClass parcelableClass, JVar identity, JVar readIdentityMap) {
        JClass remoterClassRef = generationUtil.ref(type);
        JClass binderRef = generationUtil.ref(new ASTStringType("android.os.IBinder"));


        JVar localVar = body.decl(binderRef, namer.generateName(remoterClassRef), parcelParam.invoke(getReadMethod()));

        return JOp.cond(localVar.eq(JExpr._null()), JExpr._null(), JExpr.direct("new " + remoterClassRef.name() + "_Proxy(" + localVar.name() + ")"));
    }

    @Override
    public void generateWriter(JBlock body, JExpression parcel, JVar flags, ASTType type, JExpression getExpression, JDefinedClass parcelableClass, JVar writeIdentitySet) {
        JClass remoterClassRef = generationUtil.ref(type);

        JVar localVar = body.decl(remoterClassRef, namer.generateName(remoterClassRef), getExpression);

        body.invoke(parcel, getWriteMethod()).arg(JOp.cond(localVar.eq(JExpr._null()), JExpr._null(), JExpr.direct("new " + remoterClassRef.name() + "_Stub(" + localVar.name() + ")")));
    }
}
