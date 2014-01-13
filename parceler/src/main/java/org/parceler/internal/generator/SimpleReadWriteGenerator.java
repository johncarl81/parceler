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
