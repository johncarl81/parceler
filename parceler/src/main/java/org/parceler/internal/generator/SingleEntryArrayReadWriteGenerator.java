package org.parceler.internal.generator;

import com.sun.codemodel.*;
import org.androidtransfuse.adapter.ASTType;

import java.lang.reflect.Array;

/**
 * @author John Ericksen
 */
public class SingleEntryArrayReadWriteGenerator extends ReadWriteGeneratorBase {

    private final JCodeModel codeModel;
    private final Class writeMethodParam;

    public SingleEntryArrayReadWriteGenerator(String readMethod, String writeMethod, Class writeMethodParam, JCodeModel codeModel) {
        super(readMethod, new Class[0], writeMethod, new Class[]{Array.newInstance(writeMethodParam, 0).getClass()});
        this.codeModel = codeModel;
        this.writeMethodParam = writeMethodParam;
    }

    @Override
    public JExpression generateReader(JBlock body, JVar parcelParam, ASTType type, JClass returnJClassRef, JDefinedClass parcelableClass) {
        return JExpr.component(parcelParam.invoke(getReadMethod()), JExpr.lit(0));
    }

    @Override
    public void generateWriter(JBlock body, JVar parcel, JVar flags, ASTType type, JExpression getExpression) {
        body.invoke(parcel, getWriteMethod()).arg(JExpr.newArray(codeModel._ref(writeMethodParam)).add(getExpression));
    }
}
