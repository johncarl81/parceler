package org.parceler.internal.generator;

import com.sun.codemodel.*;
import org.androidtransfuse.adapter.ASTType;

/**
* @author John Ericksen
*/
public class ClassloaderReadWriteGenerator extends ReadWriteGeneratorBase {

    public ClassloaderReadWriteGenerator(String readMethod, String writeMethod, Class writeMethodType) {
        super(readMethod, new String[]{ClassLoader.class.getName()}, writeMethod, new String[]{writeMethodType.getName()});
    }

    public ClassloaderReadWriteGenerator(String readMethod, String writeMethod, String writeMethodType) {
        super(readMethod, new String[]{ClassLoader.class.getName()}, writeMethod, new String[]{writeMethodType});
    }

    @Override
    public JExpression generateReader(JBlock body, JVar parcelParam, ASTType type, JClass returnJClassRef, JDefinedClass parcelableClass) {
        return parcelParam.invoke(getReadMethod()).arg(returnJClassRef.dotclass().invoke("getClassLoader"));
    }

    @Override
    public void generateWriter(JBlock body, JVar parcel, JVar flags, ASTType type, JExpression getExpression) {
        body.invoke(parcel, getWriteMethod()).arg(getExpression);
    }
}
