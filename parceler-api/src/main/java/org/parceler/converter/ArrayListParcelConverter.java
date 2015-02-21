package org.parceler.converter;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author John Ericksen
 */
public abstract class ArrayListParcelConverter<T> extends CollectionParcelConverter<T> {
    @Override
    public Collection<T> createCollection() {
        return new ArrayList<T>();
    }
}
