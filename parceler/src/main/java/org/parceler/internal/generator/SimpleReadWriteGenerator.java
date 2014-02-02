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

/**
* @author John Ericksen
*/
public class SimpleReadWriteGenerator extends ReadWriteGeneratorBase {

    public SimpleReadWriteGenerator(String readMethod, String[] readMethodParams, String writeMethod, String[] writeMethodParams) {
        super(readMethod, readMethodParams, writeMethod, writeMethodParams);
    }

    @Override
    public JExpression generateReader(JBlock body, JVar parcelParam, ASTType type, JClass returnJClassRef, JDefinedClass parcelableClass) {
        return parcelParam.invoke(getReadMethod());
    }

    @Override
    public void generateWriter(JBlock body, JVar parcel, JVar flags, ASTType type, JExpression getExpression) {
        body.invoke(parcel, getWriteMethod()).arg(getExpression);
    }
}
