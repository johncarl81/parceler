package org.parceler.internal.generator;

import com.sun.codemodel.*;
import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.gen.ClassGenerationUtil;

import javax.inject.Inject;

/**
 * @author John Ericksen
 */
public class EnumReadWriteGenerator extends ReadWriteGeneratorBase {

    private final ClassGenerationUtil generationUtil;

    @Inject
    public EnumReadWriteGenerator(ClassGenerationUtil generationUtil) {
        super("readString", new Class[0], "writeString", new Class[]{String.class});
        this.generationUtil = generationUtil;
    }

    @Override
    public JExpression generateReader(JBlock body, JVar parcelParam, ASTType type, JClass returnJClassRef, JDefinedClass parcelableClass) {
        JClass enumRef = generationUtil.ref(Enum.class);
        JClass enumClassRef = generationUtil.ref(type);

        return JExpr.cast(returnJClassRef, enumRef.staticInvoke("valueOf").arg(enumClassRef.dotclass()).arg(parcelParam.invoke(getReadMethod())));
    }

    @Override
    public void generateWriter(JBlock body, JExpression parcel, JVar flags, ASTType type, JExpression getExpression, JDefinedClass parcelableClass) {
        body.invoke(parcel, getWriteMethod()).arg(getExpression.invoke("name"));
    }
}
