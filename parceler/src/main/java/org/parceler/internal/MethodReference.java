package org.parceler.internal;

import org.androidtransfuse.adapter.ASTMethod;
import org.androidtransfuse.adapter.ASTType;

/**
 * @author John Ericksen
 */
public class MethodReference implements Reference {

    private final ASTMethod method;
    private final ASTType type;

    public MethodReference(ASTType type, ASTMethod method) {
        this.method = method;
        this.type = type;
    }

    public ASTMethod getMethod() {
        return method;
    }

    @Override
    public ASTType getType() {
        return type;
    }

    public <T, R> R accept(ReferenceVisitor<T, R> visitor, T input){
        return visitor.visit(this, input);
    }
}
