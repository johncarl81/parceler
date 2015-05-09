package org.parceler.internal.generator;

import com.sun.codemodel.JCodeModel;
import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.gen.ClassGenerationUtil;
import org.androidtransfuse.gen.UniqueVariableNamer;
import org.parceler.internal.Generators;

/**
 * @author John Ericksen
 */
public class NullCheckReadWriteLazyLookupGenerator extends NullCheckReadWriteGenerator {

    private final ASTType unboxedType;
    private final Generators generators;

    public NullCheckReadWriteLazyLookupGenerator(ASTType boxedType, JCodeModel codeModel, ClassGenerationUtil generationUtil, UniqueVariableNamer namer, Generators generators, ASTType unboxedType) {
        super(boxedType, codeModel, generationUtil, namer);
        this.unboxedType = unboxedType;
        this.generators = generators;
    }

    @Override
    protected ReadWriteGenerator getGenerator() {
        return generators.getGenerator(unboxedType);
    }
}
