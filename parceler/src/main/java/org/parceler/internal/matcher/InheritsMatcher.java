package org.parceler.internal.matcher;

import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.util.matcher.Matcher;

/**
* @author John Ericksen
*/
public class InheritsMatcher implements Matcher<ASTType> {

    private final ASTType superType;

    public InheritsMatcher(ASTType superType) {
        this.superType = superType;
    }

    @Override
    public boolean matches(ASTType astType) {
        return astType.inheritsFrom(superType);
    }
}
