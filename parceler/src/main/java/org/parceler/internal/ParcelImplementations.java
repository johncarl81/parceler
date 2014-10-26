package org.parceler.internal;

import com.sun.codemodel.JDefinedClass;
import org.androidtransfuse.adapter.ASTType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author John Ericksen
 */
public class ParcelImplementations {

    private final JDefinedClass definedClass;
    private final boolean parcelsIndex;
    private final List<ASTType> extraImplementations = new ArrayList<ASTType>();

    public ParcelImplementations(JDefinedClass definedClass, boolean parcelsIndex) {
        this(definedClass, new ASTType[0], parcelsIndex);
    }

    public ParcelImplementations(JDefinedClass definedClass, ASTType[] extraImplementations, boolean parcelsIndex) {
        this.definedClass = definedClass;
        this.parcelsIndex = parcelsIndex;
        if(extraImplementations != null){
            this.extraImplementations.addAll(Arrays.asList(extraImplementations));
        }
    }

    public JDefinedClass getDefinedClass() {
        return definedClass;
    }

    public List<ASTType> getExtraImplementations() {
        return extraImplementations;
    }

    public boolean isParcelsIndex() {
        return parcelsIndex;
    }
}
