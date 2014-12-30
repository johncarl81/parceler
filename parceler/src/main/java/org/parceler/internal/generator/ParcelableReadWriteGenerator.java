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
import org.androidtransfuse.adapter.ASTType;

/**
* @author John Ericksen
*/
public class ParcelableReadWriteGenerator extends ReadWriteGeneratorBase {

    public ParcelableReadWriteGenerator(String readMethod, String writeMethod, String parcelableType) {
        super(readMethod, new String[]{ClassLoader.class.getName()}, writeMethod, new String[]{parcelableType, int.class.getName()});
    }

    @Override
    public JExpression generateReader(JBlock body, JVar parcelParam, ASTType type, JClass returnJClassRef, JDefinedClass parcelableClass) {
        return JExpr.cast(returnJClassRef, parcelParam.invoke(getReadMethod()).arg(parcelableClass.dotclass().invoke("getClassLoader")));
    }

    @Override
    public void generateWriter(JBlock body, JExpression parcel, JVar flags, ASTType type, JExpression getExpression, JDefinedClass parcelableClass) {
        body.invoke(parcel, getWriteMethod()).arg(getExpression).arg(flags);
    }
}
