package org.parceler.converter;

import java.util.TreeSet;

/**
 * @author John Ericksen
 */
public abstract class TreeSetParcelConverter<T> extends CollectionParcelConverter<T, TreeSet<T>> {
    @Override
    public TreeSet<T> createCollection() {
        return new TreeSet<T>();
    }
}
