package org.parceler.internal;

import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;

/**
 * @author John Ericksen
 */
public class WriteReferenceVisitor implements ReferenceVisitor<WriteContext, JExpression> {

    @Override
    public JExpression visit(FieldReference fieldReference, WriteContext input) {
        JFieldVar wrapped = input.getWrapped();

        return wrapped.ref(fieldReference.getField().getName());
    }

    @Override
    public JExpression visit(MethodReference methodReference, WriteContext input) {
        JFieldVar wrapped = input.getWrapped();

        return wrapped.invoke(methodReference.getMethod().getName());
    }
}
