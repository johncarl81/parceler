/**
 * Copyright 2013-2015 John Ericksen
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
import org.androidtransfuse.adapter.ASTArrayType;
import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.gen.ClassGenerationUtil;
import org.androidtransfuse.gen.UniqueVariableNamer;
import org.parceler.ParcelerRuntimeException;
import org.parceler.internal.Generators;

import javax.inject.Inject;

/**
 * @author John Ericksen
 */
public class ArrayReadWriteGenerator extends ReadWriteGeneratorBase {

    private final ClassGenerationUtil generationUtil;
    private final UniqueVariableNamer namer;
    private final Generators generators;
    private final JCodeModel codeModel;

    @Inject
    public ArrayReadWriteGenerator(ClassGenerationUtil generationUtil, UniqueVariableNamer namer, Generators generators, JCodeModel codeModel) {
        super("readArray", new Class[]{ClassLoader.class}, "writeArray", new Class[]{Object[].class});
        this.generationUtil = generationUtil;
        this.generators = generators;
        this.namer = namer;
        this.codeModel = codeModel;
    }

    @Override
    public JExpression generateReader(JBlock body, JVar parcelParam, ASTType type, JClass returnJClassRef, JDefinedClass parcelableClass) {

        if(!(type instanceof ASTArrayType)){
            throw new ParcelerRuntimeException("Input type not an array");
        }
        ASTArrayType arrayType = (ASTArrayType) type;

        ASTType componentType = arrayType.getComponentType();

        JClass componentRef = generationUtil.ref(componentType);

        JVar sizeVar = body.decl(codeModel.INT, namer.generateName(codeModel.INT), parcelParam.invoke("readInt"));

        JVar outputVar = body.decl(componentRef.array(), namer.generateName(componentRef));

        JConditional nullInputConditional = body._if(sizeVar.lt(JExpr.lit(0)));

        JBlock nullBody = nullInputConditional._then();

        nullBody.assign(outputVar, JExpr._null());

        JBlock nonNullBody = nullInputConditional._else();

        nonNullBody.assign(outputVar, JExpr.newArray(componentRef, sizeVar));

        JForLoop forLoop = nonNullBody._for();
        JVar nVar = forLoop.init(codeModel.INT, namer.generateName(codeModel.INT), JExpr.lit(0));
        forLoop.test(nVar.lt(sizeVar));
        forLoop.update(nVar.incr());
        JBlock readLoopBody = forLoop.body();

        ReadWriteGenerator generator = generators.getGenerator(componentType);

        JExpression readExpression = generator.generateReader(readLoopBody, parcelParam, componentType, generationUtil.ref(componentType), parcelableClass);

        readLoopBody.assign(outputVar.component(nVar), readExpression);

        return outputVar;
    }

    @Override
    public void generateWriter(JBlock body, JExpression parcel, JVar flags, ASTType type, JExpression getExpression, JDefinedClass parcelableClass) {

        if(!(type instanceof ASTArrayType)){
            throw new ParcelerRuntimeException("Input type not an array");
        }
        ASTArrayType arrayType = (ASTArrayType) type;

        ASTType componentType = arrayType.getComponentType();

        JClass componentRef = generationUtil.ref(componentType);


        JConditional nullConditional = body._if(getExpression.eq(JExpr._null()));
        nullConditional._then().invoke(parcel, "writeInt").arg(JExpr.lit(-1));

        JBlock writeBody = nullConditional._else();

        writeBody.invoke(parcel, "writeInt").arg(getExpression.ref("length"));
        JForEach forEach = writeBody.forEach(componentRef, namer.generateName(componentRef), getExpression);

        ReadWriteGenerator generator = generators.getGenerator(componentType);

        generator.generateWriter(forEach.body(), parcel, flags, componentType, forEach.var(), parcelableClass);
    }
}
