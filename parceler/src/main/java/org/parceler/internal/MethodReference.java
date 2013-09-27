package org.parceler.internal;

import org.androidtransfuse.adapter.ASTMethod;
import org.androidtransfuse.adapter.ASTType;

/**
 * @author John Ericksen
 */
public class MethodReference implements AccessibleReference {

    private final String name;
    private final ASTMethod method;
    private final ASTType type;

    public MethodReference(String name, ASTType type, ASTMethod method) {
        this.method = method;
        this.type = type;
        this.name = name;
    }

    public ASTMethod getMethod() {
        return method;
    }

    public ASTType getType() {
        return type;
    }

    public <T, R> R accept(ReferenceVisitor<T, R> visitor, T input){
        return visitor.visit(this, input);
    }

    public String getName() {
        return name;
    }
}
