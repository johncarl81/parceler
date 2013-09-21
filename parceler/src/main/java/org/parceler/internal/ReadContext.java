package org.parceler.internal;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import org.androidtransfuse.adapter.ASTType;

/**
 * @author John Ericksen
 */
public class ReadContext {

    private final JBlock body;
    private final JFieldVar wrapped;
    private final JExpression getExpression;
    private final ASTType getExpressionType;

    public ReadContext(JBlock body, JFieldVar wrapped, ASTType getExpressionType, JExpression getExpression) {
        this.body = body;
        this.wrapped = wrapped;
        this.getExpression = getExpression;
        this.getExpressionType = getExpressionType;
    }

    public JBlock getBody() {
        return body;
    }

    public JFieldVar getWrapped() {
        return wrapped;
    }

    public JExpression getGetExpression() {
        return getExpression;
    }

    public ASTType getGetExpressionType() {
        return getExpressionType;
    }
}
