package org.parceler.internal;

import org.androidtransfuse.adapter.ASTConstructor;
import org.androidtransfuse.adapter.ASTField;
import org.androidtransfuse.adapter.ASTType;

/**
 * @author John Ericksen
 */
public class ConstructorReference implements Reference {

    private final ASTConstructor constructor;

    public ConstructorReference(ASTConstructor constructor) {
        this.constructor = constructor;
    }

    public ASTConstructor getConstructor() {
        return constructor;
    }

    public <T, R> R accept(ReferenceVisitor<T, R> visitor, T input){
        return visitor.visit(this, input);
    }

    @Override
    public ASTType getType() {
        return null;
    }
}
