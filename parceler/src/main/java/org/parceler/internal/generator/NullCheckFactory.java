package org.parceler.internal.generator;

import com.sun.codemodel.JCodeModel;
import org.androidtransfuse.adapter.classes.ASTClassFactory;
import org.androidtransfuse.gen.ClassGenerationUtil;
import org.androidtransfuse.gen.UniqueVariableNamer;
import org.parceler.internal.Generators;

import javax.inject.Inject;

/**
 * @author John Ericksen
 */
public class NullCheckFactory {

    private final JCodeModel codeModel;
    private final ClassGenerationUtil generationUtil;
    private final UniqueVariableNamer namer;
    private final ASTClassFactory astClassFactory;

    @Inject
    public NullCheckFactory(JCodeModel codeModel, ClassGenerationUtil generationUtil, UniqueVariableNamer namer, ASTClassFactory astClassFactory) {
        this.codeModel = codeModel;
        this.generationUtil = generationUtil;
        this.namer = namer;
        this.astClassFactory = astClassFactory;
    }

    public NullCheckReadWriteGenerator get(Class boxedType, Generators generators, Class unboxed){
        return new NullCheckReadWriteGenerator(astClassFactory.getType(boxedType), astClassFactory.getType(unboxed), codeModel, generationUtil, namer, generators);
    }

    public NullCheckReadWriteGenerator get(Class boxedType, ReadWriteGenerator generator){
        return new NullCheckReadWriteGenerator(astClassFactory.getType(boxedType), generator, codeModel, generationUtil, namer);
    }
}
