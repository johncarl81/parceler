package org.parceler.internal;

import com.sun.codemodel.JBlock;
import org.androidtransfuse.gen.InvocationBuilder;

import javax.inject.Inject;
import java.util.Collections;

/**
 * @author John Ericksen
 */
public class ReadReferenceVisitor implements ReferenceVisitor<ReadContext, Void> {

    private InvocationBuilder invocationBuilder;

    @Inject
    public ReadReferenceVisitor(InvocationBuilder invocationBuilder) {
        this.invocationBuilder = invocationBuilder;
    }

    @Override
    public Void visit(FieldReference fieldReference, ReadContext input) {
        JBlock body = input.getBody();

        body.add(invocationBuilder.buildFieldSet(
                fieldReference.getField().getAccessModifier(),
                input.getType(),
                input.getWrapped(),
                fieldReference.getType(),
                fieldReference.getField().getName(),
                input.getGetExpressionType(),
                input.getGetExpression()
        ));
        return null;
    }

    @Override
    public Void visit(MethodReference methodReference, ReadContext input) {
        JBlock body = input.getBody();

        body.add(invocationBuilder.buildMethodCall(
                methodReference.getMethod().getAccessModifier(),
                methodReference.getMethod().getReturnType(),
                methodReference.getMethod().getName(),
                Collections.singletonList(input.getGetExpression()),
                Collections.singletonList(input.getGetExpressionType()),
                input.getType(),
                input.getWrapped()));
        return null;
    }
}
