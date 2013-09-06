package org.parceler.internal;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;

/**
 * @author John Ericksen
 */
public class ReadContext {

    private final JBlock body;
    private final JFieldVar wrapped;
    private final JExpression getExpression;

    public ReadContext(JBlock body, JFieldVar wrapped, JExpression getExpression) {
        this.body = body;
        this.wrapped = wrapped;
        this.getExpression = getExpression;
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
}
