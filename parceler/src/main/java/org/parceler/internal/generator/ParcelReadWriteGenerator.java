package org.parceler.internal.generator;

import com.sun.codemodel.*;
import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.gen.ClassGenerationUtil;
import org.parceler.ParcelWrapper;
import org.parceler.internal.ParcelsGenerator;
import org.parceler.internal.ParcelableGenerator;

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
