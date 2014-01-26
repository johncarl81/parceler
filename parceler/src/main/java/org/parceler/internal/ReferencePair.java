package org.parceler.internal;

import org.androidtransfuse.adapter.ASTType;

/**
 * @author John Ericksen
 */
public class ReferencePair<T extends Reference> {

    private final String name;
    private final T setter;
    private final AccessibleReference accessor;
    private final ASTType converter;

    public ReferencePair(String name, T setter, AccessibleReference accessor, ASTType converter) {
        this.name = name;
        this.setter = setter;
        this.accessor = accessor;
        this.converter = converter;
    }

    public String getName() {
        return name;
    }

    public T getSetter() {
        return setter;
    }

    public AccessibleReference getAccessor() {
        return accessor;
    }

    public ASTType getConverter() {
        return converter;
    }
}
