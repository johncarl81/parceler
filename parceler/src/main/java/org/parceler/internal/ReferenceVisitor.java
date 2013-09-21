package org.parceler.internal;

/**
 * @author John Ericksen
 */
public interface ReferenceVisitor<T, R> {

    R visit(FieldReference fieldReference, T input);

    R visit(MethodReference methodReference, T input);
}
