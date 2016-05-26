package org.parceler.internal.matcher;

import org.androidtransfuse.adapter.ASTStringType;
import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.util.matcher.Matcher;

/**
 * @author John Ericksen
 */
public class InheritsParcelableMatcher implements Matcher<ASTType> {

    private static final ASTType PARCELABLE_TYPE = new ASTStringType("android.os.Parcelable");

    @Override
    public boolean matches(ASTType input) {
        return input.inheritsFrom(PARCELABLE_TYPE);
    }
}
