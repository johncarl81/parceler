package org.parceler.internal;

import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import org.androidtransfuse.gen.InvocationBuilder;

import javax.inject.Inject;
import java.util.Collections;

/**
 * @author John Ericksen
 */
public class WriteReferenceVisitor implements ReferenceVisitor<WriteContext, JExpression> {

    private InvocationBuilder invocationBuilder;

    @Inject
    public WriteReferenceVisitor(InvocationBuilder invocationBuilder) {
        this.invocationBuilder = invocationBuilder;
    }

    @Override
    public JExpression visit(FieldReference fieldReference, WriteContext input) {
        JFieldVar wrapped = input.getWrapped();

        return invocationBuilder.buildFieldGet(
                input.getType(),
                input.getType(),
                wrapped,
                fieldReference.getField().getName(),
                fieldReference.getField().getAccessModifier());
    }

    @Override
    public JExpression visit(MethodReference methodReference, WriteContext input) {
        JFieldVar wrapped = input.getWrapped();

        return invocationBuilder.buildMethodCall(
                methodReference.getMethod().getAccessModifier(),
                methodReference.getType(),
                methodReference.getMethod().getName(),
                Collections.EMPTY_LIST,
                Collections.EMPTY_LIST,
                input.getType(),
                wrapped);
    }
}
