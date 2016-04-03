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

/**
 * @author John Ericksen
 */
public class BooleanEntryReadWriteGenerator extends ReadWriteGeneratorBase {
    public BooleanEntryReadWriteGenerator(JCodeModel codeModel) {
        super("readInt", new Class[0], "writeInt", new Class[]{int.class});
    }

    @Override
    public JExpression generateReader(JBlock body, JVar parcelParam, ASTType type, JClass returnJClassRef, JDefinedClass parcelableClass, JVar identity, JVar readIdentityMap) {
        //target.programmingRelated = (parcel.readInt() == 1);
        return parcelParam.invoke(getReadMethod()).eq(JExpr.lit(1));
    }

    @Override
    public void generateWriter(JBlock body, JExpression parcel, JVar flags, ASTType type, JExpression getExpression, JDefinedClass parcelableClass, JVar writeIdentitySet) {
        //parcel.writeInt(skill$$0.programmingRelated ? 1 : 0);
        body.invoke(parcel, getWriteMethod()).arg(JOp.cond(getExpression, JExpr.lit(1), JExpr.lit(0)));
    }
}
