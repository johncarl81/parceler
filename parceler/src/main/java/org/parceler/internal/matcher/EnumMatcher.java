package org.parceler.internal.matcher;

import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.util.matcher.Matcher;

/**
 * @author John Ericksen
 */
public class EnumMatcher implements Matcher<ASTType> {
    @Override
    public boolean matches(ASTType input) {
        return input.isEnum();
    }
}
