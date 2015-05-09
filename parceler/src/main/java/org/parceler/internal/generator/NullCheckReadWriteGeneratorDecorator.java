package org.parceler.internal.generator;

import com.sun.codemodel.JCodeModel;
import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.gen.ClassGenerationUtil;
import org.androidtransfuse.gen.UniqueVariableNamer;

/**
 * @author John Ericksen
 */
public class NullCheckReadWriteGeneratorDecorator extends NullCheckReadWriteGenerator {

    private final ReadWriteGenerator generator;

    public NullCheckReadWriteGeneratorDecorator(ASTType boxedType, JCodeModel codeModel, ClassGenerationUtil generationUtil, UniqueVariableNamer namer, ReadWriteGenerator generator) {
        super(boxedType, codeModel, generationUtil, namer);
        this.generator = generator;
    }

    @Override
    protected ReadWriteGenerator getGenerator() {
        return generator;
    }
}
