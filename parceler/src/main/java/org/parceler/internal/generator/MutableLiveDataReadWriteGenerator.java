package org.parceler.internal.generator;

import com.sun.codemodel.*;
import org.androidtransfuse.adapter.ASTStringType;
import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.gen.ClassGenerationUtil;
import org.androidtransfuse.gen.UniqueVariableNamer;
import org.parceler.internal.Generators;

import javax.inject.Inject;

public class MutableLiveDataReadWriteGenerator extends ReadWriteGeneratorBase {

    private static final ASTType TYPE = new ASTStringType("android.arch.lifecycle.MutableLiveData");

    private final Generators generators;
    private final ClassGenerationUtil generationUtil;
    private final UniqueVariableNamer namer;

    @Inject
    public MutableLiveDataReadWriteGenerator(Generators generators, ClassGenerationUtil generationUtil, UniqueVariableNamer namer) {
        super("readParcelable", new String[]{ClassLoader.class.getName()}, "writeParcelable", new String[]{"android.os.Parcelable", int.class.getName()});
        this.generators = generators;
        this.generationUtil = generationUtil;
        this.namer = namer;
    }

    @Override
    public JExpression generateReader(JBlock body, JVar parcelParam, ASTType type, JClass returnJClassRef, JDefinedClass parcelableClass, JVar identity, JVar readIdentityMap) {

        ASTType componentType = type.getGenericArgumentTypes().get(0);

        ReadWriteGenerator generator = generators.getGenerator(componentType);

        JExpression readExpression = generator.generateReader(body, parcelParam, componentType, generationUtil.ref(componentType), parcelableClass, identity, readIdentityMap);

        JClass mutableLiveDataType = generationUtil.ref(TYPE);

        JVar outputVar = body.decl(mutableLiveDataType, namer.generateName(mutableLiveDataType));

        body.assign(outputVar, JExpr._new(mutableLiveDataType));

        body.invoke(outputVar, "setValue").arg(readExpression);

        return outputVar;
    }

    @Override
    public void generateWriter(JBlock body, JExpression parcel, JVar flags, ASTType type, JExpression getExpression, JDefinedClass parcelableClass, JVar writeIdentitySet) {

        ASTType componentType = type.getGenericArgumentTypes().get(0);

        ReadWriteGenerator generator = generators.getGenerator(componentType);

        generator.generateWriter(body, parcel, flags, componentType, getExpression.invoke("getValue"), parcelableClass, writeIdentitySet);
    }
}
