package org.parceler.internal.matcher;

import org.androidtransfuse.adapter.ASTField;
import org.androidtransfuse.adapter.ASTStringType;
import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.util.matcher.Matcher;

/**
 * @author John Ericksen
 */
public class ParcelableImplementationMatcher implements Matcher<ASTType> {

    private static final ASTType CREATOR_TYPE = new ASTStringType("android.os.Parcelable.Creator");
    private static final ASTType PARCELABLE_TYPE = new ASTStringType("android.os.Parcelable");

    @Override
    public boolean matches(ASTType input) {
        return input.inheritsFrom(PARCELABLE_TYPE) && isCreatorFieldImplemented(input);
    }

    private boolean isCreatorFieldImplemented(ASTType type) {
        ASTType creatorType = getTypeForField(type, "CREATOR");
        return creatorType != null && creatorType.extendsFrom(CREATOR_TYPE);
    }

    private ASTType getTypeForField(ASTType type, String name) {
        for (ASTField field : type.getFields()) {
            if(name.equals(field.getName())){
                return field.getASTType();
            }
        }
        return null;
    }
}
