package org.parceler.converter;

import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * @author John Ericksen
 */
public abstract class LinkedHashSetParcelConverter<T> extends CollectionParcelConverter<T> {
    @Override
    public Collection<T> createCollection() {
        return new LinkedHashSet<T>();
    }
}
