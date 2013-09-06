package org.parceler.internal;

import com.sun.codemodel.JFieldVar;

/**
 * @author John Ericksen
 */
public class WriteContext {

    private JFieldVar wrapped;

    public WriteContext(JFieldVar wrapped) {
        this.wrapped = wrapped;
    }

    public JFieldVar getWrapped() {
        return wrapped;
    }
}
