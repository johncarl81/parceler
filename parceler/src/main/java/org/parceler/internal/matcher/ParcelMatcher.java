package org.parceler.internal.matcher;

import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.util.matcher.Matcher;
import org.parceler.internal.ExternalParcelRepository;

/**
* @author John Ericksen
*/
public class ParcelMatcher implements Matcher<ASTType> {

    private final ExternalParcelRepository externalParcelRepository;

    public ParcelMatcher(ExternalParcelRepository externalParcelRepository) {
        this.externalParcelRepository = externalParcelRepository;
    }

    @Override
    public boolean matches(ASTType type) {
        return type.isAnnotated(org.parceler.Parcel.class) || externalParcelRepository.contains(type);
    }
}
