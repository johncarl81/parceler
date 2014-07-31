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
    public JExpression generateReader(JBlock body, JVar parcelParam, ASTType type, JClass returnJClassRef, JDefinedClass parcelableClass) {
        //target.programmingRelated = (parcel.readInt() == 1);
        return parcelParam.invoke(getReadMethod()).eq(JExpr.lit(1));
    }

    @Override
    public void generateWriter(JBlock body, JExpression parcel, JVar flags, ASTType type, JExpression getExpression, JDefinedClass parcelableClass) {
        //parcel.writeInt(skill$$0.programmingRelated ? 1 : 0);
        body.invoke(parcel, getWriteMethod()).arg(JOp.cond(getExpression, JExpr.lit(1), JExpr.lit(0)));
    }
}
