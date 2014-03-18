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
    private final List<ASTType> extraImplementations = new ArrayList<ASTType>();

    public ParcelImplementations(JDefinedClass definedClass) {
        this(definedClass, new ASTType[0]);
    }

    public ParcelImplementations(JDefinedClass definedClass, ASTType[] extraImplementations) {
        this.definedClass = definedClass;
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
}
