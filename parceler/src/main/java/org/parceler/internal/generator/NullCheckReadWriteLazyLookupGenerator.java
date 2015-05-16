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

import com.sun.codemodel.JCodeModel;
import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.gen.ClassGenerationUtil;
import org.androidtransfuse.gen.UniqueVariableNamer;
import org.parceler.internal.Generators;

/**
 * @author John Ericksen
 */
public class NullCheckReadWriteLazyLookupGenerator extends NullCheckReadWriteGenerator {

    private final ASTType unboxedType;
    private final Generators generators;

    public NullCheckReadWriteLazyLookupGenerator(ASTType boxedType, JCodeModel codeModel, ClassGenerationUtil generationUtil, UniqueVariableNamer namer, Generators generators, ASTType unboxedType) {
        super(boxedType, codeModel, generationUtil, namer);
        this.unboxedType = unboxedType;
        this.generators = generators;
    }

    @Override
    protected ReadWriteGenerator getGenerator() {
        return generators.getGenerator(unboxedType);
    }
}
