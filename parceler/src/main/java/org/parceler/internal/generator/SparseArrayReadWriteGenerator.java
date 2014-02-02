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
import org.androidtransfuse.adapter.classes.ASTClassFactory;
import org.androidtransfuse.gen.ClassGenerationUtil;
import org.androidtransfuse.gen.UniqueVariableNamer;
import org.parceler.internal.Generators;

import javax.inject.Inject;
import java.util.List;

/**
* @author John Ericksen
*/
public class SparseArrayReadWriteGenerator extends ReadWriteGeneratorBase {

    private final ClassGenerationUtil generationUtil;
    private final UniqueVariableNamer namer;
    private final Generators generators;
    private final ASTClassFactory astClassFactory;
    private final JCodeModel codeModel;

    @Inject
    public SparseArrayReadWriteGenerator(ClassGenerationUtil generationUtil, UniqueVariableNamer namer, Generators generators, ASTClassFactory astClassFactory, JCodeModel codeModel) {
        super("readArrayList", new Class[]{ClassLoader.class}, "writeList", new Class[]{List.class});
        this.generationUtil = generationUtil;
        this.generators = generators;
        this.namer = namer;
        this.astClassFactory = astClassFactory;
        this.codeModel = codeModel;
    }

    @Override
    public JExpression generateReader(JBlock body, JVar parcelParam, ASTType type, JClass returnJClassRef, JDefinedClass parcelableClass) {

        JClass sparseArrayType = generationUtil.ref("android.util.SparseArray");

        ASTType componentType = astClassFactory.getType(Object.class);

        if(type.getGenericParameters().size() == 1){
            componentType = type.getGenericParameters().iterator().next();
            sparseArrayType = sparseArrayType.narrow(generationUtil.narrowRef(componentType));
        }

        JVar sizeVar = body.decl(codeModel.INT, namer.generateName(codeModel.INT), parcelParam.invoke("readInt"));

        JVar outputVar = body.decl(sparseArrayType, namer.generateName(sparseArrayType));

        JConditional nullInputConditional = body._if(sizeVar.lt(JExpr.lit(0)));

        JBlock nullBody = nullInputConditional._then();

        nullBody.assign(outputVar, JExpr._null());

        JBlock nonNullBody = nullInputConditional._else();

        nonNullBody.assign(outputVar, JExpr._new(sparseArrayType).arg(sizeVar));

        JForLoop forLoop = nonNullBody._for();
        JVar nVar = forLoop.init(codeModel.INT, namer.generateName(codeModel.INT), JExpr.lit(0));
        forLoop.test(nVar.lt(sizeVar));
        forLoop.update(nVar.incr());
        JBlock readLoopBody = forLoop.body();

        JVar keyVar = readLoopBody.decl(codeModel.INT, namer.generateName(codeModel.INT), parcelParam.invoke("readInt"));

        ReadWriteGenerator generator = generators.getGenerator(componentType);

        JExpression readExpression = generator.generateReader(readLoopBody, parcelParam, componentType, generationUtil.ref(componentType), parcelableClass);

        readLoopBody.invoke(outputVar, "append").arg(keyVar).arg(readExpression);

        return outputVar;
    }

    @Override
    public void generateWriter(JBlock body, JVar parcel, JVar flags, ASTType type, JExpression getExpression) {

        JClass sparseArrayType = generationUtil.ref("android.util.SparseArray");
        ASTType componentType = astClassFactory.getType(Object.class);

        if(type.getGenericParameters().size() == 1){
            componentType = type.getGenericParameters().iterator().next();
            sparseArrayType = sparseArrayType.narrow(generationUtil.narrowRef(componentType));
        }

        JVar spareArrayVar = body.decl(sparseArrayType, namer.generateName(sparseArrayType), getExpression);

        JConditional nullConditional = body._if(spareArrayVar.eq(JExpr._null()));
        nullConditional._then().invoke(parcel, "writeInt").arg(JExpr.lit(-1));

        JBlock writeBody = nullConditional._else();

        writeBody.invoke(parcel, "writeInt").arg(spareArrayVar.invoke("size"));

        JForLoop forLoop = writeBody._for();
        JVar nVar = forLoop.init(codeModel.INT, namer.generateName(codeModel.INT), JExpr.lit(0));
        forLoop.test(nVar.lt(spareArrayVar.invoke("size")));
        forLoop.update(nVar.incr());
        JBlock readLoopBody = forLoop.body();

        readLoopBody.invoke(parcel, "writeInt").arg(spareArrayVar.invoke("keyAt").arg(nVar));

        ReadWriteGenerator generator = generators.getGenerator(componentType);

        generator.generateWriter(readLoopBody, parcel, flags, componentType, spareArrayVar.invoke("valueAt").arg(nVar));
    }
}
