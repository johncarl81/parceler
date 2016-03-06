package org.parceler.internal.generator;

import com.sun.codemodel.*;
import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.adapter.PackageClass;
import org.androidtransfuse.gen.ClassGenerationUtil;
import org.androidtransfuse.gen.ClassNamer;
import org.androidtransfuse.gen.UniqueVariableNamer;
import org.parceler.Parcels;

import javax.inject.Inject;

/**
 * @author John Ericksen
 */
public class LinkParcelReadWriteGenerator extends ReadWriteGeneratorBase {

    public static final String WRITE_METHOD = "write";
    public static final String READ_METHOD = "read";
    private static final String ANDROID_PARCEL = "android.os.Parcel";

    private final ClassGenerationUtil generationUtil;
    private final UniqueVariableNamer variableNamer;
    private final JCodeModel codeModel;

    @Inject
    public LinkParcelReadWriteGenerator(ClassGenerationUtil generationUtil, UniqueVariableNamer variableNamer, JCodeModel codeModel) {
        super("readParcelable", new String[]{ClassLoader.class.getName()}, "writeParcelable", new String[]{"android.os.Parcelable", int.class.getName()});
        this.generationUtil = generationUtil;
        this.variableNamer = variableNamer;
        this.codeModel = codeModel;
    }

    @Override
    public JExpression generateReader(JBlock body, JVar parcelParam, ASTType type, JClass returnJClassRef, JDefinedClass parcelableClass, JVar readIdentityMap) {
        PackageClass packageClass = ClassNamer.className(type).append(Parcels.IMPL_EXT).build();
        JType parcelType = generationUtil.ref(ANDROID_PARCEL);
        JType inputType = generationUtil.ref(type);

        JVar wrapped = body.decl(inputType, variableNamer.generateName(type));
        JConditional nullCondition = body._if(parcelParam.invoke("readInt").eq(JExpr.lit(-1)));
        nullCondition._then().assign(wrapped, JExpr._null());
        nullCondition._else().assign(wrapped, generationUtil.ref(packageClass).staticInvoke(READ_METHOD).arg(parcelParam).arg(readIdentityMap));

        return wrapped;
    }

    @Override
    public void generateWriter(JBlock body, JExpression parcel, JVar flags, ASTType type, JExpression getExpression, JDefinedClass parcelableClass, JVar writeIdentitySet) {
        PackageClass packageClass = ClassNamer.className(type).append(Parcels.IMPL_EXT).build();

        JConditional nullCondition = body._if(getExpression.eq(JExpr._null()));
        nullCondition._then().add(parcel.invoke("writeInt").arg(JExpr.lit(-1)));
        JBlock nonNullCondition = nullCondition._else();
        nonNullCondition.add(parcel.invoke("writeInt").arg(JExpr.lit(1)));
        nonNullCondition.add(generationUtil.ref(packageClass).staticInvoke(WRITE_METHOD).arg(getExpression).arg(parcel).arg(flags).arg(writeIdentitySet));
    }
}
