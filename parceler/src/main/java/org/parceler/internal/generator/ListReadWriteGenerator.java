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
import org.androidtransfuse.adapter.classes.ASTClassFactory;
import org.androidtransfuse.gen.ClassGenerationUtil;
import org.androidtransfuse.gen.UniqueVariableNamer;
import org.parceler.internal.Generators;

import javax.inject.Inject;
import java.util.List;

/**
* @author John Ericksen
*/
public class ListReadWriteGenerator extends ReadWriteGeneratorBase {

    private final ClassGenerationUtil generationUtil;
    private final UniqueVariableNamer namer;
    private final Generators generators;
    private final ASTClassFactory astClassFactory;
    private final JCodeModel codeModel;
    private final ASTType listType;
    private final boolean listInitialCapacityArgument;

    @Inject
    public ListReadWriteGenerator(ClassGenerationUtil generationUtil, UniqueVariableNamer namer, Generators generators, ASTClassFactory astClassFactory, JCodeModel codeModel, Class<? extends List> listType, boolean listInitialCapacityArgument) {
        this(generationUtil, namer, generators, astClassFactory, codeModel, astClassFactory.getType(listType), listInitialCapacityArgument);
    }

    public ListReadWriteGenerator(ClassGenerationUtil generationUtil, UniqueVariableNamer namer, Generators generators, ASTClassFactory astClassFactory, JCodeModel codeModel, ASTType listType, boolean listInitialCapacityArgument) {
        super("readArrayList", new Class[]{ClassLoader.class}, "writeList", new Class[]{List.class});
        this.generationUtil = generationUtil;
        this.generators = generators;
        this.namer = namer;
        this.astClassFactory = astClassFactory;
        this.codeModel = codeModel;
        this.listType = listType;
        this.listInitialCapacityArgument = listInitialCapacityArgument;
    }

    @Override
    public JExpression generateReader(JBlock body, JVar parcelParam, ASTType type, JClass returnJClassRef, JDefinedClass parcelableClass, JVar identity, JVar readIdentityMap) {

        JClass arrayListType = generationUtil.ref(listType);

        ASTType componentType = astClassFactory.getType(Object.class);

        if(type.getGenericArgumentTypes().size() == 1){
            componentType = type.getGenericArgumentTypes().iterator().next();
            arrayListType = arrayListType.narrow(generationUtil.narrowRef(componentType));
        }

        JVar sizeVar = body.decl(codeModel.INT, namer.generateName(codeModel.INT), parcelParam.invoke("readInt"));

        JVar outputVar = body.decl(arrayListType, namer.generateName(List.class));

        JConditional nullInputConditional = body._if(sizeVar.lt(JExpr.lit(0)));

        JBlock nullBody = nullInputConditional._then();

        nullBody.assign(outputVar, JExpr._null());

        JBlock nonNullBody = nullInputConditional._else();

        JInvocation listCreation = JExpr._new(arrayListType);

        if(listInitialCapacityArgument){
            listCreation = listCreation.arg(sizeVar);
        }

        nonNullBody.assign(outputVar, listCreation);

        JForLoop forLoop = nonNullBody._for();
        JVar nVar = forLoop.init(codeModel.INT, namer.generateName(codeModel.INT), JExpr.lit(0));
        forLoop.test(nVar.lt(sizeVar));
        forLoop.update(nVar.incr());
        JBlock readLoopBody = forLoop.body();

        ReadWriteGenerator generator = generators.getGenerator(componentType);

        JExpression readExpression = generator.generateReader(readLoopBody, parcelParam, componentType, generationUtil.ref(componentType), parcelableClass, identity, readIdentityMap);

        readLoopBody.invoke(outputVar, "add").arg(readExpression);

        return outputVar;
    }

    @Override
    public void generateWriter(JBlock body, JExpression parcel, JVar flags, ASTType type, JExpression getExpression, JDefinedClass parcelableClass, JVar writeIdentitySet) {

        ASTType componentType = astClassFactory.getType(Object.class);

        if(type.getGenericArgumentTypes().size() == 1){
            componentType = type.getGenericArgumentTypes().iterator().next();
        }
        JClass inputType = generationUtil.narrowRef(componentType);


        JConditional nullConditional = body._if(getExpression.eq(JExpr._null()));
        nullConditional._then().invoke(parcel, "writeInt").arg(JExpr.lit(-1));

        JBlock writeBody = nullConditional._else();

        writeBody.invoke(parcel, "writeInt").arg(getExpression.invoke("size"));
        JForEach forEach = writeBody.forEach(inputType, namer.generateName(inputType), getExpression);

        ReadWriteGenerator generator = generators.getGenerator(componentType);

        generator.generateWriter(forEach.body(), parcel, flags, componentType, forEach.var(), parcelableClass, writeIdentitySet);
    }
}
