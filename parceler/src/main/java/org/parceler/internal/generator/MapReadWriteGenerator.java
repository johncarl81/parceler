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

import com.google.common.collect.UnmodifiableIterator;
import com.sun.codemodel.*;
import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.adapter.classes.ASTClassFactory;
import org.androidtransfuse.gen.ClassGenerationUtil;
import org.androidtransfuse.gen.UniqueVariableNamer;
import org.parceler.MapsUtil;
import org.parceler.internal.Generators;

import javax.inject.Inject;
import java.util.Map;

/**
* @author John Ericksen
*/
public class MapReadWriteGenerator extends ReadWriteGeneratorBase {

    private final ClassGenerationUtil generationUtil;
    private final UniqueVariableNamer namer;
    private final Generators generators;
    private final ASTClassFactory astClassFactory;
    private final JCodeModel codeModel;
    private final ASTType mapType;
    private final boolean mapInitialCapacityArgument;
    private final boolean initialCapacityLoadFactor;

    @Inject
    public MapReadWriteGenerator(ClassGenerationUtil generationUtil, UniqueVariableNamer namer, Generators generators, ASTClassFactory astClassFactory, JCodeModel codeModel, Class<? extends Map> mapType, boolean mapInitialCapacityArgument, boolean initialCapacityLoadFactor) {
        this(generationUtil, namer, generators, astClassFactory, codeModel, astClassFactory.getType(mapType), mapInitialCapacityArgument, initialCapacityLoadFactor);
    }

    public MapReadWriteGenerator(ClassGenerationUtil generationUtil, UniqueVariableNamer namer, Generators generators, ASTClassFactory astClassFactory, JCodeModel codeModel, ASTType mapType, boolean mapInitialCapacityArgument, boolean initialCapacityLoadFactor) {
        super("readHashMap", new Class[]{ClassLoader.class}, "writeMap", new Class[]{Map.class});
        this.generationUtil = generationUtil;
        this.generators = generators;
        this.namer = namer;
        this.astClassFactory = astClassFactory;
        this.codeModel = codeModel;
        this.mapType = mapType;
        this.mapInitialCapacityArgument = mapInitialCapacityArgument;
        this.initialCapacityLoadFactor = initialCapacityLoadFactor;
    }

    @Override
    public JExpression generateReader(JBlock body, JVar parcelParam, ASTType type, JClass returnJClassRef, JDefinedClass parcelableClass, JVar identity, JVar readIdentityMap) {

        JClass mapImplType = generationUtil.ref(mapType);

        ASTType keyComponentType = astClassFactory.getType(Object.class);
        ASTType valueComponentType = astClassFactory.getType(Object.class);
        JClass keyType = generationUtil.ref(Object.class);
        JClass valueType = generationUtil.ref(Object.class);

        if(type.getGenericParameters().size() == 2){
            UnmodifiableIterator<ASTType> iterator = type.getGenericParameters().iterator();
            keyComponentType = iterator.next();
            valueComponentType = iterator.next();
            keyType = generationUtil.narrowRef(keyComponentType);
            valueType = generationUtil.narrowRef(valueComponentType);
            mapImplType = mapImplType.narrow(keyType, valueType);
        }

        JVar sizeVar = body.decl(codeModel.INT, namer.generateName(codeModel.INT), parcelParam.invoke("readInt"));

        JVar outputVar = body.decl(mapImplType, namer.generateName(Map.class));

        JConditional nullInputConditional = body._if(sizeVar.lt(JExpr.lit(0)));

        JBlock nullBody = nullInputConditional._then();

        nullBody.assign(outputVar, JExpr._null());

        JBlock nonNullBody = nullInputConditional._else();

        JInvocation mapConstruction = JExpr._new(mapImplType);
        if(mapInitialCapacityArgument) {
            JExpression initialCapacityExpression;
            if(initialCapacityLoadFactor) {
                initialCapacityExpression = generationUtil.ref(MapsUtil.class).staticInvoke(MapsUtil.INITIAL_HASH_MAP_CAPACITY_METHOD).arg(sizeVar);
            }
            else{
                initialCapacityExpression = sizeVar;
            }
            mapConstruction = mapConstruction.arg(initialCapacityExpression);
        }

        nonNullBody.assign(outputVar, mapConstruction);

        JForLoop forLoop = nonNullBody._for();
        JVar nVar = forLoop.init(codeModel.INT, namer.generateName(codeModel.INT), JExpr.lit(0));
        forLoop.test(nVar.lt(sizeVar));
        forLoop.update(nVar.incr());
        JBlock readLoopBody = forLoop.body();

        ReadWriteGenerator keyGenerator = generators.getGenerator(keyComponentType);
        ReadWriteGenerator valueGenerator = generators.getGenerator(valueComponentType);

        JExpression readKeyExpression = keyGenerator.generateReader(readLoopBody, parcelParam, keyComponentType, generationUtil.ref(keyComponentType), parcelableClass, identity, readIdentityMap);
        JVar keyVar = readLoopBody.decl(keyType, namer.generateName(keyComponentType), readKeyExpression);

        JExpression readValueExpression = valueGenerator.generateReader(readLoopBody, parcelParam, valueComponentType, generationUtil.ref(valueComponentType), parcelableClass, identity, readIdentityMap);
        JVar valueVar = readLoopBody.decl(valueType, namer.generateName(valueComponentType), readValueExpression);

        readLoopBody.invoke(outputVar, "put").arg(keyVar).arg(valueVar);

        return outputVar;
    }

    @Override
    public void generateWriter(JBlock body, JExpression parcel, JVar flags, ASTType type, JExpression getExpression, JDefinedClass parcelableClass, JVar writeIdentitySet) {

        ASTType keyComponentType = astClassFactory.getType(Object.class);
        ASTType valueComponentType = astClassFactory.getType(Object.class);

        if(type.getGenericParameters().size() == 2){
            UnmodifiableIterator<ASTType> iterator = type.getGenericParameters().iterator();
            keyComponentType = iterator.next();
            valueComponentType = iterator.next();
        }
        JClass keyType = generationUtil.narrowRef(keyComponentType);
        JClass valueType = generationUtil.narrowRef(valueComponentType);

        JClass inputType = generationUtil.ref(Map.Entry.class).narrow(keyType, valueType);


        JConditional nullConditional = body._if(getExpression.eq(JExpr._null()));
        nullConditional._then().invoke(parcel, "writeInt").arg(JExpr.lit(-1));

        JBlock writeBody = nullConditional._else();

        writeBody.invoke(parcel, "writeInt").arg(getExpression.invoke("size"));

        JForEach forEach = writeBody.forEach(inputType, namer.generateName(inputType), ((JExpression)JExpr.cast(generationUtil.narrowRef(type), getExpression)).invoke("entrySet"));

        ReadWriteGenerator keyGenerator = generators.getGenerator(keyComponentType);
        ReadWriteGenerator valueGenerator = generators.getGenerator(valueComponentType);

        keyGenerator.generateWriter(forEach.body(), parcel, flags, keyComponentType, forEach.var().invoke("getKey"), parcelableClass, writeIdentitySet);
        valueGenerator.generateWriter(forEach.body(), parcel, flags, valueComponentType, forEach.var().invoke("getValue"), parcelableClass, writeIdentitySet);
    }
}
