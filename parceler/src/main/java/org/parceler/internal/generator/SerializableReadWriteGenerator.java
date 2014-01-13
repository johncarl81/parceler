package org.parceler.internal.generator;

import com.sun.codemodel.*;
import org.androidtransfuse.adapter.ASTType;

import java.io.Serializable;

/**
* @author John Ericksen
*/
public class SerializableReadWriteGenerator extends ReadWriteGeneratorBase {

    public SerializableReadWriteGenerator() {
        super("readSerializable", new Class[0], "writeSerializable", new Class[]{Serializable.class});
    }

    @Override
    public JExpression generateReader(JBlock body, JVar parcelParam, ASTType type, JClass returnJClassRef, JDefinedClass parcelableClass) {
        return JExpr.cast(returnJClassRef, parcelParam.invoke(getReadMethod()));
    }

    @Override
    public void generateWriter(JBlock body, JVar parcel, JVar flags, ASTType type, JExpression getExpression) {
        body.invoke(parcel, getWriteMethod()).arg(getExpression);
    }
}
