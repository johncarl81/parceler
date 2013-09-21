package org.parceler.internal;

import com.sun.codemodel.JFieldVar;
import org.androidtransfuse.adapter.ASTType;

/**
 * @author John Ericksen
 */
public class WriteContext {

    private final JFieldVar wrapped;
    private final ASTType type;

    public WriteContext(JFieldVar wrapped, ASTType type) {
        this.wrapped = wrapped;
        this.type = type;
    }

    public JFieldVar getWrapped() {
        return wrapped;
    }

    public ASTType getType() {
        return type;
    }
}
