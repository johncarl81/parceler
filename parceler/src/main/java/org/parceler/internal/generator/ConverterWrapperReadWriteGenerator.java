package org.parceler.internal.generator;

import com.sun.codemodel.*;
import org.androidtransfuse.adapter.ASTType;
import org.parceler.ParcelConverter;

/**
 * @author John Ericksen
 */
public class ConverterWrapperReadWriteGenerator implements ReadWriteGenerator {

    private final JClass converter;

    public ConverterWrapperReadWriteGenerator(JClass converter) {
        this.converter = converter;
    }

    @Override
    public JExpression generateReader(JBlock body, JVar parcelParam, ASTType type, JClass returnJClassRef, JDefinedClass parcelableClass) {
        return JExpr._new(converter).invoke(ParcelConverter.CONVERT_FROM_PARCEL).arg(parcelParam);
    }

    @Override
    public void generateWriter(JBlock body, JVar parcel, JVar flags, ASTType type, JExpression getExpression) {
        body.invoke(JExpr._new(converter), ParcelConverter.CONVERT_TO_PARCEL).arg(getExpression).arg(parcel);
    }
}
