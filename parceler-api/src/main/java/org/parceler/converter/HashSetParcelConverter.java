package org.parceler.converter;

import java.util.Collection;
import java.util.HashSet;

/**
 * @author John Ericksen
 */
public abstract class HashSetParcelConverter<T> extends CollectionParcelConverter<T> {
    @Override
    public Collection<T> createCollection() {
        return new HashSet<T>();
    }
}
