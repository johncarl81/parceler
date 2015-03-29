package org.parceler.converter;

import java.util.HashSet;

/**
 * @author John Ericksen
 */
public abstract class HashSetParcelConverter<T> extends CollectionParcelConverter<T, HashSet<T>> {
    @Override
    public HashSet<T> createCollection() {
        return new HashSet<T>();
    }
}
