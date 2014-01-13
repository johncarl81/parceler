package org.parceler.internal.generator;

import com.sun.codemodel.*;
import org.androidtransfuse.adapter.ASTType;

/**
* @author John Ericksen
*/
public interface ReadWriteGenerator {

    JExpression generateReader(JBlock body, JVar parcelParam, ASTType type, JClass returnJClassRef, JDefinedClass parcelableClass);

    void generateWriter(JBlock body, JVar parcel, JVar flags, ASTType type, JExpression getExpression);
}
