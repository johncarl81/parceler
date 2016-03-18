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

    private final ClassGenerationUtil generationUtil;
    private final UniqueVariableNamer variableNamer;

    @Inject
    public LinkParcelReadWriteGenerator(ClassGenerationUtil generationUtil, UniqueVariableNamer variableNamer) {
        super("readParcelable", new String[]{ClassLoader.class.getName()}, "writeParcelable", new String[]{"android.os.Parcelable", int.class.getName()});
        this.generationUtil = generationUtil;
        this.variableNamer = variableNamer;
    }

    @Override
    public JExpression generateReader(JBlock body, JVar parcelParam, ASTType type, JClass returnJClassRef, JDefinedClass parcelableClass, JVar identity, JVar readIdentityMap) {
        PackageClass packageClass = ClassNamer.className(type).append(Parcels.IMPL_EXT).build();
        JType inputType = generationUtil.ref(type);
        JVar wrapped = body.decl(inputType, variableNamer.generateName(type), generationUtil.ref(packageClass).staticInvoke(READ_METHOD).arg(parcelParam).arg(readIdentityMap));
        return wrapped;
    }

    @Override
    public void generateWriter(JBlock body, JExpression parcel, JVar flags, ASTType type, JExpression getExpression, JDefinedClass parcelableClass, JVar writeIdentitySet) {
        PackageClass packageClass = ClassNamer.className(type).append(Parcels.IMPL_EXT).build();
        body.add(generationUtil.ref(packageClass).staticInvoke(WRITE_METHOD).arg(getExpression).arg(parcel).arg(flags).arg(writeIdentitySet));
    }
}
