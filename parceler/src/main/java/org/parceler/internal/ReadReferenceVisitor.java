package org.parceler.internal;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;

/**
 * @author John Ericksen
 */
public class ReadReferenceVisitor implements ReferenceVisitor<ReadContext, Void> {

    @Override
    public Void visit(ConstructorReference constructorReference, ReadContext input) {
        return null;
    }

    @Override
    public Void visit(FieldReference fieldReference, ReadContext input) {
        return null;
    }

    @Override
    public Void visit(MethodReference methodReference, ReadContext input) {
        JBlock body = input.getBody();
        JFieldVar wrapped = input.getWrapped();
        JExpression getExpression = input.getGetExpression();

        body.invoke(wrapped, methodReference.getMethod().getName()).arg(getExpression);
        return null;
    }
}
