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
import org.parceler.ParcelConverter;

/**
 * @author John Ericksen
 */
public class ConverterWrapperReadWriteGenerator implements ReadWriteGenerator {

    private final JClass converter;

    public ConverterWrapperReadWriteGenerator(JClass converter) {
        this.converter = converter;
    }

    @Override
    public JExpression generateReader(JBlock body, JVar parcelParam, ASTType type, JClass returnJClassRef, JDefinedClass parcelableClass) {
        return JExpr._new(converter).invoke(ParcelConverter.CONVERT_FROM_PARCEL).arg(parcelParam);
    }

    @Override
    public void generateWriter(JBlock body, JExpression parcel, JVar flags, ASTType type, JExpression getExpression, JDefinedClass parcelableClass) {
        body.invoke(JExpr._new(converter), ParcelConverter.CONVERT_TO_PARCEL).arg(getExpression).arg(parcel);
    }
}
