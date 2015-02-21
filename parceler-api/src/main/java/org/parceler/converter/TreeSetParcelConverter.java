package org.parceler.converter;

import java.util.Collection;
import java.util.TreeSet;

/**
 * @author John Ericksen
 */
public abstract class TreeSetParcelConverter<T> extends CollectionParcelConverter<T> {
    @Override
    public Collection<T> createCollection() {
        return new TreeSet<T>();
    }
}
