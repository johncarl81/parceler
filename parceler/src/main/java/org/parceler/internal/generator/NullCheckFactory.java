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

import com.sun.codemodel.JCodeModel;
import org.androidtransfuse.adapter.classes.ASTClassFactory;
import org.androidtransfuse.gen.ClassGenerationUtil;
import org.androidtransfuse.gen.UniqueVariableNamer;
import org.parceler.internal.Generators;

import javax.inject.Inject;

/**
 * @author John Ericksen
 */
public class NullCheckFactory {

    private final JCodeModel codeModel;
    private final ClassGenerationUtil generationUtil;
    private final UniqueVariableNamer namer;
    private final ASTClassFactory astClassFactory;

    @Inject
    public NullCheckFactory(JCodeModel codeModel, ClassGenerationUtil generationUtil, UniqueVariableNamer namer, ASTClassFactory astClassFactory) {
        this.codeModel = codeModel;
        this.generationUtil = generationUtil;
        this.namer = namer;
        this.astClassFactory = astClassFactory;
    }

    public NullCheckReadWriteGenerator get(Class boxedType, Generators generators, Class unboxed){
        return new NullCheckReadWriteGenerator(astClassFactory.getType(boxedType), astClassFactory.getType(unboxed), codeModel, generationUtil, namer, generators);
    }

    public NullCheckReadWriteGenerator get(Class boxedType, ReadWriteGenerator generator){
        return new NullCheckReadWriteGenerator(astClassFactory.getType(boxedType), generator, codeModel, generationUtil, namer);
    }
}
