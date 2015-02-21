package org.parceler.converter;

import java.util.Collection;
import java.util.LinkedList;

/**
 * @author John Ericksen
 */
public abstract class LinkedListParcelConverter<T> extends CollectionParcelConverter<T> {
    @Override
    public Collection<T> createCollection() {
        return new LinkedList<T>();
    }
}
