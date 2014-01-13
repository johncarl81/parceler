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
        return JExpr.cast(returnJClassRef, parcelParam.invoke(getReadMethod()).arg(returnJClassRef.dotclass().invoke("getClassLoader")));
    }

    @Override
    public void generateWriter(JBlock body, JVar parcel, JVar flags, ASTType type, JExpression getExpression) {
        body.invoke(parcel, getWriteMethod()).arg(getExpression).arg(flags);
    }
}
