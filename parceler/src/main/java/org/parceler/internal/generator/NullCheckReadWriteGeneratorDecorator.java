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

/**
 * @author John Ericksen
 */
public class NullCheckReadWriteGeneratorDecorator extends NullCheckReadWriteGenerator {

    private final ReadWriteGenerator generator;

    public NullCheckReadWriteGeneratorDecorator(ASTType boxedType, JCodeModel codeModel, ClassGenerationUtil generationUtil, UniqueVariableNamer namer, ReadWriteGenerator generator) {
        super(boxedType, codeModel, generationUtil, namer);
        this.generator = generator;
    }

    @Override
    protected ReadWriteGenerator getGenerator() {
        return generator;
    }
}
