package org.parceler.internal.generator;

import com.google.common.collect.UnmodifiableIterator;
import com.sun.codemodel.*;
import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.adapter.classes.ASTClassFactory;
import org.androidtransfuse.gen.ClassGenerationUtil;
import org.androidtransfuse.gen.UniqueVariableNamer;
import org.parceler.internal.Generators;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

/**
* @author John Ericksen
*/
public class MapReadWriteGenerator extends ReadWriteGeneratorBase {

    private final ClassGenerationUtil generationUtil;
    private final UniqueVariableNamer namer;
    private final Generators generators;
    private final ASTClassFactory astClassFactory;
    private final JCodeModel codeModel;

    @Inject
    public MapReadWriteGenerator(ClassGenerationUtil generationUtil, UniqueVariableNamer namer, Generators generators, ASTClassFactory astClassFactory, JCodeModel codeModel) {
        super("readHashMap", new Class[]{ClassLoader.class}, "writeMap", new Class[]{Map.class});
        this.generationUtil = generationUtil;
        this.generators = generators;
        this.namer = namer;
        this.astClassFactory = astClassFactory;
        this.codeModel = codeModel;
    }

    @Override
    public JExpression generateReader(JBlock body, JVar parcelParam, ASTType type, JClass returnJClassRef, JDefinedClass parcelableClass) {

        JClass outputType = generationUtil.ref(Map.class);
        JClass hashMapType = generationUtil.ref(HashMap.class);

        ASTType keyComponentType = astClassFactory.getType(Object.class);
        ASTType valueComponentType = astClassFactory.getType(Object.class);
        JClass keyType = generationUtil.ref(Object.class);
        JClass valueType = generationUtil.ref(Object.class);

        if(type.getGenericParameters().size() == 2){
            UnmodifiableIterator<ASTType> iterator = type.getGenericParameters().iterator();
            keyComponentType = iterator.next();
            valueComponentType = iterator.next();
            keyType = generationUtil.narrowRef(keyComponentType);
            valueType = generationUtil.narrowRef(valueComponentType);
            outputType = outputType.narrow(keyType, valueType);
            hashMapType = hashMapType.narrow(keyType, valueType);
        }

        JVar sizeVar = body.decl(codeModel.INT, namer.generateName(codeModel.INT), parcelParam.invoke("readInt"));

        JVar outputVar = body.decl(outputType, namer.generateName(Map.class));

        JConditional nullInputConditional = body._if(sizeVar.lt(JExpr.lit(0)));

        JBlock nullBody = nullInputConditional._then();

        nullBody.assign(outputVar, JExpr._null());

        JBlock nonNullBody = nullInputConditional._else();

        nonNullBody.assign(outputVar, JExpr._new(hashMapType));

        JForLoop forLoop = nonNullBody._for();
        JVar nVar = forLoop.init(codeModel.INT, namer.generateName(codeModel.INT), JExpr.lit(0));
        forLoop.test(nVar.lt(sizeVar));
        forLoop.update(nVar.incr());
        JBlock readLoopBody = forLoop.body();

        ReadWriteGenerator keyGenerator = generators.getGenerator(keyComponentType);
        ReadWriteGenerator valueGenerator = generators.getGenerator(valueComponentType);

        JExpression readKeyExpression = keyGenerator.generateReader(readLoopBody, parcelParam, keyComponentType, generationUtil.ref(keyComponentType), parcelableClass);
        JVar keyVar = readLoopBody.decl(keyType, namer.generateName(keyComponentType), readKeyExpression);

        JExpression readValueExpression = valueGenerator.generateReader(readLoopBody, parcelParam, valueComponentType, generationUtil.ref(valueComponentType), parcelableClass);
        JVar valueVar = readLoopBody.decl(valueType, namer.generateName(valueComponentType), readValueExpression);

        readLoopBody.invoke(outputVar, "put").arg(keyVar).arg(valueVar);

        return outputVar;
    }

    @Override
    public void generateWriter(JBlock body, JVar parcel, JVar flags, ASTType type, JExpression getExpression) {

        ASTType keyComponentType = astClassFactory.getType(Object.class);
        ASTType valueComponentType = astClassFactory.getType(Object.class);

        if(type.getGenericParameters().size() == 2){
            UnmodifiableIterator<ASTType> iterator = type.getGenericParameters().iterator();
            keyComponentType = iterator.next();
            valueComponentType = iterator.next();
        }
        JClass keyType = generationUtil.narrowRef(keyComponentType);
        JClass valueType = generationUtil.narrowRef(valueComponentType);

        JClass inputType = generationUtil.ref(Map.Entry.class).narrow(keyType, valueType);


        JConditional nullConditional = body._if(getExpression.eq(JExpr._null()));
        nullConditional._then().invoke(parcel, "writeInt").arg(JExpr.lit(-1));

        JBlock writeBody = nullConditional._else();

        writeBody.invoke(parcel, "writeInt").arg(getExpression.invoke("size"));

        JForEach forEach = writeBody.forEach(inputType, namer.generateName(inputType), getExpression.invoke("entrySet"));

        ReadWriteGenerator keyGenerator = generators.getGenerator(keyComponentType);
        ReadWriteGenerator valueGenerator = generators.getGenerator(valueComponentType);

        keyGenerator.generateWriter(forEach.body(), parcel, flags, keyComponentType, forEach.var().invoke("getKey"));
        valueGenerator.generateWriter(forEach.body(), parcel, flags, valueComponentType, forEach.var().invoke("getValue"));
    }
}
