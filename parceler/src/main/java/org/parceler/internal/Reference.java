package org.parceler.internal;

import android.R;
import org.androidtransfuse.adapter.ASTType;

/**
 * @author John Ericksen
 */
public interface Reference {

    <T, R> R accept(ReferenceVisitor<T, R> visitor, T input);

    ASTType getType();

}
