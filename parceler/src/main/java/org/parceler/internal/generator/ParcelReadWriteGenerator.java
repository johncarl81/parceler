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
import org.parceler.ParcelWrapper;
import org.parceler.internal.ParcelsGenerator;

/**
* @author John Ericksen
*/
public class ParcelReadWriteGenerator extends ReadWriteGeneratorBase {
    public static final String WRAP_METHOD = "wrap";

    private final ClassGenerationUtil generationUtil;

    public ParcelReadWriteGenerator(ClassGenerationUtil generationUtil) {
        super("readParcelable", new String[]{ClassLoader.class.getName()}, "writeParcelable", new String[]{"android.os.Parcelable", int.class.getName()});
        this.generationUtil = generationUtil;
    }

    @Override
    public JExpression generateReader(JBlock body, JVar parcelParam, ASTType type, JClass returnJClassRef, JDefinedClass parcelableClass) {
        JClass wrapperRef = generationUtil.ref(ParcelWrapper.class).narrow(generationUtil.ref(type));
        return ((JExpression) JExpr.cast(wrapperRef, parcelParam.invoke(getReadMethod())
                .arg(parcelableClass.dotclass().invoke("getClassLoader")))).invoke(ParcelWrapper.GET_PARCEL);
    }

    @Override
    public void generateWriter(JBlock body, JVar parcel, JVar flags, ASTType type, JExpression getExpression) {
        JInvocation wrappedParcel = generationUtil.ref(ParcelsGenerator.PARCELS_NAME).staticInvoke(WRAP_METHOD).arg(getExpression);
        body.invoke(parcel, getWriteMethod()).arg(wrappedParcel).arg(flags);
    }
}
