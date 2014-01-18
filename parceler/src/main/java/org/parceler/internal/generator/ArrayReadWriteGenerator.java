package org.parceler.internal.generator;

import com.sun.codemodel.*;
import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.adapter.classes.ASTClassFactory;
import org.androidtransfuse.gen.ClassGenerationUtil;
import org.androidtransfuse.gen.UniqueVariableNamer;
import org.parceler.internal.Generators;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * @author John Ericksen
 */
public class ArrayReadWriteGenerator extends ReadWriteGeneratorBase {

    private final ClassGenerationUtil generationUtil;
    private final UniqueVariableNamer namer;
    private final Generators generators;
    private final ASTClassFactory astClassFactory;
    private final JCodeModel codeModel;

    @Inject
    public ArrayReadWriteGenerator(ClassGenerationUtil generationUtil, UniqueVariableNamer namer, Generators generators, ASTClassFactory astClassFactory, JCodeModel codeModel) {
        super("readArray", new Class[]{ClassLoader.class}, "writeArray", new Class[]{Object[].class});
        this.generationUtil = generationUtil;
        this.generators = generators;
        this.namer = namer;
        this.astClassFactory = astClassFactory;
        this.codeModel = codeModel;
    }

    @Override
    public JExpression generateReader(JBlock body, JVar parcelParam, ASTType type, JClass returnJClassRef, JDefinedClass parcelableClass) {

        JClass outputType = generationUtil.ref(List.class);
        JClass arrayListType = generationUtil.ref(ArrayList.class);

        ASTType componentType = astClassFactory.getType(Object.class);

        if(type.getGenericParameters().size() == 1){
            componentType = type.getGenericParameters().iterator().next();
            outputType = outputType.narrow(generationUtil.narrowRef(componentType));
            arrayListType = arrayListType.narrow(generationUtil.narrowRef(componentType));
        }

        JVar sizeVar = body.decl(codeModel.INT, namer.generateName(codeModel.INT), parcelParam.invoke("readInt"));

        JVar outputVar = body.decl(outputType, namer.generateName(List.class));

        JConditional nullInputConditional = body._if(sizeVar.lt(JExpr.lit(0)));

        JBlock nullBody = nullInputConditional._then();

        nullBody.assign(outputVar, JExpr._null());

        JBlock nonNullBody = nullInputConditional._else();

        nonNullBody.assign(outputVar, JExpr._new(arrayListType));

        JForLoop forLoop = nonNullBody._for();
        JVar nVar = forLoop.init(codeModel.INT, namer.generateName(codeModel.INT), JExpr.lit(0));
        forLoop.test(nVar.lt(sizeVar));
        forLoop.update(nVar.incr());
        JBlock readLoopBody = forLoop.body();

        ReadWriteGenerator generator = generators.getGenerator(componentType);

        JExpression readExpression = generator.generateReader(readLoopBody, parcelParam, componentType, generationUtil.ref(componentType), parcelableClass);

        readLoopBody.invoke(outputVar, "add").arg(readExpression);

        return outputVar;
    }

    @Override
    public void generateWriter(JBlock body, JVar parcel, JVar flags, ASTType type, JExpression getExpression) {

        ASTType componentType = astClassFactory.getType(Object.class);

        if(type.getGenericParameters().size() == 1){
            componentType = type.getGenericParameters().iterator().next();
        }
        JClass inputType = generationUtil.narrowRef(componentType);


        JConditional nullConditional = body._if(getExpression.eq(JExpr._null()));
        nullConditional._then().invoke(parcel, "writeInt").arg(JExpr.lit(-1));

        JBlock writeBody = nullConditional._else();

        writeBody.invoke(parcel, "writeInt").arg(getExpression.invoke("size"));
        JForEach forEach = writeBody.forEach(inputType, namer.generateName(inputType), getExpression);

        ReadWriteGenerator generator = generators.getGenerator(componentType);

        generator.generateWriter(forEach.body(), parcel, flags, componentType, forEach.var());
    }
}
