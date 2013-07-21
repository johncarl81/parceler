package org.parceler.internal;

import org.androidtransfuse.adapter.ASTType;

import javax.inject.Singleton;
import java.util.HashSet;
import java.util.Set;

/**
 * @author John Ericksen
 */
@Singleton
public class ExternalParcelRepository {

    private Set<ASTType> externalParcels = new HashSet<ASTType>();

    public void add(ASTType parcelType){
        externalParcels.add(parcelType);
    }

    public boolean contains(ASTType returnType) {
        return externalParcels.contains(returnType);
    }
}
