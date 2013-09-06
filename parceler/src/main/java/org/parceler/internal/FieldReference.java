package org.parceler.internal;

import org.androidtransfuse.adapter.ASTField;
import org.androidtransfuse.adapter.ASTType;

/**
 * @author John Ericksen
 */
public class FieldReference implements Reference {

    private final ASTField field;

    public FieldReference(ASTField field) {
        this.field = field;
    }

    public ASTField getField() {
        return field;
    }

    public <T, R> R accept(ReferenceVisitor<T, R> visitor, T input){
        return visitor.visit(this, input);
    }

    @Override
    public ASTType getType() {
        return field.getASTType();
    }
}
