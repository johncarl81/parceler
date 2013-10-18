package org.parceler.internal;

import org.androidtransfuse.adapter.ASTType;

/**
 * @author John Ericksen
 */
public interface AccessibleReference extends Reference{

    ASTType getType();

    String getName();

    <T, R> R accept(ReferenceVisitor<T, R> visitor, T input);
}
