package org.parceler.converter;

import java.util.LinkedHashSet;

/**
 * @author John Ericksen
 */
public abstract class LinkedHashSetParcelConverter<T> extends CollectionParcelConverter<T, LinkedHashSet<T>> {
    @Override
    public LinkedHashSet<T> createCollection() {
        return new LinkedHashSet<T>();
    }
}
