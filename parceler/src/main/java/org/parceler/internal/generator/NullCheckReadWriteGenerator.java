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
import org.parceler.internal.Generators;

/**
* @author John Ericksen
*/
public class NullCheckReadWriteGenerator implements ReadWriteGenerator {

    private final JCodeModel codeModel;
    private final ClassGenerationUtil generationUtil;
    private final UniqueVariableNamer namer;
    private final ASTType boxedType;
    private ASTType unboxedType;
    private Generators generators;
    private ReadWriteGenerator generator;

    public NullCheckReadWriteGenerator(ASTType boxedType, ASTType unboxedType, JCodeModel codeModel, ClassGenerationUtil generationUtil, UniqueVariableNamer namer, Generators generators) {
        this.boxedType = boxedType;
        this.unboxedType = unboxedType;
        this.codeModel = codeModel;
        this.generationUtil = generationUtil;
        this.namer = namer;
        this.generators = generators;
    }

    public NullCheckReadWriteGenerator(ASTType boxedType, ReadWriteGenerator generator, JCodeModel codeModel, ClassGenerationUtil generationUtil, UniqueVariableNamer namer) {
        this.boxedType = boxedType;
        this.codeModel = codeModel;
        this.generationUtil = generationUtil;
        this.namer = namer;
        this.generator = generator;
    }

    @Override
    public JExpression generateReader(JBlock body, JVar parcelParam, ASTType type, JClass returnJClassRef, JDefinedClass parcelableClass) {

        JVar sizeVar = body.decl(codeModel.INT, namer.generateName(codeModel.INT), parcelParam.invoke("readInt"));

        JVar value = body.decl(generationUtil.ref(boxedType), namer.generateName(boxedType));

        JConditional nullInputConditional = body._if(sizeVar.lt(JExpr.lit(0)));

        JBlock nullBody = nullInputConditional._then();

        nullBody.assign(value, JExpr._null());

        JBlock nonNullBody = nullInputConditional._else();

        nonNullBody.assign(value, getGenerator().generateReader(body, parcelParam, type, returnJClassRef, parcelableClass));

        return value;
    }

    @Override
    public void generateWriter(JBlock body, JVar parcel, JVar flags, ASTType type, JExpression getExpression) {

        JConditional nullConditional = body._if(getExpression.eq(JExpr._null()));
        nullConditional._then().invoke(parcel, "writeInt").arg(JExpr.lit(-1));

        JBlock writeBody = nullConditional._else();
        writeBody.invoke(parcel, "writeInt").arg(JExpr.lit(1));

        getGenerator().generateWriter(writeBody, parcel, flags, type, getExpression);
    }

    private ReadWriteGenerator getGenerator(){
        if(generator != null){
            return generator;
        }
        return generators.getGenerator(unboxedType);
    }
}
