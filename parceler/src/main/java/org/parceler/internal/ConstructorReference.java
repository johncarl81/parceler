package org.parceler.internal;

import org.androidtransfuse.adapter.ASTConstructor;
import org.androidtransfuse.adapter.ASTParameter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author John Ericksen
 */
public class ConstructorReference {

    private final ASTConstructor constructor;
    private Map<ASTParameter, AccessibleReference> writeReferences = new HashMap<ASTParameter, AccessibleReference>();

    public ConstructorReference(ASTConstructor constructor) {
        this.constructor = constructor;
    }

    public ASTConstructor getConstructor() {
        return constructor;
    }

    public void putReference(ASTParameter parameter, AccessibleReference reference){
        writeReferences.put(parameter, reference);
    }

    public AccessibleReference getWriteReference(ASTParameter parameter) {
        return writeReferences.get(parameter);
    }

    public Map<ASTParameter, AccessibleReference> getWriteReferences() {
        return writeReferences;
    }
}
